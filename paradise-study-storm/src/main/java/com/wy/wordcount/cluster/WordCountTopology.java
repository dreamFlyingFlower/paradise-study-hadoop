package com.wy.wordcount.cluster;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * 执行
 * 
 * @author ParadiseWY
 * @date 2020-10-28 12:25:46
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		// 第3个参数设置并行度
		// builder.setSpout("spout", new WordReader(), 1);
		// 2个参数，默认并行度为
		builder.setSpout("word-reader", new WordCountSpout());
		builder.setBolt("word-normalizer", new WordCountNormal()).shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCountBolt(), 2).fieldsGrouping("word-normalizer", new Fields("word"));
		// 配置
		Config conf = new Config();
		// 设置此Topology分配2个work
		conf.put(Config.TOPOLOGY_WORKERS, 2);
		conf.setDebug(false);
		try {
			// 分布式提交到storm集群
			StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}