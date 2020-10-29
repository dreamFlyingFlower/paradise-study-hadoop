package com.wy.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.hbase.bolt.HBaseBolt;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;
import org.apache.storm.shade.com.google.common.collect.Maps;
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
public class WordCountHBaseTopology {

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
			String word = input.getStringByField("line");
			this.collector.emit(new Values(word));
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
	}

	/**
	 * 词频汇总Bolt
	 */
	public static class CountBolt extends BaseRichBolt {

		private static final long serialVersionUID = 7525401744245627728L;

		private OutputCollector collector;

		public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		Map<String, Integer> map = new HashMap<String, Integer>();

		/**
		 * 业务逻辑:1.获取每个单词;2.对所有单词进行汇总;3输出
		 */
		public void execute(Tuple input) {
			// 1.）获取每个单词
			String word = input.getStringByField("word");
			Integer count = map.get(word);
			if (count == null) {
				count = 0;
			}
			count++;
			// 2.对所有单词进行汇总
			map.put(word, count);
			// 3.输出
			collector.emit(new Values(word, map.get(word)));
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}
	}

	public static void main(String[] args) {
		// 通过TopologyBuilder根据Spout和Bolt构建Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("DataSourceSpout", new DataSourceSpout());
		// 获得Spout弹射的数据源,通过id->DataSourceSpout
		builder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("DataSourceSpout");
		// 获得SplitBolt弹射的数据源
		builder.setBolt("CountBolt", new CountBolt()).shuffleGrouping("SplitBolt");
		// 构建hbase的一些链接参数
		Config hbaseConfig = new Config();
		Map<Object, Object> map = Maps.newHashMap();
		map.put("hbase.rootdir", "hdfs://192.168.1.150:8020/hbase");
		map.put("hbase.zookeeper.quorum", "192.168.1.150:2181");
		hbaseConfig.put("hbase.config", map);
		SimpleHBaseMapper mapper = new SimpleHBaseMapper().withRowKeyField("word").withColumnFields(new Fields("word"))
				.withCounterFields(new Fields("count")).withColumnFamily("cf");
		// 第一个参数是表名,需要根据实际情况修改
		// configkey需要和上面的hbaseConfig中的key一样,这是为了保证能找到hbase的地址
		HBaseBolt hbase = new HBaseBolt("WordCount", mapper).withConfigKey("hbase.config");
		builder.setBolt("HBaseStoreBolt", hbase).shuffleGrouping("CountBolt");
		// 创建本地集群
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("LocalWordCountRedisTopology", hbaseConfig, builder.createTopology());
	}
}