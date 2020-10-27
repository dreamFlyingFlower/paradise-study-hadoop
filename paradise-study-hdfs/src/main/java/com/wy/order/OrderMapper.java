package com.wy.order;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.wy.model.Order;

public class OrderMapper extends Mapper<LongWritable, Text, Order, NullWritable> {

	Order bean = new Order();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String[] fields = line.split("\t");
		// Order_0000002 Pdt_03 522.8
		bean.setOrderId(fields[0]);
		bean.setPrice(Double.parseDouble(fields[2]));
		context.write(bean, NullWritable.get());
	}
}