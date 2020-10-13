package com.wy.util;

import com.wy.model.User;

/**
 * @apiNote 用户信息获取
 * @author ParadiseWY
 * @date 2020年2月10日 下午12:10:59
 */
public class ContextUtil {

	public static final String SESSION_KEY="USER_TOKEN";
	
	private static ThreadLocal<User> threadLocal = new ThreadLocal<>();
	
	public static User getCurrentUser() {
		return threadLocal.get();
	}
	
	public static void setUser(User user) {
		threadLocal.set(user);
	}
	
	static void clear() {
		threadLocal.remove();
	}
}