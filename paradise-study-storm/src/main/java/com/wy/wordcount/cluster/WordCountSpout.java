package com.wy.wordcount.cluster;

import java.util.Map;
import java.util.UUID;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * 读取字符串数组,按行发射给bolt
 * 
 * @author ParadiseWY
 * @date 2020-10-28 10:44:39
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;

	private SpoutOutputCollector collector;

	String[] lines = { "storm hadoop hive zookeeper spark", "hadoop hadoop storm spark storm",
			"storm spark storm hadoop hadoop" };

	int i = 0;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector) {
		try {
			this.collector = collector;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void activate() {
	}

	@Override
	public void nextTuple() {
		// 由于Topology在运行之后,nextTuple方法是无限循环执行的,在这里设置数组lines的元素发射完成之后不再发射数据
		if (i < lines.length) {
			for (String line : lines) {
				// 数据过滤
				System.out.println("=====sput========" + Thread.currentThread() + "==" + line);
				// 发射数据
				this.collector.emit(new Values(line), UUID.randomUUID());
				i++;
			}
		}
	}

	@Override
	public void ack(Object msgId) {
		System.out.println("OK:" + msgId);
	}

	@Override
	public void fail(Object msgId) {
		System.out.println("FAIL:" + msgId);
	}

	@Override
	public void close() {
	}

	@Override
	public void deactivate() {
	}
}