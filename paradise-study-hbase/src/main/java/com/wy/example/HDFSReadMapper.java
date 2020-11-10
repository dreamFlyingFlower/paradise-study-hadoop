package com.wy.example;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 整个hdfs,同样是fruit表,读取HDFS中的文件数据
 * 
 * 新建一个数据文件fruit,上传到hdfs中,数据格式如下,注意:br是为了好看,文件中没有<br>
 * 1001 Apple Red<br>
 * 1002 Pear Yellow<br>
 * 1003 Pineapple Yellow<br>
 * 
 * @author ParadiseWY
 * @date 2020-11-10 11:25:29
 * @git {@link https://github.com/mygodness100}
 */
public class HDFSReadMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// 从HDFS中读取的数据
		String lineValue = value.toString();
		// 读取出来的每行数据使用\t进行分割,存于String数组
		String[] values = lineValue.split("\t");
		// 根据数据中值的含义取值
		String rowKey = values[0];
		String name = values[1];
		String color = values[2];
		// 初始化rowKey
		ImmutableBytesWritable rowKeyWritable = new ImmutableBytesWritable(Bytes.toBytes(rowKey));
		// 初始化put对象
		Put put = new Put(Bytes.toBytes(rowKey));
		// 参数分别:列族,列,值
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(name));
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("color"), Bytes.toBytes(color));
		context.write(rowKeyWritable, put);
	}
}