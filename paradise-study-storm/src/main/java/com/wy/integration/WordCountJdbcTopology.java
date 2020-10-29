package com.wy.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
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
 * 本地模式使用Storm整合jdbc完成词频统计功能
 * 
 * 官方文档:http://storm.apache.org/releases/1.2.3/storm-jdbc.html
 * 
 * @author ParadiseWY
 * @date 2020-10-29 14:43:03
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountJdbcTopology {

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
		// HikariCPConnectionProvider是Storm已经实现好的类,若想自定义连接,需要实现{@link
		// ConnectionProvider}
		Map<String, Object> hikariConfigMap = Maps.newHashMap();
		// 数据库连接
		hikariConfigMap.put("dataSourceClassName", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		hikariConfigMap.put("dataSource.url", "jdbc:mysql://localhost/test");
		hikariConfigMap.put("dataSource.user", "root");
		hikariConfigMap.put("dataSource.password", "password");
		ConnectionProvider connectionProvider = new HikariCPConnectionProvider(hikariConfigMap);

		String tableName = "user_details";
		JdbcMapper simpleJdbcMapper = new SimpleJdbcMapper(tableName, connectionProvider);
		// 从数据库中查询数据
		// JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider,
		// simpleJdbcMapper)
		// .withTableName("user")
		// .withQueryTimeoutSecs(30);
		// 往数据库中插入数据
		JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
				.withInsertQuery("insert into user values (?,?)").withQueryTimeoutSecs(30);
		// 从CountBolt中获得的字段个数和字段名要和数据库表中的个数和字段名相同
		builder.setBolt("JdbcStoreBolt", userPersistanceBolt).shuffleGrouping("CountBolt");
		// 创建本地集群
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("LocalWordCountRedisTopology", new Config(), builder.createTopology());
	}
}