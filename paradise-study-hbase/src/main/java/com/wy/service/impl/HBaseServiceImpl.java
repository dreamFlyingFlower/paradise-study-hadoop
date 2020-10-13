package com.wy.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import com.wy.common.ErrorCode;
import com.wy.result.ResultException;

/**
 * @apiNote HBASE实现类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:26:54
 */
public interface HBaseServiceImpl {

	/**
	 * 创建表
	 * @param connection 连接
	 * @param tableName 表名
	 * @param columnFamilies 列族
	 * @param splitKeys 预先分区，用于分区的键
	 * @return
	 */
	public static boolean createTable(Connection connection, String tableName, String[] columnFamilies,
			byte[][] splitKeys) {
		try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
			// 表存在,直接返回false
			if (admin.tableExists(TableName.valueOf(tableName))) {
				return false;
			}
			// 创建列族
			TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder
					.newBuilder(TableName.valueOf(tableName));
			Arrays.stream(columnFamilies).forEach(columnFamily -> {
				ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder
						.newBuilder(Bytes.toBytes(columnFamily)).setMaxVersions(1).build();
				tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
			});
			admin.createTable(tableDescriptorBuilder.build(), splitKeys);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "Create table " + tableName + " error");
		}
		return true;
	}

	/**
	 * 删除表操作
	 * @param connection 连接
	 * @param tableName 表名
	 * @return
	 */
	public static boolean deleteTable(Connection connection, String tableName) {
		try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
			// 删除表
			admin.disableTable(TableName.valueOf(tableName));
			admin.deleteTable(TableName.valueOf(tableName));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "delete table " + tableName + " error");
		}
	}

	public static boolean deleteColumnFamily(Connection connection, String tableName, String columnFamily) {

		try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
			// 删除列族
			admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(columnFamily));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE,
					"delete column family " + columnFamily + " in table " + tableName + " error");
		}
	}

	/**
	 * 删除某条数据的某列
	 * @param connection 连接
	 * @param tableName 表名
	 * @param rowKey
	 * @param columnFamily
	 * @param columnQulifier
	 * @return
	 */
	public static boolean deleteColumnQualifier(Connection connection, String tableName, String rowKey,
			String columnFamily, String columnQulifier) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Delete delete = new Delete(rowKey.getBytes());
			delete.addColumn(columnFamily.getBytes(), columnQulifier.getBytes());
			return deleteRow(connection, tableName, delete);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "delete column qualifier " + columnQulifier
					+ " of column family " + columnFamily + " in table " + tableName + " error");
		}
	}

	/**
	 * 根据delete进行数据删除
	 * @param connection
	 * @param tableName
	 * @param delete
	 * @return
	 */
	public static boolean deleteRow(Connection connection, String tableName, Delete delete) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			table.delete(delete);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "delete column qualifier error");
		}
	}

	/**
	 * 删除行数据
	 * @param connection
	 * @param tableName
	 * @param rowKey
	 * @return
	 */
	public static boolean deleteRow(Connection connection, String tableName, String rowKey) {
		Delete delete = new Delete(rowKey.getBytes());
		return deleteRow(connection, tableName, delete);
	}

	public static boolean existsRow(Connection connection, String tableName, String row) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Get g = new Get(Bytes.toBytes(row));
			return table.exists(g);
		} catch (IOException e) {
			String msg = String.format("check exists row from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
	}

	public static boolean deleteRows(Connection connection, String tableName, List<String> rows) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			List<Delete> list = new ArrayList<Delete>();
			for (String row : rows) {
				Delete d = new Delete(Bytes.toBytes(row));
				list.add(d);
			}
			if (list.size() > 0) {
				table.delete(list);
			}
		} catch (IOException e) {
			String msg = String.format("delete table=%s , rows error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return true;
	}

	public static boolean deleteQualifier(Connection connection, String tableName, String rowName,
			String columnFamilyName, String qualifierName) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Delete delete = new Delete(rowName.getBytes());
			delete.addColumns(columnFamilyName.getBytes(), qualifierName.getBytes());
			table.delete(delete);
		} catch (IOException e) {
			String msg = String.format("delete table=%s , column family=%s , qualifier=%s error. msg=%s", tableName,
					columnFamilyName, qualifierName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return true;
	}

	/**
	 * 根据Get对象读取行数据
	 * @param connection
	 * @param tableName
	 * @param get
	 * @return
	 */
	public static Result getRow(Connection connection, String tableName, Get get) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			return table.get(get);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "get row error");
		}
	}

	/**
	 * 根据rowkey获取行数据
	 * @param connection
	 * @param tableName
	 * @param rowKey
	 * @return
	 */
	public static Result getRow(Connection connection, String tableName, String rowKey) {
		Get get = new Get(rowKey.getBytes());
		return getRow(connection, tableName, get);
	}

	public static Result getRow(Connection connection, String tableName, String row, FilterList filterList) {
		Result rs;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Get g = new Get(Bytes.toBytes(row));
			g.setFilter(filterList);
			rs = table.get(g);
		} catch (IOException e) {
			String msg = String.format("get row from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return rs;
	}

	public static Result getRow(Connection connection, String tableName, String row, byte[] column, byte[] qualifier) {
		Result rs;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Get g = new Get(Bytes.toBytes(row));
			g.addColumn(column, qualifier);
			rs = table.get(g);
		} catch (IOException e) {
			String msg = String.format("get row from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return rs;
	}

	public static Result[] getRows(Connection connection, String tableName, List<String> rows, FilterList filterList) {
		Result[] results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			List<Get> gets = null;
			gets = new ArrayList<Get>();
			for (String row : rows) {
				if (row != null) {
					Get g = new Get(Bytes.toBytes(row));
					g.setFilter(filterList);
					gets.add(g);
				}
			}
			if (gets.size() > 0) {
				results = table.get(gets);
			}
		} catch (Exception e) {
			String msg = String.format("get rows from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	public static Result[] getRows(Connection connection, String tableName, List<String> rows) {
		Result[] results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			List<Get> gets = null;
			gets = new ArrayList<Get>();
			for (String row : rows) {
				if (row != null) {
					Get g = new Get(Bytes.toBytes(row));
					gets.add(g);
				}
			}
			if (gets.size() > 0) {
				results = table.get(gets);
			}
		} catch (Exception e) {
			String msg = String.format("get rows from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	/**
	 * 获取scanner对象
	 * @param connection
	 * @param tableName
	 * @param scan
	 * @return
	 */
	public static ResultScanner getScanner(Connection connection, String tableName, Scan scan) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			return table.getScanner(scan);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "get row error");
		}
	}

	/**
	 * 根据起始rowkey，filter来获取scanner
	 * @param connection
	 * @param tableName
	 * @param startKey
	 * @param endKey
	 * @param filterList
	 * @return
	 */
	public static ResultScanner getScanner(Connection connection, String tableName, String startKey, String endKey,
			FilterList filterList) {
		Scan scan = new Scan();
		scan.withStartRow(startKey.getBytes());
		scan.withStopRow(endKey.getBytes());
		scan.setFilter(filterList);
		scan.setCaching(1000);
		return getScanner(connection, tableName, scan);
	}

	public static ResultScanner getScanner(Connection connection, String tableName, FilterList filterList) {
		ResultScanner results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			scan.setCaching(1000);
			scan.setFilter(filterList);
			results = table.getScanner(scan);
		} catch (IOException e) {
			String msg = String.format("scan table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	public static ResultScanner getScanner(Connection connection, String tableName) {
		ResultScanner results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			scan.setCaching(1000);
			results = table.getScanner(scan);
		} catch (IOException e) {
			String msg = String.format("scan table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	public static ResultScanner getScanner(Connection connection, String tableName, byte[] startRowKey,
			byte[] stopRowKey) {
		ResultScanner results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			scan.withStartRow(startRowKey);
			scan.withStopRow(stopRowKey);
			scan.setCaching(1000);
			results = table.getScanner(scan);
		} catch (IOException e) {
			String msg = String.format("scan table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	public static ResultScanner getScanner(Connection connection, String tableName, String startRowKey,
			String stopRowKey) {
		return getScanner(connection, tableName, Bytes.toBytes(startRowKey), Bytes.toBytes(stopRowKey));
	}

	public static ResultScanner getScanner(Connection connection, String tableName, byte[] startRowKey,
			byte[] stopRowKey, FilterList filterList) {
		ResultScanner results = null;
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			scan.withStartRow(startRowKey);
			scan.withStopRow(stopRowKey);
			scan.setCaching(1000);
			scan.setFilter(filterList);
			results = table.getScanner(scan);
		} catch (IOException e) {
			String msg = String.format("scan table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return results;
	}

	/**
	 * 根据put对象添加行
	 * @param connection
	 * @param tableName
	 * @param put
	 * @return
	 */
	public static boolean putRow(Connection connection, String tableName, Put put) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			table.put(put);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "put row error");
		}
	}

	/**
	 * 根据列族、列、数据来插入行
	 * @param connection
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @param columnQuanlifier
	 * @param data
	 * @return
	 */
	public static boolean putRow(Connection connection, String tableName, String rowKey, String columnFamily,
			String columnQuanlifier, String data) {
		Put put = new Put(rowKey.getBytes());
		put.addColumn(columnFamily.getBytes(), columnQuanlifier.getBytes(), data.getBytes());
		return putRow(connection, tableName, put);
	}

	/**
	 * 通过这个方法生成目录的SequenceID
	 * @param connection
	 * @param tableName
	 * @param row
	 * @param columnFamily
	 * @param columnQuanlifier
	 * @param number
	 * @return
	 */
	public static long incrementColumnValue(Connection connection, String tableName, String row, byte[] columnFamily,
			byte[] columnQuanlifier, int number) {
		try (Table table = connection.getTable(TableName.valueOf(tableName))) {
			return table.incrementColumnValue(row.getBytes(), columnFamily, columnQuanlifier, number);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException(ErrorCode.ERROR_HBASE, "put row error");
		}
	}

	/**
	 * 批量插入行数据
	 * @param connection
	 * @param tableName
	 * @param puts
	 * @return
	 */
	public static boolean batchPutRows(Connection connection, String tableName, List<Put> puts) {
		final BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {
			@Override
			public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
				String msg = String.format("put rows from table=%s error. msg=%s", tableName, e.getMessage());
				throw new ResultException(ErrorCode.ERROR_HBASE, msg);
			}
		};
		BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName)).listener(listener);
		params.writeBufferSize(5 * 1024 * 1024);
		try (final BufferedMutator mutator = connection.getBufferedMutator(params)) {
			mutator.mutate(puts);
			mutator.flush();
		} catch (IOException e) {
			String msg = String.format("put rows from table=%s error. msg=%s", tableName, e.getMessage());
			throw new ResultException(ErrorCode.ERROR_HBASE, msg);
		}
		return true;
	}
}