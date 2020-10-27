package com.wy.index;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class OneIndexMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	Text k = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String[] fields = line.split(" ");
		FileSplit inputSplit = (FileSplit) context.getInputSplit();
		String name = inputSplit.getPath().getName();
		for (int i = 0; i < fields.length; i++) {
			k.set(fields[i] + "--" + name);
			context.write(k, new IntWritable(1));
		}
	}
}