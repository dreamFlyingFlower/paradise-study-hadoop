package com.wy.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * HBase整合MR,用于组装运行Job任务
 * 
 * @author ParadiseWY
 * @date 2020-11-10 11:16:58
 * @git {@link https://github.com/mygodness100}
 */
public class FruitDriver extends Configured implements Tool {

	public static void main(String[] args) {
		Configuration conf = HBaseConfiguration.create();
		try {
			int status = ToolRunner.run(conf, new FruitDriver(), args);
			System.exit(status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		// 得到Configuration
		Configuration conf = this.getConf();
		// 创建Job任务
		Job job = Job.getInstance(conf, this.getClass().getSimpleName());
		job.setJarByClass(FruitDriver.class);

		// 配置Job
		Scan scan = new Scan();
		scan.setCacheBlocks(false);
		scan.setCaching(500);

		// 设置Mapper,注意导入的是mapreduce包下的,不是mapred包下的,后者是老版本
		// 数据源的表名,scan扫描控制器,设置Mapper类,Mapper输出的key类型,Mapper输出的value类型,设置给那个Job
		TableMapReduceUtil.initTableMapperJob("fruit", scan, FruitReadMapper.class, ImmutableBytesWritable.class,
				Put.class, job);
		// 设置Reducer
		TableMapReduceUtil.initTableReducerJob("fruit_mr", FruitWriteReducer.class, job);
		// 设置Reduce数量，最少1个
		job.setNumReduceTasks(1);

		boolean isSuccess = job.waitForCompletion(true);
		if (!isSuccess) {
			throw new IOException("Job running with error");
		}
		return isSuccess ? 0 : 1;
	}
}