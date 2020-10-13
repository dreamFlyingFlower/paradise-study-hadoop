package com.wy.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.wy.model.User;

/**
 * @apiNote 用户mapper
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:07:12
 */
@Mapper
public interface UserMapper {

	/**
	 * 添加用户
	 * @param userInfo
	 */
	void addUser(@Param("user") User user);

	/**
	 * 删除用户
	 * @param userId
	 * @return
	 */
	int deleteUser(@Param("userId") String userId);

	/**
	 * 修改用户信息
	 * @param userId
	 * @param password
	 * @param detail
	 * @return
	 */
	int updateUserInfo(@Param("userId") String userId, @Param("password") String password,
			@Param("detail") String detail);

	/**
	 * 通过userID获取用户
	 * @param userId
	 * @return
	 */
	@ResultMap("UserInfoResultMap")
	User getUserInfo(@Param("userId") String userId);

	/**
	 * 通过userName获取用户
	 * @param userName
	 * @return
	 */
	@ResultMap("UserInfoResultMap")
	User getUserInfoByName(@Param("username") String username);

	/**
	 * 检查password
	 * @param userName
	 * @param password
	 * @return
	 */
	@ResultMap("UserInfoResultMap")
	User checkPassword(@Param("username") String userName, @Param("password") String password);
}