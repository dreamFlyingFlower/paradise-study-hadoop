package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.wy.crypto.CryptoUtils;
import com.wy.enums.SystemRole;
import com.wy.model.Auth;
import com.wy.model.Bucket;
import com.wy.model.Token;
import com.wy.model.User;
import com.wy.service.AuthService;
import com.wy.service.BucketService;
import com.wy.service.OperationAccessService;
import com.wy.service.UserService;

/**
 * @apiNote 权限控制实现类
 * @author ParadiseWY
 * @date 2020年2月10日 下午2:05:09
 */
@Service
public class OperationAccessServiceImpl implements OperationAccessService{
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("bucketServiceImpl")
	private BucketService bucketService;
	
	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@Override
	public User checkLogin(String username, String password) {
		User user = userService.getUserInfoByName(username);
		if (user == null) {
			return null;
		}
		return user.getPassword().equals(CryptoUtils.MD5(password)) ? user : null;
	}

	@Override
	public boolean checkSystemRole(SystemRole role1, SystemRole role2) {
		if (role1.equals(SystemRole.SUPERADMIN)) {
			return true;
		}
		return role1.equals(SystemRole.ADMIN) && role2.equals(SystemRole.USER);
	}

	@Override
	public boolean checkSystemRole(SystemRole role, String userId) {
		if (role.equals(SystemRole.SUPERADMIN)) {
			return true;
		}
		User user = userService.getUserInfo(userId);
		return role.equals(SystemRole.ADMIN) && user.getSystemRole().equals(SystemRole.USER);
	}

	@Override
	public boolean checkTokenOwner(String username, String token) {
		Token tokenInfo = authService.getTokenInfo(token);
		return username.equals(tokenInfo.getCreator());
	}

	@Override
	public boolean checkBucketOwner(String username, String bucket) {
		Bucket bucketByName = bucketService.getBucketByName(bucket);
		return username.equals(bucketByName.getCreator());
	}

	@Override
	public boolean checkPermission(String token, String bucket) {
		if (authService.checkToken(token)) {
			Auth auth = authService.getAuth(bucket, token);
			if (auth != null) {
				return true;
			}
		}
		return false;
	}
}