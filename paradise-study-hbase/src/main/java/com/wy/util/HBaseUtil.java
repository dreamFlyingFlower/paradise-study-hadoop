package com.wy.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @apiNote HBase工具类
 * @author ParadiseWY
 * @date 2020年2月9日 下午1:47:15
 */
public class HBaseUtil {

	/**
	 * 创建HBase表
	 * @param tableName 表名
	 * @param cfs column family
	 * @return 是否创建成功
	 */
	public static boolean createTable(String tableName, String[] cfs) {
		try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();) {
			if (admin.tableExists(TableName.valueOf(tableName))) {
				return false;
			}
			TableDescriptorBuilder descriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
			Arrays.stream(cfs).forEach(item -> {
				ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
						.newBuilder(Bytes.toBytes(item)).setMaxVersions(1).build();
				// 将CF信息添加到表中,单条添加是否会导致覆盖原有的CF?
				descriptorBuilder.setColumnFamily(columnFamilyDescriptor);
			});
			admin.createTable(descriptorBuilder.build());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除hbase表
	 * @param tableName 表名
	 * @return 是否删除成功
	 */
	public static boolean deleteTable(String tableName) {
		try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();) {
			admin.disableTable(TableName.valueOf(tableName));
			admin.deleteTable(TableName.valueOf(tableName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 插入数据
	 * @param tablename 表名
	 * @param rowKey 唯一标识
	 * @param cfname 列簇名
	 * @param cqname 列key
	 * @param data 数据
	 * @return 插入是否成功
	 */
	public static boolean putRow(String tablename, String rowKey, String cfname, String cqname, String data) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(cfname), Bytes.toBytes(cqname), Bytes.toBytes(data));
			table.put(put);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 批量插入数据
	 * @param tablename 表名
	 * @param puts 数据
	 * @return 插入是否成功
	 */
	public static boolean putRows(String tablename, List<Put> puts) {
		try (Table table = HBaseConn.getTable(tablename)) {
			table.put(puts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 查询单条数据
	 * @param tablename 表名
	 * @param rowKey 唯一标识
	 * @return 结果
	 */
	public static Result getRow(String tablename, String rowKey) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Get get = new Get(Bytes.toBytes(rowKey));
			return table.get(get);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询单条数据
	 * @param tablename 表名
	 * @param rowKey 唯一标识
	 * @param filterList 过滤器
	 * @return 结果
	 */
	public static Result getRow(String tablename, String rowKey, FilterList filterList) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Get get = new Get(Bytes.toBytes(rowKey));
			get.setFilter(filterList);
			return table.get(get);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 批量检索数据
	 * @param tablename 表名
	 * @return 批量数据
	 */
	public static ResultScanner getScan(String tablename) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Scan scan = new Scan();
			scan.setCaching(1000);
			return table.getScanner(scan);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 批量检索数据
	 * @param tablename 表名
	 * @param startRowKey 起始rowkey
	 * @param stopRowKey 结束rowkey
	 * @return 结果集
	 */
	public static ResultScanner getScan(String tablename, String startRowKey, String stopRowKey) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Scan scan = new Scan();
			scan.withStartRow(Bytes.toBytes(startRowKey)).withStopRow(Bytes.toBytes(stopRowKey));
			scan.setCaching(1000);
			return table.getScanner(scan);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 批量检索数据
	 * @param tablename 表名
	 * @param startRowKey 起始rowkey
	 * @param stopRowKey 结束rowkey
	 * @param filterList 过滤条件
	 * @return 结果集
	 */
	public static ResultScanner getScan(String tablename, String startRowKey, String stopRowKey,
			FilterList filterList) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Scan scan = new Scan();
			scan.withStartRow(Bytes.toBytes(startRowKey)).withStopRow(Bytes.toBytes(stopRowKey));
			scan.setFilter(filterList);
			scan.setCaching(1000);
			return table.getScanner(scan);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean deleteRow(String tablename, String rowKey) {
		try (Table table = HBaseConn.getTable(tablename)) {
			Delete operate = new Delete(Bytes.toBytes(rowKey));
			table.delete(operate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean deleteCF(String tableName, String cfname) {
		try (HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();) {
			admin.deleteTable(TableName.valueOf(tableName));
			admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(cfname));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean deleteCQ(String tableName, String rowKey, String cfname, String cqname) {
		try (Table table = HBaseConn.getTable(tableName)) {
			Delete operate = new Delete(Bytes.toBytes(rowKey));
			operate.addColumn(Bytes.toBytes(cfname), Bytes.toBytes(cqname));
			table.delete(operate);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}