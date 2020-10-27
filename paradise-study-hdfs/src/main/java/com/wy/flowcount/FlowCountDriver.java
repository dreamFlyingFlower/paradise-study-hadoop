package com.wy.flowcount;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wy.model.Flow;

/**
 * 实现序列化,统计手机流量数据
 * 
 * @author ParadiseWY
 * @date 2020-10-21 16:19:31
 * @git {@link https://github.com/mygodness100}
 */
public class FlowCountDriver {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
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
		// 6 设置输入输出路径
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}