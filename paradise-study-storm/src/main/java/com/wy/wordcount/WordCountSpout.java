package com.wy.wordcount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * 对文本文件中的单词进行计数,分隔符为制表符:\t.本类对文件中的数据进行读取,需要实现相应接口
 * 
 * @author ParadiseWY
 * @date 2020-10-28 10:44:39
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;

	private InputStream is;

	private InputStreamReader isr;

	private BufferedReader br;

	private String line = "";

	private SpoutOutputCollector collector;

	/**
	 * 从此spout发射的带有messageid的tuple处理成功时调用此方法.<br>
	 * 该方法的一个典型实现是把消息从队列中移走,避免被再次处理
	 */
	@Override
	public void ack(Object arg0) {
	}

	/**
	 * 当thread运行完spout实例的open方法后,该spout实例处于deactivate(失效)模式,
	 * 过段时间会变成activated(激活)模式,此时会调用Spout实例的activate方法
	 */
	@Override
	public void activate() {

	}

	/**
	 * Topology终止时,执行该方法
	 */
	@Override
	public void close() {
		try {
			this.br.close();
			this.isr.close();
			this.is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在Topology运行过程中,通过客户端执行deactivate命令,禁用指定的Topology时,
	 * 被禁用的Topology的Spout实例会变成deactivate(失效),并且会调用spout实例的deactivate方法
	 */
	@Override
	public void deactivate() {
	}

	/**
	 * 从此spout发射的带有messageId的tuple处理失败时调用该方法
	 */
	@Override
	public void fail(Object arg0) {
	}

	/**
	 * 接口该接口实现具体的读取数据源的方法,当该方法被调用时,要求SpoutOutputCollector喷射tuple
	 * 
	 * 在本例中,nextTuple()方法是负责对txt文档转换成的流进行逐行读取,将获取的数据emit(发射)出去,<br>
	 * emit的对象是通过public Values createValues(String line)方法生成的backtype.storm.tuple.Values对象.
	 * 该方法从数据源的一行数据中,选取的若干个目标值组成一个backtype.storm.tuple.Values对象.
	 * 该对象可以存储不同类型的对象,例如可以同时将String,Long存取在一个backtype.storm.tuple.Values中emit出去.
	 * 实际上只要实现了Storm要求的序列化接口的对象都可以存储在里面.
	 * 
	 * emit该值得时候需要注意,他的内容要和declareOutputFields中声明的backtype.storm.tuple.Fields对象相匹配,
	 * 必须一一对应,他们被共同组成一个backtype.storm.tuple.Tuple元组对象,被后面接收该 数据流的对象使用
	 */
	@Override
	public void nextTuple() {
		try {
			while ((this.line = this.br.readLine()) != null) {
				// 数据过滤
				// 发射数据
				this.collector.emit(new Values(this.line), UUID.randomUUID());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 当一个Task被初始化的时候会调用此open方法.<br>
	 * 在这里要将collector对象保存下来,供后面的nextTuple()方法使用,还可以执行一些其他的操作<br>
	 * 例如这里将txt文档转换成流,也就是初始化操作
	 * 
	 * @param map 创建Topology时的配置
	 * @param context 所有的Topology数据
	 * @param collector 将Spout的数据发射给bolt
	 */
	@Override
	public void open(@SuppressWarnings("rawtypes") Map map, TopologyContext context, SpoutOutputCollector collector) {
		try {
			this.collector = collector;
			this.is = WordCountSpout.class.getClassLoader().getResourceAsStream("wordcount.txt");
			this.isr = new InputStreamReader(is, "utf-8");
			this.br = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 该接口要声明本Spout要发射(emit)的数据结构,即一个backtype.storm.tuple.Fields对象.
	 * 这个对象和nextTuple方法中emit的backtype.storm.tuple.Values共同组成了一个Tuple,供后面接收该数据的Blot使用,
	 * 运行TopologyBuilder的createTopology()时调用此方法
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line"));
	}

	/**
	 * 运行TopologyBuilder的createTopology方法时调用该方法.用于输出特定于Spout和Bolt实例的配置参数键值对,
	 * 该方法用于声明针对当前组件的特殊的Configuration配置
	 */
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// 设置Topology中当前组件的线程数量上限为3
		// Map<String, Object> ret = new HashMap<String, Object>();
		// ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 3);
		// return ret;
		return null;
	}
}