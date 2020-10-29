package com.wy.integration;

import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy.Units;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
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
 * 本地模式使用Storm整合HDFS完成词频统计功能
 * 
 * 官方文档:http://storm.apache.org/releases/1.2.3/storm-hdfs.html
 * 
 * @apiNote 自定义RecordFormats:需要实现{@link org.apache.storm.hdfs.format.RecordFormat}接口,
 *          默认的实现为{@link org.apache.storm.hdfs.format.DelimitedRecordFormat}
 * @apiNote 自定义FileNaming:文件名命名规则,
 *          自定义需要实现{@link org.apache.storm.hdfs.format.FileNameFormat}接口,
 *          默认的实现为{@link org.apache.storm.hdfs.format.DefaultFileNameFormat},
 *          该方式的命名规则如下:{prefix}{componentId}-{taskId}-{rotationNum}-{timestamp}{extension},
 *          通常情况下prefix是空的,extension默认为.txt
 * @apiNote 文件同步策略SyncPolicies:控制缓存数据何时刷到HDFS中,
 *          自定义需要实现{@link org.apache.storm.hdfs.sync.SyncPolicy}接口,
 *          默认实现为{@link org.apache.storm.hdfs.sync.CountSyncPolicy}
 * @apiNote 文件滚动写入策略FileRotationPolicies:控制文件的写入方式,和SyncPolicies类似,
 *          自定义需要实现{@link org.apache.storm.hdfs.rotation.FileRotation}接口,
 *          默认实现为{@link org.apache.storm.hdfs.rotation.FileSizeRotationPolicy}
 * 
 * @author ParadiseWY
 * @date 2020-10-29 14:43:03
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountHdfsTopology {

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

	public static void main(String[] args) {
		// 通过TopologyBuilder根据Spout和Bolt构建Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("DataSourceSpout", new DataSourceSpout());
		// 获得Spout弹射的数据源,通过id->DataSourceSpout
		builder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("DataSourceSpout");

		// 使用|代替,作为默认分隔符
		RecordFormat format = new DelimitedRecordFormat().withFieldDelimiter("|");
		// 同步文件系统直到有1000个tuple
		SyncPolicy syncPolicy = new CountSyncPolicy(1000);
		// 滚动文件直到达到5M
		FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f, Units.MB);
		// 默认方式对文件进行命名
		FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath("/foo/");
		// 需要注意文件权限,见官方文档
		HdfsBolt bolt = new HdfsBolt().withFsUrl("hdfs://192.168.1.150:54310").withFileNameFormat(fileNameFormat)
				.withRecordFormat(format).withRotationPolicy(rotationPolicy).withSyncPolicy(syncPolicy);

		builder.setBolt("HdfsStoreBolt", bolt).shuffleGrouping("SplitBolt");
		// 创建本地集群
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("LocalWordCountRedisTopology", new Config(), builder.createTopology());
	}
}