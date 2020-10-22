package com.wy.sort;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FlowSortReducer extends Reducer<FlowSort, Text, Text, FlowSort> {

	@Override
	protected void reduce(FlowSort key, Iterable<Text> values, Reducer<FlowSort, Text, Text, FlowSort>.Context context)
			throws IOException, InterruptedException {
		// 13736230513 2481 24681 27162
		for (Text value : values) {
			context.write(value, key);
		}
	}
}