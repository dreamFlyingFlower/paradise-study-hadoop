package com.wy.wordcount.cluster;

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

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		line = (String) input.getValueByField("line");
		words = line.split("\t");
		System.out.println("=====bolt========" + Thread.currentThread() + "==" + line);
		for (String word : words) {
			collector.emit(new Values(word));
		}
		this.collector.ack(input);
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}