package com.wy.wordcount;

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
 * 单词计数
 * 
 * mapreduce的驱动类,主要是用来启动一个mapreduce作业
 * 一个mapper,reducer,dirver就完成了一个简单的开发,现在可以在hadoop上进行部署,只需要在master上部署,
 * hadoop会将任务传送给他的slave,需要将打包好的jar放到$HADOOP_HOME下
 * hadoop整合数据库,这种通过mapreduce的方式太慢了,7,8秒,直接使用Map/reduce不合适,
 * 
 * @author ParadiseWY
 * @date 2020-10-21 15:23:13
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountDriver {

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
			Job job = Job.getInstance(conf, "word_count");
			// 设置jar存储位置
			job.setJarByClass(WordCountDriver.class);
			// 关联mapper和reducer
			job.setMapperClass(WordCountMapper.class);
			job.setReducerClass(WordCountReducer.class);
			// 设置mapper阶段输出数据的KV类型
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(IntWritable.class);
			// 设置最终数据输出的KV类型
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			// 设置输入路径和输出路径
			FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
			// 提交job
			// job.setCombinerClass(WordCountReducer.class);
			System.exit(job.waitForCompletion(true) ? 0 : 1);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}