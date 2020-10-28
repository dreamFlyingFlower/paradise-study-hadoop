package com.wy.wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * 执行
 * 
 * @author ParadiseWY
 * @date 2020-10-28 12:25:46
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountTopology {

	public static void main(String[] args) {
		// 定义一个Topology
		TopologyBuilder builder = new TopologyBuilder();
		// 第3个参数设置并行度
		// builder.setSpout("spout", new WordReader(), 1);
		// 2个参数，默认并行度为
		builder.setSpout("word-reader", new WordCountSpout());
		builder.setBolt("word-normalizer", new WordCountNormal()).shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCountBolt(), 2).fieldsGrouping("word-normalizer", new Fields("word"));
		// 配置
		Config conf = new Config();
		conf.put("wordsFile", "E:/test.txt");
		conf.setDebug(false);
		// 本地提交模式，例如eclipse执行main方法
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("mytopology", conf, builder.createTopology());
		Utils.sleep(15000);
		// 停止本地运行
		localCluster.shutdown();
	}
}