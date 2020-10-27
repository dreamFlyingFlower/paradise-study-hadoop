package com.wy.order;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

import com.wy.model.Order;

public class OrderPatitioner extends Partitioner<Order, NullWritable> {

	@Override
	public int getPartition(Order key, NullWritable value, int numPartitions) {
		// 按照key的orderid的hashCode值分区
		return (key.getOrderId().hashCode() & Integer.MAX_VALUE) % numPartitions;
	}
}
