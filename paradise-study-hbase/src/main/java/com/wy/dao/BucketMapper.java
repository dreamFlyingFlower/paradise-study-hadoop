package com.wy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.wy.model.Bucket;

/**
 * @apiNote bucket数据接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:24:42
 */
@Mapper
public interface BucketMapper {

	public void addBucket(@Param("bucket") Bucket bucketModel);

	public void deleteBucket(@Param("bucketName") String bucketName);

	public void updateBucket(@Param("bucketName") String bucketName, @Param("detail") String detail);

	@ResultMap("BucketResultMap")
	public Bucket getBucket(@Param("bucketId") String bucketId);

	@ResultMap("BucketResultMap")
	public Bucket getBucketByName(@Param("bucketName") String bucketName);

	@ResultMap("BucketResultMap")
	public List<Bucket> getBucketsByCreator(@Param("creator") String creator);

	@ResultMap("BucketResultMap")
	public List<Bucket> getUserAuthorizedBuckets(@Param("token") String token);
}