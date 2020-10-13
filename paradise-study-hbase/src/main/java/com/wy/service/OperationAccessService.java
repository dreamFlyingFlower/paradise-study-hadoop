package com.wy.service;

import com.wy.enums.SystemRole;
import com.wy.model.User;

/**
 * @apiNote 角色权限控制接口
 * @author ParadiseWY
 * @date 2020年2月10日 下午1:48:32
 */
public interface OperationAccessService {

	/**
	 * 检查是否登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户信息
	 */
	User checkLogin(String username, String password);

	/**
	 * 权限比较
	 * @param role1 权限1
	 * @param role2 权限2
	 * @return 权限1是否有对权限2操作的权限
	 */
	boolean checkSystemRole(SystemRole role1, SystemRole role2);

	/**
	 * 是否对另外的用户有操作权限
	 * @param role 操作者的权限
	 * @param userId 被操作的用户编号
	 * @return 是否有权限去操作用户
	 */
	boolean checkSystemRole(SystemRole role, String userId);

	boolean checkTokenOwner(String username, String token);

	boolean checkBucketOwner(String username, String bucket);

	boolean checkPermission(String userId, String bucket);
}