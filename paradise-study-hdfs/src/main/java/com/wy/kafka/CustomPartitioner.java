package com.wy.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * 用户自定义分区
 *
 * @author ParadiseWY
 * @date 2020-10-26 20:42:31
 * @git {@link https://github.com/mygodness100}
 */
public class CustomPartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {

	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		// 控制分区
		return 1;
	}

	@Override
	public void close() {

	}
}