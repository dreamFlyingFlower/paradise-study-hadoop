package com.wy.integration;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * 本地模式使用Storm整合HBase完成词频统计功能
 * 
 * 官方文档:http://storm.apache.org/releases/1.2.3/storm-hbase.html
 * 
 * @apiNote 主要接口是{@link org.apache.storm.hbase.bolt.mapper.HBaseMapper},
 *          默认的实现为{@link org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper}
 * 
 * @author ParadiseWY
 * @date 2020-10-29 14:43:03
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountKafkaTopology {

	public static class DataSourceSpout extends BaseRichSpout {

		private static final long serialVersionUID = 1080417784433980983L;

		public static final String[] WORDS = new String[] { "apple", "orange", "banana", "purple" };

		private SpoutOutputCollector collector;

		public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			this.collector = collector;
		}

		public void nextTuple() {
			Random random = new Random(WORDS.length);
			String word = WORDS[random.nextInt()];
			this.collector.emit(new Values(word));
			Utils.sleep(1000);
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("line"));
		}
	}

	/**
	 * 对数据进行分割
	 */
	public static class SplitBolt extends BaseRichBolt {

		private static final long serialVersionUID = -3082554641650091855L;

		private OutputCollector collector;

		public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		/**
		 * 业务逻辑,从Spout中取出emit的line资源
		 */
		public void execute(Tuple input) {
			String line = input.getStringByField("line");
			this.collector.emit(new Values(line));
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
	}

	public static void main(String[] args) {
		Config conf = new Config();
		conf.setNumWorkers(2);
		conf.setDebug(true);
		// zk集群地址,多个用逗号隔开
		BrokerHosts hosts = new ZkHosts("192.168.1.150:2181");
		// Spout配置
		SpoutConfig spoutConfig = new SpoutConfig(hosts, "test2", "/test2", UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		// 通过TopologyBuilder根据Spout和Bolt构建Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("DataSourceSpout", kafkaSpout).setNumTasks(2);
		// 获得Spout弹射的数据源,通过id->DataSourceSpout
		builder.setBolt("SplitBolt", new SplitBolt(), 2).shuffleGrouping("DataSourceSpout").setNumTasks(2);
		// 创建本地集群
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("LocalWordCountKafkaTopology", conf, builder.createTopology());
	}
}