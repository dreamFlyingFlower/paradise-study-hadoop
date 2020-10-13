package com.wy.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

/**
 * @apiNote HBase连接类
 * @author ParadiseWY
 * @date 2020年2月9日 下午1:29:09
 */
public class HBaseConn {

	private static final HBaseConn INSTANCE = new HBaseConn();

	private static Configuration configuration;

	private static Connection connection;

	private HBaseConn() {
		if (configuration == null) {
			configuration = HBaseConfiguration.create();
			// 设置zk地址
			configuration.set("hbase.zookeeper.quorum", "localhost:2181");
		}
	}

	/**
	 * 获得连接
	 * @return connection
	 */
	private Connection getConnection() {
		if (connection == null || connection.isClosed()) {
			try {
				connection = ConnectionFactory.createConnection(configuration);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	public static Connection getHBaseConn() {
		return INSTANCE.getConnection();
	}

	/**
	 * 获得表
	 * @param tableName 表名
	 * @return Table
	 */
	public static Table getTable(String tableName) {
		try {
			return INSTANCE.getConnection().getTable(TableName.valueOf(tableName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void closeConn() {
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}