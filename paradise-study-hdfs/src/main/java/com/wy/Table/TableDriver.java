package com.wy.Table;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wy.model.Table;

public class TableDriver {

	public static void main(String[] args) throws Exception {
		Configuration configuration = new Configuration();
		Job job = Job.getInstance(configuration);

		job.setJarByClass(TableDriver.class);

		job.setMapperClass(TableMapper.class);
		job.setReducerClass(TableReduce.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Table.class);

		job.setOutputKeyClass(Table.class);
		job.setOutputValueClass(NullWritable.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		boolean result = job.waitForCompletion(true);
		System.exit(result ? 0 : 1);

	}
}