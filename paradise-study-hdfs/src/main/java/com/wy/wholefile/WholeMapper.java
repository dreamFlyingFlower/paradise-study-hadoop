package com.wy.wholefile;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WholeMapper extends Mapper<NullWritable, BytesWritable, Text, BytesWritable> {

	Text k = new Text();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		FileSplit inputSplit = (FileSplit) context.getInputSplit();
		Path path = inputSplit.getPath();
		k.set(path.toString());
	}

	@Override
	protected void map(NullWritable key, BytesWritable value, Context context)
			throws IOException, InterruptedException {
		context.write(k, value);
	}
}