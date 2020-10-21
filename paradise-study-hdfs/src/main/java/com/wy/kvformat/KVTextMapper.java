package com.wy.kvformat;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KVTextMapper extends Mapper<Text, Text, Text, IntWritable> {

	IntWritable v = new IntWritable(1);

	@Override
	protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		context.write(key, v);
	}
}