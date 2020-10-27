package com.wy.filter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FilterDriver {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(FilterDriver.class);
		job.setMapperClass(FilterMapper.class);
		job.setReducerClass(FilterReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		// 要将自定义的输出格式组件设置到job中
		job.setOutputFormatClass(FilterOutputformat.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		// 自定义的outputformat继承fileoutputformat,而fileoutputformat要输出一个_SUCCESS文件,所以,还要指定一个输出目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean result = job.waitForCompletion(true);
		System.exit(result ? 0 : 1);
	}
}