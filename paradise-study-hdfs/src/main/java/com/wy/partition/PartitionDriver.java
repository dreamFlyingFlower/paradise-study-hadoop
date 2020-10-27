package com.wy.partition;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wy.flowcount.FlowCountDriver;
import com.wy.flowcount.FlowCountMapper;
import com.wy.flowcount.FlowCountReducer;
import com.wy.model.Flow;

/**
 * 使用flowcount演示分区
 * 
 * @author ParadiseWY
 * @date 2020-10-27 09:40:46
 * @git {@link https://github.com/mygodness100}
 */
public class PartitionDriver {

	public static void main(String[] args)
			throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {

		args = new String[] { "e:/input/inputflow", "e:/output1" };
		Configuration conf = new Configuration();
		// 1 获取job对象
		Job job = Job.getInstance(conf);
		// 2 设置jar的路径
		job.setJarByClass(FlowCountDriver.class);
		// 3 关联mapper和reducer
		job.setMapperClass(FlowCountMapper.class);
		job.setReducerClass(FlowCountReducer.class);
		// 4 设置mapper输出的key和value类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Flow.class);
		// 5 设置最终输出的key和value类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Flow.class);
		// 6 设置自定义分区类
		job.setPartitionerClass(ProvincePartitioner.class);
		// 7 设置分区数量
		job.setNumReduceTasks(6);
		// 8 设置输入输出路径
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}