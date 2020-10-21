package com.wy.wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 继承Mapper,实现其中的map方法,map方法主要是把字符串解析成key-value的形式,发给reduce统计
 * 
 * @apiNote Mapper的泛型类型:<br>
 *          KEYIN:输入数据的key类型<br>
 *          VALUEIN:输入数据的value类型<br>
 *          KEYOUT:输出数据的key类型<br>
 *          VALUEOUT:输出数据的value类型
 * 
 * @author ParadiseWY
 * @date 2020-10-21 15:15:50
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {

	private static final IntWritable ONE = new IntWritable(1);

	private Text word = new Text();

	/**
	 * key:每行文件的偏移量 value:每行文件的内容 context:Map端的上下文,包含了OutputCollector和Reporter的功能
	 */
	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString());
		while (itr.hasMoreTokens()) {
			word.set(itr.nextToken());
			context.write(word, ONE);
		}
	}
}