package com.wy.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 主要是获取map方法的key-value结果,相同的key发送到同意reduce里处理,然后迭代key,把value相加,结果写到hdfs
 * 
 * @author ParadiseWY
 * @date 2020-10-21 15:22:07
 * @git {@link https://github.com/mygodness100}
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	private IntWritable result = new IntWritable();

	/**
	 * @param key:Map端输出的key
	 * @param values:Map端输出的value集合,相同key的集合
	 * @param context:recude的上下文
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		result.set(sum);
		context.write(key, result);
	}
}