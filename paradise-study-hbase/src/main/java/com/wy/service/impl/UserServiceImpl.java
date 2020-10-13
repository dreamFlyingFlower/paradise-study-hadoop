package com.wy.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wy.common.HOSConst;
import com.wy.crypto.CryptoUtils;
import com.wy.dao.UserMapper;
import com.wy.model.Token;
import com.wy.model.User;
import com.wy.service.AuthService;
import com.wy.service.UserService;

/**
 * @apiNote 用户业务接口实现类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:10:00
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

	// 设置token的过期时间是一百年以后
	private final long LONG_REFRESH_TIME = 4670409600000L;

	@Autowired
	private UserMapper userInfoMapper;

	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@Override
	public boolean addUser(User userInfo) {
		userInfoMapper.addUser(userInfo);
		// 给用户设置token
		Token tokenInfo = new Token();
		tokenInfo.setToken(userInfo.getUserId());
		tokenInfo.setActive(true);
		tokenInfo.setExpireTime(7);
		tokenInfo.setRefreshTime(new Date(LONG_REFRESH_TIME));
		tokenInfo.setCreator(HOSConst.SYSTEM_USER);
		tokenInfo.setCreateTime(new Date());
		authService.addToken(tokenInfo);
		return true;
	}

	@Override
	public boolean updateUserInfo(String userId, String password, String detail) {
		// 这里需要判断密码是否为空，当为空时则不更改密码
		userInfoMapper.updateUserInfo(userId, StringUtils.isEmpty(password) ? null : CryptoUtils.MD5(password), detail);
		return true;
	}

	@Override
	public boolean deleteUser(String userId) {
		userInfoMapper.deleteUser(userId);
		// 删除用户的token
		authService.deleteToken(userId);
		authService.deleteAuthByToken(userId);
		return true;
	}

	@Override
	public User getUserInfo(String userId) {
		return userInfoMapper.getUserInfo(userId);
	}

	@Override
	public User getUserInfoByName(String userName) {
		return userInfoMapper.getUserInfoByName(userName);
	}

	@Override
	public User checkPassword(String userName, String password) {
		return userInfoMapper.checkPassword(userName, password);
	}
}