package com.wy.pv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * 获取数据源
 * 
 * @author ParadiseWY
 * @date 2020-10-29 09:32:57
 * @git {@link https://github.com/mygodness100}
 */
public class PvSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;

	private SpoutOutputCollector collector;

	private BufferedReader reader;

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream("e:/website.log"), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void activate() {

	}

	@Override
	public void deactivate() {

	}

	private String str;

	@Override
	public void nextTuple() {
		try {
			while ((str = reader.readLine()) != null) {
				collector.emit(new Values(str));
				Utils.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ack(Object msgId) {
	}

	@Override
	public void fail(Object msgId) {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}