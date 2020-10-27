package com.wy.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DistributedMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

	// 缓存pd.txt数据
	private Map<String, String> pdMap = new HashMap<>();

	Text k = new Text();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		// 读取pd.txt文件,并把数据存储到缓存(集合)
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("pd.txt"))));
		String line;
		// 01 小米
		while (StringUtils.isNotEmpty(line = reader.readLine())) {
			String[] fields = line.split("\t");
			// 存储数据到缓存
			pdMap.put(fields[0], fields[1]);
		}
		reader.close();
	}

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// 需求:要合并pd.txt和order.txt里面的内容
		// 1 获取一行 1001 01 4 pdName
		String line = value.toString();
		// 2 截取 1001 01 4
		String[] fields = line.split("\t");
		// 3 获取pdname
		String pdName = pdMap.get(fields[1]);
		// 4 拼接 1001 01 4 pdName
		k.set(line + "\t" + pdName);
		// 5 写出
		context.write(k, NullWritable.get());
	}
}