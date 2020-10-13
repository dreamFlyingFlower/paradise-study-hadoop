package com.wy.service;

import java.util.List;

import com.wy.model.Bucket;
import com.wy.model.User;

/**
 * @apiNote bucker接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:20:38
 */
public interface BucketService {

	public boolean addBucket(User userInfo, String bucketName, String detail);

	public boolean deleteBucket(String bucketName);

	public boolean updateBucket(String bucketName, String detail);

	public Bucket getBucketById(String bucketId);

	public Bucket getBucketByName(String bucketName);

	public List<Bucket> getBucketsByCreator(String creator);

	public List<Bucket> getUserAuthorizedBuckets(String token);
}