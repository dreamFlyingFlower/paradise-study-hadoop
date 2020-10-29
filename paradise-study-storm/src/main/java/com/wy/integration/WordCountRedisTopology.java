package com.wy.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * 本地模式使用Storm整合redis完成词频统计功能
 * 
 * 官方文档:http://storm.apache.org/releases/1.2.3/storm-redis.html
 * 
 * @author ParadiseWY
 * @date 2020-10-29 14:43:03
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountRedisTopology {

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

	/**
	 * 从CountBolt中取出emit的Fields,存入到redis中
	 *
	 * @author ParadiseWY
	 * @date 2020-10-29 20:40:59
	 * @git {@link https://github.com/mygodness100}
	 */
	public static class WordCountStoreMapper implements RedisStoreMapper {

		private static final long serialVersionUID = 7874154500288291967L;

		private RedisDataTypeDescription description;

		private final String hashKey = "wordCount";

		public WordCountStoreMapper() {
			description = new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.HASH, hashKey);
		}

		@Override
		public String getKeyFromTuple(ITuple tuple) {
			return tuple.getStringByField("word");
		}

		@Override
		public String getValueFromTuple(ITuple tuple) {
			return tuple.getIntegerByField("count") + "";
		}

		@Override
		public RedisDataTypeDescription getDataTypeDescription() {
			return description;
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
		JedisPoolConfig poolConfig = new JedisPoolConfig.Builder().setHost("localhost").setPort(6379).build();
		RedisStoreMapper storeMapper = new WordCountStoreMapper();
		RedisStoreBolt storeBolt = new RedisStoreBolt(poolConfig, storeMapper);
		builder.setBolt("RedisStoreBolt", storeBolt).shuffleGrouping("CountBolt");
		// 创建本地集群
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("LocalWordCountRedisTopology", new Config(), builder.createTopology());
	}
}