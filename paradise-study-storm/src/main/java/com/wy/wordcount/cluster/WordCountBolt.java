package com.wy.wordcount.cluster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

/**
 * 接收WordCountNormal发射的单词,进行计数,使用tick定时机制,定时把统计结果通过JDBC的方式保存到数据库
 * 
 * @author ParadiseWY
 * @date 2020-10-28 12:20:21
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountBolt implements IRichBolt {

	private static final long serialVersionUID = 1L;

	Integer id;

	String name;

	Map<String, Integer> counters;

	private OutputCollector collector;

	public static final String url = "jdbc:mysql://master:3306/test?useUnicode=true&characterEncoding=utf8";

	public static final String dbname = "com.mysql.jdbc.Driver";

	public static final String user = "hadoop";

	public static final String password = "hadoop";

	public static Connection conn = null;

	public static PreparedStatement pst = null;

	public static ResultSet rs = null;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.counters = new HashMap<String, Integer>();
		this.collector = collector;
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
	}

	@Override
	public void execute(Tuple input) {
		if (input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
				&& input.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID)) {
			// 如果当前得到的tuple是tick tuple，则保存数据到数据库，
			// 我们在getComponentConfiguration()方法中，设置的是每6秒发送一个tick tuple
			// 也就是说每6秒一次的频率把统计结果保存到数据库
			System.out.println("存储数据开始");
			updateWordCount(counters);
			System.out.println("存储数据结束");
		} else {
			// 正常的计数逻辑
			String word = input.getString(0);
			// 计数
			if (!counters.containsKey(word)) {
				counters.put(word, 1);
			} else {
				Integer c = counters.get(word) + 1;
				counters.put(word, c);
			}
		}
		this.collector.ack(input);
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 不再向外发射数据，此处不写代码
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		// 设置定时向当前的bolt发送tick tuple的时间,单位是秒
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 6);
		return conf;
	}

	/***
	 * 向数据库添加或更新单词计数结果*
	 * 
	 * @param counters
	 */
	public void updateWordCount(Map<String, Integer> counters) {
		try {
			Class.forName(dbname);
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			int count = 0;
			if (counters != null && counters.size() > 0) {
				String sql = "";
				for (Map.Entry<String, Integer> entry : counters.entrySet()) {
					sql = "select count(1) from wordcount where word=?";
					pst = conn.prepareStatement(sql);
					pst.setString(1, entry.getKey());
					rs = pst.executeQuery();
					while (rs.next()) {
						count = rs.getInt(1);
					}
					if (count > 0) {
						sql = "update wordcount set count = ? where word= ?";
						pst = conn.prepareStatement(sql);
						pst.setString(1, String.valueOf(entry.getValue()));
						pst.setString(2, entry.getKey());
					} else {
						sql = "insert into wordcount (word,count) values(?,?)";
						pst = conn.prepareStatement(sql);
						pst.setString(1, entry.getKey());
						pst.setString(2, entry.getValue().toString());
					}
					pst.executeUpdate();
					conn.commit();
				}
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}