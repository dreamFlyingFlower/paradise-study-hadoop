package com.wy.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * mapreduce的驱动类,主要是用来启动一个mapreduce作业
 * 一个mapper,reducer,main就完成了一个简单的开发,现在可以在hadoop上进行部署,只需要在master上部署,
 * hadoop会将任务传送给他的slave,需要将打包好的jar放到$HADOOP_HOME下
 * hadoop整合数据库,这种通过mapreduce的方式太慢了,7,8秒,直接使用Map/reduce不合适,
 * hadoop配合hbase使用才是最好的,加上zookeeper的协同调度,加上hive对hbase的操作
 * https://blog.csdn.net/huiweizuotiandeni/article/details/59632613 hadoop安装伪分布式
 * https://www.cnblogs.com/DarrenChan/p/6418416.html centos免密码登录
 * http://people.apache.org/~srimanth/hadoop-eclipse/update-site/
 * ifconfig没有网,使用dhclient 网络名称
 * @author paradiseWy
 */
public class TestMain {

	public static void main(String[] args) {
		// 读取hadoop的配置文件,如site-core.xml,mapred-site.xml,hdfs-site.xml等
		// 也可以利用set方法设置,如conf.set("fs.default.name","hdfs://ip:port"),set方法设置的值会替代配置文件的值
		Configuration conf = new Configuration();
		try {
			String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
			System.out.println(otherArgs.length);
			if (otherArgs.length != 2) {
				System.err.println("Uage:wordcount<in><Out>");
				System.exit(2);
			}
			// job表示一个mapreduce任务,第二个参数是job的名称,等同于任务名称
			Job job = Job.getInstance(conf, "word count");
			job.setJarByClass(TestMain.class);
			job.setMapperClass(TestMapper.class);
			job.setReducerClass(TestReducer.class);
			job.setCombinerClass(TestReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}