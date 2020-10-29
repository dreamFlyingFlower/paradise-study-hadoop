package com.wy.uv;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/**
 * UV(user views):count(distinct session_id),即独立访客数
 * 
 * 1.用ip地址分析<br>
 * 指访问某个站点或点击某个网页的不同IP地址的人数.<br>
 * 在同一天内,UV只记录第一次进入网站的具有独立IP的访问者,在同一天内再次访问该网站则不计数<br>
 * 2.用Cookie分析UV值<br>
 * 当客户端第一次访问某个网站服务器时,网站服务器会给这个客户端的电脑发出一个Cookie,通常放在客户端电脑C盘中.
 * 在这个Cookie中会分配一个独一无二的编号,这其中会记录一些访问服务器的信息,如访问时间,访问了哪些页面等等.
 * 当下次再访问这个服务器时,服务器就可以直接从电脑中找到上一次的Cookie,并且对其进行更新,但那个编号是不会变的
 * 
 * 需求分析<br>
 * 方案一:<br>
 * 把ip放入Set实现自动去重,Set.size()获得UV,但是在分布式应用中不可行<br>
 * 方案二:<br>
 * UvBolt通过fieldGrouping进行多线程局部汇总,下一级UvSumBolt进行单线程全局汇总去重.按ip地址统计UV数<br>
 * 既然去重，必须持久化数据:<br>
 * 1.内存:数据结构map<br>
 * 2.no-sql分布式数据库,如Hbase
 * 
 * @author ParadiseWY
 * @date 2020-10-29 09:28:08
 * @git {@link https://github.com/mygodness100}
 */
public class UvTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("UvSpout", new UvSpout(), 1);
		builder.setBolt("UvBolt", new UvBolt(), 4).shuffleGrouping("UvSpout");
		builder.setBolt("UVSumBolt", new UvSumBolt(), 1).shuffleGrouping("UvBolt");
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
			cluster.submitTopology("uvtopology", conf, builder.createTopology());
		}
	}
}