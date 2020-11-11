package com.wy.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * HBase的基本操作
 * 
 * @author ParadiseWY
 * @date 2020-11-10 10:46:20
 * @git {@link https://github.com/mygodness100}
 */
public class HBaseBasic {

	public static Configuration conf;

	// 获取Configuration对象
	static {
		// 使用HBaseConfiguration的单例方法实例化
		conf = HBaseConfiguration.create();
		// 设置zk集群参数
		conf.set("hbase.zookeeper.quorum", "192.168.9.102");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
	}

	/**
	 * 创建命名空间
	 */
	public void createNameSpace() {
		Configuration conf = HBaseConfiguration.create();
		try (Connection conn = ConnectionFactory.createConnection(conf); Admin admin = conn.getAdmin();) {
			// 创建名字空间描述符
			NamespaceDescriptor nsd = NamespaceDescriptor.create("ns2").build();
			admin.createNamespace(nsd);
			NamespaceDescriptor[] ns = admin.listNamespaceDescriptors();
			for (NamespaceDescriptor n : ns) {
				System.out.println(n.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得所有命名空间
	 */
	public void listNameSpaces() {
		Configuration conf = HBaseConfiguration.create();
		try (Connection conn = ConnectionFactory.createConnection(conf); Admin admin = conn.getAdmin();) {
			NamespaceDescriptor[] ns = admin.listNamespaceDescriptors();
			for (NamespaceDescriptor n : ns) {
				System.out.println(n.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断表是否存在
	 * 
	 * @param tableName 表名
	 * @return false不存在
	 */
	public static boolean isTableExist(String tableName) {
		// 在HBase中管理,访问表需要先创建HBaseAdmin对象
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			return admin.tableExists(TableName.valueOf(tableName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建表
	 * 
	 * @param tableName 表名
	 * @param columnFamily 列簇名
	 */
	public static void createTable(String tableName, String... columnFamily) {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			// 判断表是否存在
			if (isTableExist(tableName)) {
				System.out.println("表" + tableName + "已存在");
			} else {
				// 创建表属性对象,表名需要转字节
				TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder
						.newBuilder(TableName.valueOf(tableName));
				List<ColumnFamilyDescriptor> families = new ArrayList<>();
				// 创建多个列族
				for (String cf : columnFamily) {
					families.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build());
				}
				tableDescriptorBuilder.setColumnFamilies(families);
				// 根据对表的配置，创建表
				admin.createTable(tableDescriptorBuilder.build());
				System.out.println("表" + tableName + "创建成功！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除表
	 * 
	 * @param tableName 表名
	 */
	public static void dropTable(String tableName) {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();) {
			if (isTableExist(tableName)) {
				admin.disableTable(TableName.valueOf(tableName));
				admin.deleteTable(TableName.valueOf(tableName));
				System.out.println("表" + tableName + "删除成功！");
			} else {
				System.out.println("表" + tableName + "不存在！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向表中插入数据
	 * 
	 * @param tableName 表名
	 * @param rowKey rowkey
	 * @param columnFamily 列簇名
	 * @param column 列
	 * @param value 值
	 */
	public static void addRowData(String tableName, String rowKey, String columnFamily, String column, String value) {
		// 创建HTable对象
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			Table table = connection.getTable(TableName.valueOf(tableName));
			// 向表中插入数据
			Put put = new Put(Bytes.toBytes(rowKey));
			// 向Put对象中组装数据
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
			table.put(put);
			table.close();
			System.out.println("插入数据成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除多行数据
	 * 
	 * @param tableName
	 * @param rows
	 */
	public static void deleteMultiRow(String tableName, String... rows) {
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			Table table = connection.getTable(TableName.valueOf(tableName));
			List<Delete> deleteList = new ArrayList<Delete>();
			for (String row : rows) {
				Delete delete = new Delete(Bytes.toBytes(row));
				deleteList.add(delete);
			}
			table.delete(deleteList);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有数据
	 * 
	 * @param tableName
	 */
	public static void getAllRows(String tableName) {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName));) {
			// 得到用于扫描region的对象
			Scan scan = new Scan();
			// 设置扫描器缓存
			// scan.setCaching(5000);
			// 设置遍历的起始值
			// scan.withStartRow(Bytes.toBytes("row5000"));
			// scan.withStopRow(Bytes.toBytes("row8000"));
			// 使用HTable得到resultcanner实现类的对象
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {
				Cell[] cells = result.rawCells();
				byte[] name = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
				System.out.println(Bytes.toString(name));
				for (Cell cell : cells) {
					// 得到rowkey
					System.out.println("行键:" + Bytes.toString(CellUtil.cloneRow(cell)));
					// 得到列族
					System.out.println("列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
					System.out.println("列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));
					System.out.println("值:" + Bytes.toString(CellUtil.cloneValue(cell)));
				}
				// 通过map遍历
				// Map<byte[], byte[]> map = result.getFamilyMap(Bytes.toBytes("f1"));
				// for (Map.Entry<byte[], byte[]> entrySet : map.entrySet()) {
				// String col = Bytes.toString(entrySet.getKey());
				// String val = Bytes.toString(entrySet.getValue());
				// System.out.print(col + ":" + val + ",");
				// }

				// 得到一行的所有map,key=f1,value=Map<Col,Map<Timestamp,value>>
				// NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map =
				// result.getMap();
				// for(Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry :
				// map.entrySet()){
				// //得到列族
				// String f = Bytes.toString(entry.getKey());
				// Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
				// for(Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet() ){
				// String c = Bytes.toString(ets.getKey());
				// Map<Long, byte[]> tsValueMap = ets.getValue();
				// for(Map.Entry<Long,byte[]> e : tsValueMap.entrySet()){
				// Long ts = e.getKey() ;
				// String value = Bytes.toString(e.getValue());
				// System.out.print(f+":"+c+":"+ts+"=" +value + ",");
				// }
				// }
				// }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取某一行数据
	 * 
	 * @param tableName
	 * @param rowKey
	 */
	public static void getRow(String tableName, String rowKey) throws IOException {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName));) {
			Get get = new Get(Bytes.toBytes(rowKey));
			// get.setMaxVersions();显示所有版本
			// get.setTimeStamp();显示指定时间戳的版本
			Result result = table.get(get);
			for (Cell cell : result.rawCells()) {
				System.out.println("行键:" + Bytes.toString(result.getRow()));
				System.out.println("列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
				System.out.println("列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));
				System.out.println("值:" + Bytes.toString(CellUtil.cloneValue(cell)));
				System.out.println("时间戳:" + cell.getTimestamp());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取某一行指定列族:列的数据
	 * 
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 */
	public static void getRowQualifier(String tableName, String rowKey, String family, String qualifier) {
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName));) {
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			Result result = table.get(get);
			for (Cell cell : result.rawCells()) {
				System.out.println("行键:" + Bytes.toString(result.getRow()));
				System.out.println("列族" + Bytes.toString(CellUtil.cloneFamily(cell)));
				System.out.println("列:" + Bytes.toString(CellUtil.cloneQualifier(cell)));
				System.out.println("值:" + Bytes.toString(CellUtil.cloneValue(cell)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按照指定版本数查询
	 */
	public void getWithVersions() throws IOException {
		Configuration conf = HBaseConfiguration.create();
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t3");
		Table table = conn.getTable(tname);
		Get get = new Get(Bytes.toBytes("row1"));
		// 检索所有版本
		get.readAllVersions();
		Result r = table.get(get);
		List<Cell> cells = r.getColumnCells(Bytes.toBytes("f1"), Bytes.toBytes("name"));
		for (Cell c : cells) {
			String f = Bytes.toString(c.getFamilyArray());
			String col = Bytes.toString(c.getQualifierArray());
			long ts = c.getTimestamp();
			String val = Bytes.toString(c.getValueArray());
			System.out.println(f + "/" + col + "/" + ts + "=" + val);
		}
	}

	/**
	 * 计数器
	 */
	public void testIncr() throws IOException {
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tname = TableName.valueOf("ns1:t8");
		Table t = conn.getTable(tname);
		Increment incr = new Increment(Bytes.toBytes("row1"));
		incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("daily"), 1);
		incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("weekly"), 10);
		incr.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("monthly"), 100);
		t.increment(incr);
	}
}