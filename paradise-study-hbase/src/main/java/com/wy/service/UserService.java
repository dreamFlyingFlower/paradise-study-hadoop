package com.wy.service;

import com.wy.model.User;

/**
 * @apiNote 用户接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:09:35
 */
public interface UserService {

	public boolean addUser(User userInfo);

	public boolean updateUserInfo(String userId, String password, String detail);

	public boolean deleteUser(String userId);

	public User getUserInfo(String userId);

	public User getUserInfoByName(String username);

	public User checkPassword(String username, String password);
}