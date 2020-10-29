package com.wy.pv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

/**
 * 数据汇总处理
 * 
 * @author ParadiseWY
 * @date 2020-10-29 09:35:22
 * @git {@link https://github.com/mygodness100}
 */
public class PvSumBolt implements IRichBolt {

	private static final long serialVersionUID = 1L;

	private Map<Long, Long> counts = new HashMap<>();

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
	}

	@Override
	public void execute(Tuple input) {
		Long threadID = input.getLong(0);
		Long pv = input.getLong(1);
		counts.put(threadID, pv);
		long word_sum = 0;
		Iterator<Long> iterator = counts.values().iterator();
		while (iterator.hasNext()) {
			word_sum += iterator.next();
		}
		System.err.println("pv_all:" + word_sum);
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}