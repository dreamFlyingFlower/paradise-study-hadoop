package com.wy.example;

import java.io.IOException;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.NullWritable;

/**
 * HBase整合MR,用于将读取到的fruit表中的数据写入到fruit_mr表中
 * 
 * @author ParadiseWY
 * @date 2020-11-10 11:15:27
 * @git {@link https://github.com/mygodness100}
 */
public class FruitWriteReducer extends TableReducer<ImmutableBytesWritable, Put, NullWritable> {

	@Override
	protected void reduce(ImmutableBytesWritable key, Iterable<Put> values, Context context)
			throws IOException, InterruptedException {
		// 读出来的每一行数据写入到fruit_mr表中
		for (Put put : values) {
			context.write(NullWritable.get(), put);
		}
	}
}
