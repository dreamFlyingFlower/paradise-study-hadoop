package com.wy.model;

import java.util.Date;

import lombok.Data;

/**
 * @apiNote auth验证
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:25:13
 */
@Data
public class Auth {

	private String bucketName;

	private String targetToken;

	private Date authTime;
}