package com.wy.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.dao.AuthMapper;
import com.wy.dao.TokenMapper;
import com.wy.model.Auth;
import com.wy.model.Token;
import com.wy.service.AuthService;

/**
 * @apiNote auth业务接口实现类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:20:06
 */
@Service("authServiceImpl")
@Transactional
public class AuthServiceImpl implements AuthService {

	@Autowired
	private TokenMapper tokenInfoMapper;

	@Autowired
	private AuthMapper authMapper;

	@Override
	public boolean addAuth(Auth serviceAuth) {
		authMapper.addAuth(serviceAuth);
		return true;
	}

	@Override
	public boolean deleteAuth(String bucket, String token) {
		authMapper.deleteAuth(bucket, token);
		return true;
	}

	@Override
	public boolean deleteAuthByToken(String token) {
		authMapper.deleteAuthByToken(token);
		return true;
	}

	@Override
	public boolean deleteAuthByBucket(String bucket) {
		authMapper.deleteAuthByBucket(bucket);
		return true;
	}

	@Override
	public Auth getAuth(String bucket, String token) {
		return authMapper.getAuth(bucket, token);
	}

	@Override
	public boolean addToken(Token tokenInfo) {
		tokenInfoMapper.addToken(tokenInfo);
		return true;
	}

	@Override
	public boolean deleteToken(String token) {
		tokenInfoMapper.deleteToken(token);
		// 删除auth
		authMapper.deleteAuthByToken(token);
		return true;
	}

	@Override
	public boolean updateToken(String token, int expireTime, boolean isActive) {
		tokenInfoMapper.updateToken(token, expireTime, isActive ? 1 : 0);
		return true;
	}

	@Override
	public boolean refreshToken(String token) {
		tokenInfoMapper.refreshToken(token, new Date());
		return false;
	}

	@Override
	public boolean checkToken(String token) {
		Token tokenInfo = tokenInfoMapper.getTokenInfo(token);
		if (tokenInfo == null) {
			return false;
		}
		if (tokenInfo.isActive()) {
			// 判断token是否过期
			Date now = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(tokenInfo.getRefreshTime());
			calendar.add(Calendar.DATE, tokenInfo.getExpireTime());
			return now.before(calendar.getTime());
		}
		return false;
	}

	@Override
	public Token getTokenInfo(String token) {
		return tokenInfoMapper.getTokenInfo(token);
	}

	@Override
	public List<Token> getTokenInfos(String creator) {
		return tokenInfoMapper.getTokenInfos(creator);
	}
}