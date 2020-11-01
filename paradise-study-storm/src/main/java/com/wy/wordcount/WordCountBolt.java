package com.wy.wordcount;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

/**
 * 接收WordCountNormal发射的单词,进行计数
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
		this.counters = new HashMap<String, Integer>();
		this.collector = collector;
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
	}

	/**
	 * 处理输入的一个单一tuple.每次接收到元组时都会被调用一次,还会再发布若干个元组
	 */
	@Override
	public void execute(Tuple input) {
		String word = input.getString(0);
		// 计数
		if (!counters.containsKey(word)) {
			counters.put(word, 1);
		} else {
			Integer c = counters.get(word) + 1;
			counters.put(word, c);
		}
		// 成功,提示从此spout喷出的带有messageID的tuple已被完全处理,把消息从队列中移走,避免被再次处理
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
		System.out.println("-- Word Counter [" + name + "-" + id + "] -- 	");
		for (Map.Entry<String, Integer> entry : counters.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		counters.clear();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 不再向外发射数据，此处不写代码
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