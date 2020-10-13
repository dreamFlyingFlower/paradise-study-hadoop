package com.wy.service;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.wy.common.HOSObject;
import com.wy.common.HOSObjectSummary;
import com.wy.common.ObjectListResult;

/**
 * @apiNote 存储业务接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:26:22
 */
public interface HOSStoreService {

	public void createBucketStore(String bucket) throws Exception;

	public void deleteBucketStore(String bucket) throws Exception;

	public void createSeqTable() throws Exception;

	public void put(String bucket, String key, ByteBuffer content, long length, String mediaType,
			Map<String, String> properties) throws Exception;

	public HOSObjectSummary getSummary(String bucket, String key) throws Exception;

	public List<HOSObjectSummary> getSummaries(String bucket, String key, String startKey, String endKey)
			throws Exception;

	public List<HOSObjectSummary> list(String bucket, String startKey, String endKey) throws Exception;

	public ObjectListResult listDir(String bucket, String dir, String startKey, int maxCount) throws Exception;

	public ObjectListResult listDirByPrefix(String bucket, String dir, String startKey, String prefix, int maxCount)
			throws Exception;

	public HOSObject getObject(String bucket, String key) throws Exception;

	public void deleteObject(String bucket, String key) throws Exception;
}