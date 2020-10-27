package com.wy.order;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.wy.model.Order;

public class OrderReducer extends Reducer<Order, NullWritable, Order, NullWritable> {

	@Override
	protected void reduce(Order bean, Iterable<NullWritable> values, Context context)
			throws IOException, InterruptedException {
		context.write(bean, NullWritable.get());
	}
}