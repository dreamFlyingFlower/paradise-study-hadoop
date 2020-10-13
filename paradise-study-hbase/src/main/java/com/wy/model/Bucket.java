package com.wy.model;

import java.util.Date;

import com.wy.crypto.CryptoUtils;

import lombok.Data;

/**
 * @apiNote 权限模块
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:00:14
 */
@Data
public class Bucket {

	private String bucketId;

	private String bucketName;

	private String creator;

	private String detail;

	private Date createTime;

	public Bucket() {
	}

	public Bucket(String bucketName, String creator, String detail) {
		this.bucketId = CryptoUtils.UUID();
		this.bucketName = bucketName;
		this.creator = creator;
		this.detail = detail;
		this.createTime = new Date();
	}
}