package com.wy.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;

public class Example03 {

	// 创建表,插入记录,查询一条记录,遍历所有的记录,删除表
	public static final String TABLE_NAME = "table1";

	public static final String FAMILY_NAME = "family1";

	public static final String ROW_KEY = "rowkey1";

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.rootdir", "hdfs://hadoop0:9000/hbase");
		// 使用eclipse时必须添加这个,否则无法定位
		conf.set("hbase.zookeeper.quorum", "hadoop0");
		Connection connection = ConnectionFactory.createConnection(conf);
		// 创建表,删除表使用HBaseAdmin
		HBaseAdmin hBaseAdmin = (HBaseAdmin) connection.getAdmin();
		createTable(hBaseAdmin);
		// 插入记录,查询一条记录,遍历所有的记录HTable
		Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
		// putRecord(hTable);
		// getRecord(hTable);
		// scanTable(hTable);
		table.close();
		// deleteTable(hBaseAdmin);
	}

	public static void scanTable(final HTable hTable) throws IOException {
		Scan scan = new Scan();
		final ResultScanner scanner = hTable.getScanner(scan);
		for (Result result : scanner) {
			final byte[] value = result.getValue(FAMILY_NAME.getBytes(), "age".getBytes());
			System.out.println(result + "\t" + new String(value));
		}
	}

	public static void getRecord(final Table hTable) throws IOException {
		Get get = new Get(ROW_KEY.getBytes());
		final Result result = hTable.get(get);
		final byte[] value = result.getValue(FAMILY_NAME.getBytes(), "age".getBytes());
		System.out.println(result + "\t" + new String(value));
	}

	public static void putRecord(final Table hTable) throws IOException {
		Put put = new Put(ROW_KEY.getBytes());
		put.addColumn(FAMILY_NAME.getBytes(), "age".getBytes(), "25".getBytes());
		hTable.put(put);
	}

	public static void deleteTable(final HBaseAdmin hBaseAdmin) throws IOException {
		hBaseAdmin.disableTable(TableName.valueOf(TABLE_NAME));
		hBaseAdmin.deleteTable(TableName.valueOf(TABLE_NAME));
	}

	public static void createTable(final HBaseAdmin hBaseAdmin) throws IOException {
		if (!hBaseAdmin.tableExists(TableName.valueOf(TABLE_NAME))) {
			TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf(TABLE_NAME))
					.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(FAMILY_NAME)).build())
					.build();
			hBaseAdmin.createTable(tableDescriptor);
		}
	}
}