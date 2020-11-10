package com.wy.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 整合HDFS,组装Job
 * 
 * @author ParadiseWY
 * @date 2020-11-10 11:29:14
 * @git {@link https://github.com/mygodness100}
 */
public class HDFSDriver extends Configured implements Tool {
	
	public static void main(String[] args) {
		Configuration conf = HBaseConfiguration.create();
		try {
			int status = ToolRunner.run(conf, new HDFSDriver(), args);
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
		job.setJarByClass(HDFSDriver.class);
		Path inPath = new Path("hdfs://linux01:8020/input_fruit/fruit.tsv");
		FileInputFormat.addInputPath(job, inPath);

		// 设置Mapper
		job.setMapperClass(HDFSReadMapper.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		// 设置Reducer
		TableMapReduceUtil.initTableReducerJob("fruit_mr", HDFSWriteReducer.class, job);

		// 设置Reduce数量，最少1个
		job.setNumReduceTasks(1);

		boolean isSuccess = job.waitForCompletion(true);
		if (!isSuccess) {
			throw new IOException("Job running with error");
		}
		return isSuccess ? 0 : 1;
	}
}