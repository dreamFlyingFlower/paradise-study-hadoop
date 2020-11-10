package com.wy.example;

import java.io.IOException;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * HBase整合MR,用于读取fruit表中的数据
 *
 * 新建一个数据文件fruit,上传到hdfs中,数据格式如下,注意:br是为了好看,文件中没有<br>
 * 1001 Apple Red<br>
 * 1002 Pear Yellow<br>
 * 1003 Pineapple Yellow<br>
 *
 * @author ParadiseWY
 * @date 2020-11-10 11:01:14
 * @git {@link https://github.com/mygodness100}
 */
public class FruitReadMapper extends TableMapper<ImmutableBytesWritable, Put> {

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context)
			throws IOException, InterruptedException {
		// 将fruit的name和color提取出来，相当于将每一行数据读取出来放入到Put对象中。
		Put put = new Put(key.get());
		// 遍历添加column行
		for (Cell cell : value.rawCells()) {
			// 添加/克隆列族:info
			if ("info".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
				// 添加/克隆列：name
				if ("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
					// 将该列cell加入到put对象中
					put.add(cell);
					// 添加/克隆列:color
				} else if ("color".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
					// 向该列cell加入到put对象中
					put.add(cell);
				}
			}
		}
		// 将从fruit读取到的每行数据写入到context中作为map的输出
		context.write(key, put);
	}
}