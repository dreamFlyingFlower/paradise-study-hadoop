package com.wy.example.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;

import com.wy.common.Constants;
import com.wy.model.Message;

/**
 * 发布微博
 *
 * 微博内容表中添加1条数据<br>
 * 微博收件箱表对所有粉丝用户添加数据
 * 
 * @author ParadiseWY
 * @date 2020-11-10 16:32:33
 * @git {@link https://github.com/mygodness100}
 */
public class OperateWeibo {

	/** 获取配置conf */
	private Configuration conf = HBaseConfiguration.create();

	public void publishContent(String uid, String content) {
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			// 1.微博内容表中添加1条数据,首先获取微博内容表描述
			Table contentTBL = connection.getTable(TableName.valueOf(Constants.TABLE_CONTENT));
			// 组装Rowkey
			long timestamp = System.currentTimeMillis();
			String rowKey = uid + "_" + timestamp;
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("content"), timestamp, Bytes.toBytes(content));
			contentTBL.put(put);
			// 2.向微博收件箱表中加入发布的Rowkey
			// 2.1 查询用户关系表,得到当前用户有哪些粉丝
			Table relationsTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RELATIONS));
			// 2.2 取出目标数据
			Get get = new Get(Bytes.toBytes(uid));
			get.addFamily(Bytes.toBytes("fans"));

			Result result = relationsTBL.get(get);
			List<byte[]> fans = new ArrayList<byte[]>();

			// 遍历取出当前发布微博的用户的所有粉丝数据
			for (Cell cell : result.rawCells()) {
				fans.add(CellUtil.cloneQualifier(cell));
			}
			// 如果该用户没有粉丝,则直接return
			if (fans.size() <= 0)
				return;
			// 开始操作收件箱表
			Table recTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL));
			List<Put> puts = new ArrayList<Put>();
			for (byte[] fan : fans) {
				Put fanPut = new Put(fan);
				fanPut.addColumn(Bytes.toBytes("info"), Bytes.toBytes(uid), timestamp, Bytes.toBytes(rowKey));
				puts.add(fanPut);
			}
			recTBL.put(puts);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关注用户逻辑
	 * 
	 * 1.在微博用户关系表中,对当前主动操作的用户添加新的关注的好友<br>
	 * 2.在微博用户关系表中,对被关注的用户添加粉丝(当前操作的用户)<br>
	 * 3.当前操作用户的微博收件箱添加所关注的用户发布的微博rowkey<br>
	 */
	public void addAttends(String uid, String... attends) {
		// 参数过滤
		if (attends == null || attends.length <= 0 || uid == null || uid.length() <= 0) {
			return;
		}
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			// 用户关系表操作对象,连接到用户关系表
			Table relationsTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RELATIONS));
			List<Put> puts = new ArrayList<Put>();
			// 1.在微博用户关系表中,添加新关注的好友
			Put attendPut = new Put(Bytes.toBytes(uid));
			for (String attend : attends) {
				// 为当前用户添加关注的人
				attendPut.addColumn(Bytes.toBytes("attends"), Bytes.toBytes(attend), Bytes.toBytes(attend));
				// 2.为被关注的人,添加粉丝
				Put fansPut = new Put(Bytes.toBytes(attend));
				fansPut.addColumn(Bytes.toBytes("fans"), Bytes.toBytes(uid), Bytes.toBytes(uid));
				// 将所有关注的人一个一个的添加到puts(List)集合中
				puts.add(fansPut);
			}
			puts.add(attendPut);
			relationsTBL.put(puts);
			// 3.1 微博收件箱添加关注的用户发布的微博内容(content)的rowkey
			Table contentTBL = connection.getTable(TableName.valueOf(Constants.TABLE_CONTENT));
			Scan scan = new Scan();
			// 用于存放取出来的关注的人所发布的微博的rowkey
			List<byte[]> rowkeys = new ArrayList<byte[]>();
			for (String attend : attends) {
				// 过滤扫描rowkey,即:前置位匹配被关注的人的uid_
				RowFilter filter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator(attend + "_"));
				// 为扫描对象指定过滤规则
				scan.setFilter(filter);
				// 通过扫描对象得到scanner
				ResultScanner result = contentTBL.getScanner(scan);
				// 迭代器遍历扫描出来的结果集
				Iterator<Result> iterator = result.iterator();
				while (iterator.hasNext()) {
					// 取出每一个符合扫描结果的那一行数据
					Result r = iterator.next();
					for (Cell cell : r.rawCells()) {
						// 将得到的rowkey放置于集合容器中
						rowkeys.add(CellUtil.cloneRow(cell));
					}
				}
			}
			// 3.2 将取出的微博rowkey放置于当前操作用户的收件箱中
			if (rowkeys.size() <= 0)
				return;
			// 得到微博收件箱表的操作对象
			Table recTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL));
			// 用于存放多个关注的用户的发布的多条微博rowkey信息
			List<Put> recPuts = new ArrayList<Put>();
			for (byte[] rk : rowkeys) {
				Put put = new Put(Bytes.toBytes(uid));
				// uid_timestamp
				String rowKey = Bytes.toString(rk);
				// 借取uid
				String attendUID = rowKey.substring(0, rowKey.indexOf("_"));
				long timestamp = Long.parseLong(rowKey.substring(rowKey.indexOf("_") + 1));
				// 将微博rowkey添加到指定单元格中
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes(attendUID), timestamp, rk);
				recPuts.add(put);
			}
			recTBL.put(recPuts);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消关注,remove
	 * 
	 * 1.在微博用户关系表中,对当前主动操作的用户删除对应取关的好友<br>
	 * 2.在微博用户关系表中,对被取消关注的人删除粉丝(当前操作人)<br>
	 * 3.从收件箱中,删除取关的人的微博的rowkey<br>
	 */
	public void removeAttends(String uid, String... attends) {
		// 过滤数据
		if (uid == null || uid.length() <= 0 || attends == null || attends.length <= 0)
			return;
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			// 1.在微博用户关系表中,删除已关注的好友
			Table relationsTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RELATIONS));
			// 待删除的用户关系表中的所有数据
			List<Delete> deletes = new ArrayList<Delete>();
			// 当前取关操作者的uid对应的Delete对象
			Delete attendDelete = new Delete(Bytes.toBytes(uid));
			// 遍历取关,同时每次取关都要将被取关的人的粉丝-1
			for (String attend : attends) {
				attendDelete.addColumn(Bytes.toBytes("attends"), Bytes.toBytes(attend));
				// 2
				Delete fansDelete = new Delete(Bytes.toBytes(attend));
				fansDelete.addColumn(Bytes.toBytes("fans"), Bytes.toBytes(uid));
				deletes.add(fansDelete);
			}
			deletes.add(attendDelete);
			relationsTBL.delete(deletes);
			// 2.删除取关的人的微博rowkey从 收件箱表中
			Table recTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL));
			Delete recDelete = new Delete(Bytes.toBytes(uid));
			for (String attend : attends) {
				recDelete.addColumn(Bytes.toBytes("info"), Bytes.toBytes(attend));
			}
			recTBL.delete(recDelete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取微博实际内容
	 * 
	 * 1.从微博收件箱中获取所有关注的人的发布的微博的rowkey<br>
	 * 2.根据得到的rowkey去微博内容表中得到数据<br>
	 * 3.将得到的数据封装到Message对象中<br>
	 */
	public List<Message> getAttendsContent(String uid) {
		try (Connection connection = ConnectionFactory.createConnection(conf);) {
			Table recTBL = connection.getTable(TableName.valueOf(Constants.TABLE_RECEIVE_CONTENT_EMAIL));
			// 1.从收件箱中取得微博rowKey
			Get get = new Get(Bytes.toBytes(uid));
			// 设置最大版本号
			get.readVersions(5);
			List<byte[]> rowkeys = new ArrayList<byte[]>();
			Result result = recTBL.get(get);
			for (Cell cell : result.rawCells()) {
				rowkeys.add(CellUtil.cloneValue(cell));
			}
			// 2.根据取出的所有rowkey去微博内容表中检索数据
			Table contentTBL = connection.getTable(TableName.valueOf(Constants.TABLE_CONTENT));
			List<Get> gets = new ArrayList<Get>();
			// 根据rowkey取出对应微博的具体内容
			for (byte[] rk : rowkeys) {
				Get g = new Get(rk);
				gets.add(g);
			}
			// 得到所有的微博内容的result对象
			Result[] results = contentTBL.get(gets);
			List<Message> messages = new ArrayList<Message>();
			for (Result res : results) {
				for (Cell cell : res.rawCells()) {
					Message message = new Message();
					String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
					String userid = rowKey.substring(0, rowKey.indexOf("_"));
					String timestamp = rowKey.substring(rowKey.indexOf("_") + 1);
					String content = Bytes.toString(CellUtil.cloneValue(cell));
					message.setContent(content);
					message.setTimestamp(timestamp);
					message.setUid(userid);
					messages.add(message);
				}
			}
			return messages;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}