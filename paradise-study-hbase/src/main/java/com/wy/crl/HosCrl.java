package com.wy.crl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;
import com.wy.common.HOSConst;
import com.wy.common.HOSObject;
import com.wy.common.HOSObjectSummary;
import com.wy.common.ObjectListResult;
import com.wy.enums.SystemRole;
import com.wy.model.User;
import com.wy.result.Result;
import com.wy.service.AuthService;
import com.wy.service.BucketService;
import com.wy.service.HOSStoreService;
import com.wy.service.OperationAccessService;
import com.wy.service.UserService;
import com.wy.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @apiNote hos对外接口API
 * @author ParadiseWY
 * @date 2020年2月10日 下午3:45:05
 */
@Slf4j
@RestController
@RequestMapping("hos")
public class HosCrl {

	// 内存临时存储的文件大小
	private static final long MAX_FILE_IN_MEMORY = 2 * 1024 * 1024;

	// 每次读取的缓存内存大小
	private static final int readBufferSize = 32 * 1024;

	// 临时存储目录
	private static String TMP_DIR = System.getProperty("user.dir") + File.separator + "tmp";

	@Autowired
	private OperationAccessService operationAccessService;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@Autowired
	@Qualifier("bucketServiceImpl")
	private BucketService bucketService;

	@Autowired
	@Qualifier("hosStore")
	private HOSStoreService hosStoreService;

	public HosCrl() {
		File file = new File(TMP_DIR);
		file.mkdirs();
	}

	@PostMapping("addBucket")
	public Result<?> addBucket(@RequestParam String bucket,
			@RequestParam(required = false, defaultValue = "") String detail) {
		User currentUser = ContextUtil.getCurrentUser();
		if (!currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
			bucketService.addBucket(currentUser, bucket, detail);
			try {
				hosStoreService.createBucketStore(bucket);
				return Result.ok();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Result.error();
	}

	@GetMapping("deleteBucket")
	public Result<?> deleteUser(@RequestParam String bucket) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkBucketOwner(currentUser.getUsername(), bucket)) {
			try {
				hosStoreService.deleteBucketStore(bucket);
			} catch (Exception e) {
				e.printStackTrace();
			}
			bucketService.deleteBucket(bucket);
			return Result.ok();
		}
		return Result.error();
	}

	@GetMapping("getBucket")
	public Result<?> getBucket(@RequestParam String bucket) {
		User currentUser = ContextUtil.getCurrentUser();
		return Result.ok(bucketService.getBucketsByCreator(currentUser.getUserId()));
	}

	/**
	 * 上传文件或创建目录
	 * @param bucket 需要创建或上传的文件名称
	 * @param key 关键字
	 * @param mediaType 文件类型
	 * @param file 文件
	 * @param request 请求
	 * @param response 响应
	 * @return 结果
	 */
	@PostMapping("upload")
	public Object putObject(@RequestParam("bucket") String bucket, @RequestParam("key") String key,
			@RequestParam(value = "mediaType", required = false) String mediaType,
			@RequestParam(value = "content", required = false) MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return "Permission denied";
		}
		if (!key.startsWith(File.separator)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().write("object key must start with /");
		}

		Enumeration<String> headNames = request.getHeaderNames();
		Map<String, String> attrs = new HashMap<>();
		String contentEncoding = request.getHeader("content-encoding");
		if (contentEncoding != null) {
			attrs.put("content-encoding", contentEncoding);
		}
		while (headNames.hasMoreElements()) {
			String header = headNames.nextElement();
			if (header.startsWith(HOSConst.COMMON_ATTR_PREFIX)) {
				attrs.put(header.replace(HOSConst.COMMON_ATTR_PREFIX, ""), request.getHeader(header));
			}
		}
		ByteBuffer buffer = null;
		File distFile = null;
		try {
			// put dir object
			if (key.endsWith(File.separator)) {
				if (file != null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					file.getInputStream().close();
					return null;
				}
				hosStoreService.put(bucket, key, null, 0, mediaType, attrs);
				response.setStatus(HttpStatus.OK.value());
				return "success";
			}
			if (file == null || file.getSize() == 0) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.getWriter().write("object content could not be empty");
				return "object content could not be empty";
			}

