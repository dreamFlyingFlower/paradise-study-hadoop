package com.wy.model;

import java.util.Date;

import com.wy.crypto.CryptoUtils;

import lombok.Data;

/**
 * @apiNote token
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:14:31
 */
@Data
public class Token {

	private String token;

	private int expireTime;

	private Date refreshTime;

	private Date createTime;

	private boolean isActive;

	private String creator;

	public Token() {
	}

	public Token(String creator) {
		this.token = CryptoUtils.UUID();
		this.expireTime = 7;
		this.creator = creator;
		Date now = new Date();
		this.refreshTime = now;
		this.createTime = now;
		this.isActive = true;
	}
}