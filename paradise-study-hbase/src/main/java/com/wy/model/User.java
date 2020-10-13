package com.wy.model;

import java.util.Date;

import com.wy.crypto.CryptoUtils;
import com.wy.enums.SystemRole;

import lombok.Data;

/**
 * @apiNote 用户实体类
 * @author ParadiseWY
 * @date 2020年2月9日 下午10:45:45
 */
@Data
public class User {

	private String userId;

	private String username;

	private String password;

	private String detail;

	private SystemRole systemRole;

	private Date createTime;

	public User() {
	}

	public User(String username, String password, String detail, SystemRole systemRole) {
		this.userId = CryptoUtils.UUID();
		this.username = username;
		this.password = CryptoUtils.MD5(password);
		this.detail = detail;
		this.systemRole = systemRole;
		this.createTime = new Date();
	}
}