package com.wy.common;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * 常量配置
 * 
 * @author ParadiseWY
 * @date 2020-11-10 16:53:39
 * @git {@link https://github.com/mygodness100}
 */
public interface Constants {

	/** 微博内容表的表名 */
	byte[] TABLE_CONTENT = Bytes.toBytes("weibo:content");

	/** 微博用户关系表的表名 */
	byte[] TABLE_RELATIONS = Bytes.toBytes("weibo:relations");

	/** 微博收件箱表的表名 */
	byte[] TABLE_RECEIVE_CONTENT_EMAIL = Bytes.toBytes("weibo:receive_content_email");
}