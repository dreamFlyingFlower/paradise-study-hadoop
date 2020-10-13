package com.wy.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.wy.model.Auth;

/**
 * @apiNote token的mapper接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:14:52
 */
@Mapper
public interface AuthMapper {

	public void addAuth(@Param("auth") Auth auth);

	public void deleteAuth(@Param("bucket") String bucket, @Param("token") String token);

	public void deleteAuthByToken(@Param("token") String token);

	public void deleteAuthByBucket(@Param("bucket") String bucket);

	public void updateAuth(@Param("auth") Auth auth);

	@ResultMap("ServiceAuthResultMap")
	public Auth getAuth(@Param("bucket") String bucket, @Param("token") String token);
}