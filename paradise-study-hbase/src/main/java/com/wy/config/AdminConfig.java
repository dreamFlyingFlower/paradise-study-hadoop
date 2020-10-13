package com.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wy.common.HOSConst;
import com.wy.enums.SystemRole;
import com.wy.model.User;
import com.wy.service.HOSStoreService;
import com.wy.service.UserService;

/**
 * @apiNote 当程序启动时新建超级管理员帐号
 * @author ParadiseWY
 * @date 2020年2月10日 上午11:50:48
 */
public class AdminConfig implements ApplicationRunner {

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("hsStore")
	private HOSStoreService hosStore;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = userService.getUserInfoByName(HOSConst.SYSTEM_USER);
		if (user == null) {
			user = new User(HOSConst.SYSTEM_USER, "superadmin", "this is a superadmin", SystemRole.SUPERADMIN);
			userService.addUser(user);
		}
		hosStore.createSeqTable();
	}
}