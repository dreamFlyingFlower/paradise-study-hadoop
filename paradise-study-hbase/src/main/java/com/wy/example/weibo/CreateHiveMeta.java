package com.wy.example.weibo;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
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
	private Configuration conf = HBaseConfiguration.create();

	/**
	 * 初始化方法
	 */
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
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			// 命名空间类似于关系型数据库中的schema,可以想象成文件夹
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
	 * Table Name:weibo:content<br>
	 * RowKey:用户ID_时间戳<br>
	 * ColumnFamily:info<br>
	 * ColumnLabel:标题 内容 图片URL<br>
	 * Version:1个版本
	 */
	public void createTableContent() {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			// 创建列族描述
			ColumnFamilyDescriptor info = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("info"))
					// 设置块缓存
					.setBlockCacheEnabled(true)
					// 设置块缓存大小
					.setBlocksize(2097152)
					// 设置压缩方式
					// .setCompressionType(Algorithm.SNAPPY)
					// 设置版本确界
					.setMaxVersions(1).setMinVersions(1).build();
			// 创建表表述
			TableDescriptor tableDescriptor = TableDescriptorBuilder
					.newBuilder(TableName.valueOf(Constants.TABLE_CONTENT)).setColumnFamily(info).build();
			admin.createTable(tableDescriptor);
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
	 * ColumnLabel:关注用户ID,粉丝用户ID<br>
	 * ColumnValue:用户ID<br>
	 * Version：1个版本<br>
	 */
	public void createTableRelations() {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			// 关注的人的列族
			ColumnFamilyDescriptor attends = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("attends"))
					// 设置块缓存
					.setBlockCacheEnabled(true)
					// 设置块缓存大小
					.setBlocksize(2097152)
					// 设置压缩方式
					// .setCompressionType(Algorithm.SNAPPY)
					// 设置版本确界
					.setMaxVersions(1).setMinVersions(1).build();
			// 粉丝列族
			ColumnFamilyDescriptor fans = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("fans"))
					.setBlockCacheEnabled(true).setBlocksize(2097152).setMaxVersions(1).setMinVersions(1).build();
			// 创建表表述
			TableDescriptor tableDescriptor = TableDescriptorBuilder
					.newBuilder(TableName.valueOf(Constants.TABLE_RELATIONS))
					.setColumnFamilies(Arrays.asList(attends, fans)).build();
			admin.createTable(tableDescriptor);
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
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			ColumnFamilyDescriptor info = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("info"))
					.setBlockCacheEnabled(true).setBlocksize(2097152).setMaxVersions(1000).setMinVersions(1000).build();
			TableDescriptor receive_content_email = TableDescriptorBuilder
					.newBuilder(TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL)).setColumnFamily(info).build();
			admin.createTable(receive_content_email);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}