package com.wy.wordcount;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * 解析spout发射的tuple,拆分成一个个单词发射给下一个bolt
 * 
 * @author ParadiseWY
 * @date 2020-10-28 12:11:29
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountNormal implements IRichBolt {

	private static final long serialVersionUID = 1L;

	private OutputCollector collector;

	private String line;

	private String[] words;

	/**
	 * 当该组件的task在集群中的一台worker内被初始化时,该函数被调用.它向bolt提供了该bolt执行的环境<br>
	 * 在这里要将 collector对象保存下来
	 * 
	 * @param stormConf 创建Topology时的配置
	 * @param context 所有的Topology数据
	 * @param collector 将Bolt的数据发射给下一个bolt
	 */
	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	/**
	 * 处理输入的一个单一tuple.每次接收到元组时都会被调用一次,还会再发布若干个元组
	 */
	@Override
	public void execute(Tuple input) {
		// line = (String)input.getValue(0);
		// 与上面等价
		line = (String) input.getValueByField("line");
		words = line.split("\t");
		for (String word : words) {
			collector.emit(new Values(word));
		}
		// 成功,提示从此spout喷出的带有messageID的tuple已被完全处理,把消息 从队列中移走,避免被再次处理
		this.collector.ack(input);
	}

	/**
	 * Topology执行完毕的清理工作,比如关闭连接,释放资源等操作都会写在这里,topology终止时,执行此方法
	 * 
	 * 注意:由于cleanup方法并不可靠,它只在local mode下生效,<br>
	 * Storm集群模式下cleanup不会被调用执行,很多资源得不到释放,所以,在kill topology之前,先deactivate相应的topology.
	 * bolt中判断接收的数据为"shutDown"就调用cleanup()方法.在cleanup() 方法中释放需要释放的资源.
	 */
	@Override
	public void cleanup() {

	}

	/**
	 * 此方法用于声明当前bolt的Tuple发送流的域名字,即一个 backtype.storm.tuple.Fields对象.
	 * 该对象和execute()方法中emit的backtype.storm.tuple.Values共同组成了一个元组对象Tuple,供后面的Blot使用
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

	/**
	 * 用于输出特定于Spout和Bolt实例的配置参数值对<br>
	 * 此方法用于声明针对当前组件的特殊的Configuration配置,在需要的情况下会进 行配置
	 */
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}