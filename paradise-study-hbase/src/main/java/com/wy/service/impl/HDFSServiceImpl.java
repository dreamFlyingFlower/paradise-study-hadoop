package com.wy.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.stereotype.Service;

import com.wy.common.ErrorCode;
import com.wy.config.HOSConfig;
import com.wy.result.ResultException;
import com.wy.service.HDFSService;

import lombok.extern.slf4j.Slf4j;

/**
 * @apiNote HBase业务实现类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:42:02
 */
@Slf4j
@Service
public class HDFSServiceImpl implements HDFSService {

	// hadoop文件系统
	private FileSystem fileSystem;

	// hbase默认的缓存大小
	private long defaultBlockSize = 128 * 1024 * 1024;

	// 当一个文件小于BlockSize的一半的时候，手动将其大小置为BlockSize的一半
	private long initBlockSize = defaultBlockSize / 2;

	public HDFSServiceImpl() throws Exception {
		// 获取HDFS相关的配置信息
		HOSConfig hosConfiguration = HOSConfig.getConfiguration();
		// hadoop.conf.dir即存放各种-site.xml的路径,配置文件路径
		String confDir = hosConfiguration.getString("hadoop.conf.dir");
		// 获得hdfs的地址,hdfs://s100:9000
		String hdfsUri = hosConfiguration.getString("hadoop.uri");
		// 通过配置,获取一个FileSystem实例
		Configuration configuration = new Configuration();
		configuration.addResource(new Path(confDir + "/hdfs-site.xml"));
		configuration.addResource(new Path(confDir + "/core-site.xml"));
		// 通过uri和配置获取FileSystem的实例
		this.fileSystem = FileSystem.get(new URI(hdfsUri), configuration);
	}

	@Override
	public void saveFile(String dir, String name, InputStream inputStream, long length, short replication)
			throws Exception {
		// 1.判断dir是否存在,不存在则新建
		Path dirPath = new Path(dir);
		try {
			if (!this.fileSystem.exists(dirPath)) {
				// 如果目录不存在,则创建目录,给默认权限
				boolean mkdirsResult = this.fileSystem.mkdirs(dirPath, FsPermission.getDirDefault());
				log.info("create dir " + dirPath);
				if (!mkdirsResult) {
					throw new ResultException(ErrorCode.ERROR_HDFS, "Create Dir " + dirPath + " Error");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 2. 保存文件
		Path path = new Path(dirPath + File.separator + name);
		// 设置文件块大小
		long blockSize = length <= initBlockSize ? initBlockSize : defaultBlockSize;
		// 创建文件输出流
		try (FSDataOutputStream outputStream = this.fileSystem.create(path, true, 512 * 1024, replication,
				blockSize);) {
			// 设置文件权限
			this.fileSystem.setPermission(path, FsPermission.getFileDefault());
			// 开始写入文件
			byte[] buffer = new byte[512 * 1024];
			int len = -1;
			while ((len = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			inputStream.close();
		}
	}

	@Override
	public void deleteFile(String dir, String name) throws Exception {
		this.fileSystem.delete(new Path(dir + File.separator + name), false);
	}

	@Override
	public InputStream openFile(String dir, String name) throws Exception {
		return this.fileSystem.open(new Path(dir + File.separator + name));
	}

	@Override
	public void makeDir(String dir) throws Exception {
		this.fileSystem.mkdirs(new Path(dir));
	}

	@Override
	public void deleteDir(String dir) throws Exception {
		this.fileSystem.delete(new Path(dir), true);
	}
}