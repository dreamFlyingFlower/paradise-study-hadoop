package com.wy;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.net.DNSToSwitchMapping;

/**
 * 自定义机架感知,选择最近的服务器访问
 * 
 * 实现特定接口,同时需要在hadoop的配置文件core-site.xml中进行定义
 * 
 * <name>topology.node.switch.mapping.impl</name>
 * <value>com.it18zhang.hdfs.rackaware.DnsSwitch</value>
 * 
 * 将程序打成jar包放到hadoop的lib目录下,重启NN
 * 
 * @author ParadiseWY
 * @date 2020-11-16 14:23:47
 * @git {@link https://github.com/mygodness100}
 */
public class DnsSwitch implements DNSToSwitchMapping {

	@Override
	public List<String> resolve(List<String> names) {
		List<String> list = new ArrayList<String>();
		try {
			FileWriter fw = new FileWriter("/app/config/rackaware.txt", true);
			for (String str : names) {
				// 输出原来的信息,ip地址(主机名)
				fw.write(str + "\r\n");
				if (str.startsWith("192")) {
					String ip = str.substring(str.lastIndexOf(".") + 1);
					if (Integer.parseInt(ip) <= 203) {
						list.add("/rack1/" + ip);
					} else {
						list.add("/rack2/" + ip);
					}
				} else if (str.startsWith("s")) {
					String ip = str.substring(1);
					if (Integer.parseInt(ip) <= 203) {
						list.add("/rack1/" + ip);
					} else {
						list.add("/rack2/" + ip);
					}
				}
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void reloadCachedMappings() {
	}

	@Override
	public void reloadCachedMappings(List<String> names) {
	}
}