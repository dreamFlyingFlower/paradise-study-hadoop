package com.wy.service;

import java.util.List;

import com.wy.model.Auth;
import com.wy.model.Token;

/**
 * @apiNote auth业务接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:19:35
 */
public interface AuthService {

	public boolean addAuth(Auth serviceAuth);

	public boolean deleteAuth(String bucket, String token);

	public boolean deleteAuthByToken(String token);

	public boolean deleteAuthByBucket(String bucket);

	public Auth getAuth(String bucket, String token);

	public boolean addToken(Token tokenInfo);

	public boolean deleteToken(String token);

	public boolean updateToken(String token, int expireTime, boolean isActive);

	public boolean refreshToken(String token);

	public boolean checkToken(String token);

	public Token getTokenInfo(String token);

	public List<Token> getTokenInfos(String creator);
}