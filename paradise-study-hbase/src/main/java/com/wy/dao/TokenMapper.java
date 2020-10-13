package com.wy.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.wy.model.Token;

/**
 * @apiNote token数据层接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:16:49
 */
@Mapper
public interface TokenMapper {

	public void addToken(@Param("token") Token tokenInfo);

	public void deleteToken(@Param("token") String token);

	public void updateToken(@Param("token") String token, @Param("expireTime") int expireTime,
			@Param("isActive") int isActive);

	public void refreshToken(@Param("token") String token, @Param("refreshTime") Date expireTime);

	@ResultMap("TokenInfoResultMap")
	public Token getTokenInfo(@Param("token") String token);

	@ResultMap("TokenInfoResultMap")
	public List<Token> getTokenInfos(@Param("creator") String creator);
}