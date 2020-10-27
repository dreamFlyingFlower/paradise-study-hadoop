package com.wy.order;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wy.model.Order;

public class OrderDriver {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(OrderDriver.class);
		job.setMapperClass(OrderMapper.class);
		job.setReducerClass(OrderReducer.class);
		job.setMapOutputKeyClass(Order.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Order.class);
		job.setOutputValueClass(NullWritable.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		// // 设置reduce端的分组
		// job.setGroupingComparatorClass(OrderSortGroupingComparator.class);
		// 关联groupingComparator
		job.setGroupingComparatorClass(OrderGroupingCompartor.class);
		// 设置分区
		job.setPartitionerClass(OrderPatitioner.class);
		// 设置reduce个数
		job.setNumReduceTasks(3);
		boolean result = job.waitForCompletion(true);
		System.exit(result ? 0 : 1);

	}
}