package com.wy.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.dao.BucketMapper;
import com.wy.model.Auth;
import com.wy.model.Bucket;
import com.wy.model.User;
import com.wy.service.AuthService;
import com.wy.service.BucketService;

/**
 * @apiNote bucket业务接口实现类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:20:58
 */
@Service("bucketServiceImpl")
@Transactional
public class BucketServiceImpl implements BucketService {

	@Autowired
	private BucketMapper bucketModelMapper;

	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@Override
	public boolean addBucket(User userInfo, String bucketName, String detail) {
		Bucket bucketModel = new Bucket(bucketName, userInfo.getUsername(), detail);
		bucketModelMapper.addBucket(bucketModel);
		// TODO add auth for bucket and user
		Auth serviceAuth = new Auth();
		serviceAuth.setAuthTime(new Date());
		serviceAuth.setTargetToken(userInfo.getUserId());
		serviceAuth.setBucketName(bucketName);
		authService.addAuth(serviceAuth);
		return true;
	}

	@Override
	public boolean deleteBucket(String bucketName) {
		bucketModelMapper.deleteBucket(bucketName);
		// TODO delete auth for bucket
		authService.deleteAuthByBucket(bucketName);
		return true;
	}

	@Override
	public boolean updateBucket(String bucketName, String detail) {
		bucketModelMapper.updateBucket(bucketName, detail);
		return true;
	}

	@Override
	public Bucket getBucketById(String bucketId) {
		return bucketModelMapper.getBucket(bucketId);
	}

	@Override
	public Bucket getBucketByName(String bucketName) {
		return bucketModelMapper.getBucketByName(bucketName);
	}

	@Override
	public List<Bucket> getBucketsByCreator(String creator) {
		return bucketModelMapper.getBucketsByCreator(creator);
	}

	@Override
	public List<Bucket> getUserAuthorizedBuckets(String token) {
		return bucketModelMapper.getUserAuthorizedBuckets(token);
	}
}