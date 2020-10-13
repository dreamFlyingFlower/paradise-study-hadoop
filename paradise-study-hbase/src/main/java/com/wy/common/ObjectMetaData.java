package com.wy.common;

import java.util.Map;

import lombok.Data;

/**
 * @apiNote 对象meta信息类
 * @author ParadiseWY
 * @date 2020年2月10日 上午12:10:11
 */
@Data
public class ObjectMetaData {

	private String bucket;

	private String key;

	private String mediaType;

	private long length;

	private long lastModifyTime;

	private Map<String, String> attrs;

	public String getContentEncoding() {
		return this.attrs != null ? this.attrs.get("content-encoding") : null;
	}
}