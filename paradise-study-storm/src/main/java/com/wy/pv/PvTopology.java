package com.wy.pv;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/**
 * PV(page views):count(session_id),即页面浏览量
 * 
 * PvBolt进行多并发局部汇总,PVSumbolt单线程进行全局汇总<br>
 * 1.线程安全:多线程处理的结果和单线程一致<br>
 * 2.绝对准确:如果用filedGrouping可以得到中间值,如单个user的访问PV,访问深度等<br>
 * 3.计算量稍大,且多一个Bolt
 * 
 * @author ParadiseWY
 * @date 2020-10-29 09:27:03
 * @git {@link https://github.com/mygodness100}
 */
public class PvTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("PvSpout", new PvSpout(), 1);
		builder.setBolt("PvBolt", new PvBolt(), 4).shuffleGrouping("PvBolt");
		builder.setBolt("PvSumBolt", new PvSumBolt(), 1).shuffleGrouping("PvBolt");
		Config conf = new Config();
		conf.setNumWorkers(2);
		if (args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("pvtopology", conf, builder.createTopology());
		}
	}
}