			if (file != null) {
				if (file.getSize() > MAX_FILE_IN_MEMORY) {
					distFile = new File(TMP_DIR + File.separator + UUID.randomUUID().toString());
					file.transferTo(distFile);
					file.getInputStream().close();
					buffer = new FileInputStream(distFile).getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
							file.getSize());
				} else {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					org.apache.commons.io.IOUtils.copy(file.getInputStream(), outputStream);
					buffer = ByteBuffer.wrap(outputStream.toByteArray());
					file.getInputStream().close();
				}
			}
			hosStoreService.put(bucket, key, buffer, file.getSize(), mediaType, attrs);
			return "success";
		} catch (Exception e) {
			log.error(e.toString());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.getWriter().write("server error");
			return "server error";
		} finally {
			if (buffer != null) {
				buffer.clear();
			}
			if (file != null) {
				try {
					file.getInputStream().close();
				} catch (Exception e) {
					// nothing to do
				}
			}
			if (distFile != null) {
				distFile.delete();
			}
		}
	}

	// 列出目录下的文件
	@RequestMapping(value = "object/list", method = RequestMethod.GET)
	public ObjectListResult listObject(@RequestParam("bucket") String bucket, @RequestParam("startKey") String startKey,
			@RequestParam("endKey") String endKey, HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return null;
		}
		if (startKey.compareTo(endKey) > 0) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		}
		ObjectListResult result = new ObjectListResult();
		List<HOSObjectSummary> summaryList = hosStoreService.list(bucket, startKey, endKey);
		result.setBucket(bucket);
		if (summaryList.size() > 0) {
			result.setMaxKey(summaryList.get(summaryList.size() - 1).getKey());
			result.setMinKey(summaryList.get(0).getKey());
		}
		result.setObjectCount(summaryList.size());
		result.setObjectSummaryList(summaryList);
		return result;
	}

	@RequestMapping(value = "object/info", method = RequestMethod.GET)
	public HOSObjectSummary getSummary(String bucket, String key, HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return null;
		}
		HOSObjectSummary summary = hosStoreService.getSummary(bucket, key);
		if (summary == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return summary;
	}

	@RequestMapping(value = "object/list/prefix", method = RequestMethod.GET)
	public ObjectListResult listObjectByPrefix(@RequestParam("bucket") String bucket, @RequestParam("dir") String dir,
			@RequestParam("prefix") String prefix,
			@RequestParam(value = "startKey", required = false, defaultValue = "") String start,
			HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return null;
		}
		if (!dir.startsWith(File.separator) || !dir.endsWith(File.separator)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().write("dir must start with / and end with /");
			return null;
		}
		if ("".equals(start) || start.equals(File.separator)) {
			start = null;
		}
		if (start != null) {
			List<String> segs = StreamSupport
					.stream(Splitter.on(File.separator).trimResults().omitEmptyStrings().split(start).spliterator(),
							false)
					.collect(Collectors.toList());
			start = segs.get(segs.size() - 1);
		}
		ObjectListResult result = this.hosStoreService.listDirByPrefix(bucket, dir, prefix, start, 100);
		return result;
	}

	@RequestMapping(value = "object/list/dir", method = RequestMethod.GET)
	public ObjectListResult listObjectByDir(@RequestParam("bucket") String bucket, @RequestParam("dir") String dir,
			@RequestParam(value = "startKey", required = false, defaultValue = "") String start,
			HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return null;
		}
		if (!dir.startsWith(File.separator) || !dir.endsWith(File.separator)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().write("dir must start with / and end with /");
			return null;
		}
		if ("".equals(start) || start.equals(File.separator)) {
			start = null;
		}
		if (start != null) {
			List<String> segs = StreamSupport
					.stream(Splitter.on(File.separator).trimResults().omitEmptyStrings().split(start).spliterator(),
							false)
					.collect(Collectors.toList());
			start = segs.get(segs.size() - 1);
		}

		ObjectListResult result = this.hosStoreService.listDir(bucket, dir, start, 100);
		return result;
	}

	// 删除文件
	@RequestMapping(value = "object", method = RequestMethod.DELETE)
	public Object deleteObject(@RequestParam("bucket") String bucket, @RequestParam("key") String key)
			throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			return "PERMISSION DENIED";
		}
		this.hosStoreService.deleteObject(bucket, key);
		return "success";
	}

	// 下载文件
	@RequestMapping(value = "object/content", method = RequestMethod.GET)
	public void getObject(@RequestParam("bucket") String bucket, @RequestParam("key") String key,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		User currentUser = ContextUtil.getCurrentUser();
		if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Permission denied");
			return;
		}
		HOSObject object = this.hosStoreService.getObject(bucket, key);
		if (object == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		response.setHeader(HOSConst.COMMON_OBJ_BUCKET, bucket);
		response.setHeader(HOSConst.COMMON_OBJ_KEY, key);
		response.setHeader(HOSConst.RESPONSE_OBJ_LENGTH, "" + object.getMetaData().getLength());
		String iflastModify = request.getHeader("If-Modified-Since");
		String lastModify = object.getMetaData().getLastModifyTime() + "";
		response.setHeader("Last-Modified", lastModify);
		String contentEncoding = object.getMetaData().getContentEncoding();
		if (contentEncoding != null) {
			response.setHeader("content-encoding", contentEncoding);
		}
		if (iflastModify != null && iflastModify.equals(lastModify)) {
			response.setStatus(HttpStatus.NOT_MODIFIED.value());
			return;
		}
		response.setHeader(HOSConst.COMMON_OBJ_BUCKET, object.getMetaData().getBucket());
		response.setContentType(object.getMetaData().getMediaType());
		OutputStream outputStream = response.getOutputStream();
		InputStream inputStream = object.getContent();
		try {
			byte[] buffer = new byte[readBufferSize];
			int len = -1;
			while ((len = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			response.flushBuffer();
		} finally {
			inputStream.close();
			outputStream.close();
		}
	}
}