package com.wy.weibo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;

import com.wy.common.Constants;

/**
 * 创建命名空间以及表名的定义
 * 
 * @author ParadiseWY
 * @date 2020-11-10 15:14:23
 * @git {@link https://github.com/mygodness100}
 */
public class CreateHiveMeta {

	/** 获取配置conf */
	Configuration conf = HBaseConfiguration.create();

	public void initTable() {
		initNamespace();
		createTableContent();
		createTableRelations();
		createTableReceiveContentEmail();
	}

	/**
	 * 创建命名空间以及表名的定义
	 */
	public void initNamespace() {
		try (HBaseAdmin admin = new HBaseAdmin(conf);) {
			// 命名空间类似于关系型数据库中的schema，可以想象成文件夹
			NamespaceDescriptor weibo = NamespaceDescriptor.create("weibo").addConfiguration("creator", "Jinji")
					.addConfiguration("create_time", System.currentTimeMillis() + "").build();
			admin.createNamespace(weibo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建微博内容表
	 * 
	 * Table Name:weibo:content <br>
	 * RowKey:用户ID_时间戳<br>
	 * ColumnFamily:info ColumnLabel:标题 内容 图片URL<br>
	 * Version:1个版本
	 */
	public void createTableContent() {
		try (HBaseAdmin admin = new HBaseAdmin(conf);) {
			// 创建表表述
			HTableDescriptor content = new HTableDescriptor(TableName.valueOf(Constants.TABLE_CONTENT));
			// 创建列族描述
			HColumnDescriptor info = new HColumnDescriptor(Bytes.toBytes("info"));
			// 设置块缓存
			info.setBlockCacheEnabled(true);
			// 设置块缓存大小
			info.setBlocksize(2097152);
			// 设置压缩方式
			// info.setCompressionType(Algorithm.SNAPPY);
			// 设置版本确界
			info.setMaxVersions(1);
			info.setMinVersions(1);
			content.addFamily(info);
			admin.createTable(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户关系表<br>
	 * 
	 * Table Name:weibo:relations<br>
	 * RowKey:用户ID<br>
	 * ColumnFamily:attends,fans<br>
	 * ColumnLabel:关注用户ID，粉丝用户ID<br>
	 * ColumnValue:用户ID<br>
	 * Version：1个版本<br>
	 */
	public void createTableRelations() {
		try (HBaseAdmin admin = new HBaseAdmin(conf);) {
			HTableDescriptor relations = new HTableDescriptor(TableName.valueOf(Constants.TABLE_RELATIONS));
			// 关注的人的列族
			HColumnDescriptor attends = new HColumnDescriptor(Bytes.toBytes("attends"));
			// 设置块缓存
			attends.setBlockCacheEnabled(true);
			// 设置块缓存大小
			attends.setBlocksize(2097152);
			// 设置压缩方式
			// info.setCompressionType(Algorithm.SNAPPY);
			// 设置版本确界
			attends.setMaxVersions(1);
			attends.setMinVersions(1);
			// 粉丝列族
			HColumnDescriptor fans = new HColumnDescriptor(Bytes.toBytes("fans"));
			fans.setBlockCacheEnabled(true);
			fans.setBlocksize(2097152);
			fans.setMaxVersions(1);
			fans.setMinVersions(1);
			relations.addFamily(attends);
			relations.addFamily(fans);
			admin.createTable(relations);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建微博收件箱表
	 * 
	 * Table Name: weibo:receive_content_email<br>
	 * RowKey:用户ID<br>
	 * ColumnFamily:info<br>
	 * ColumnLabel:用户ID-发布微博的人的用户ID<br>
	 * ColumnValue:关注的人的微博的RowKey<br>
	 * Version:1000
	 */
	public void createTableReceiveContentEmail() {
		try (HBaseAdmin admin = new HBaseAdmin(conf);) {
			HTableDescriptor receive_content_email = new HTableDescriptor(
					TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL));
			HColumnDescriptor info = new HColumnDescriptor(Bytes.toBytes("info"));

			info.setBlockCacheEnabled(true);
			info.setBlocksize(2097152);
			info.setMaxVersions(1000);
			info.setMinVersions(1000);

			receive_content_email.addFamily(info);
			;
			admin.createTable(receive_content_email);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}