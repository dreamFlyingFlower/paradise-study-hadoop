package com.wy.flowcount;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.wy.model.Flow;

public class FlowCountMapper extends Mapper<LongWritable, Text, Text, Flow> {

	Text k = new Text();

	Flow v = new Flow();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// 数据格式:编号 手机号 ip 域名 上传流量 下载流量 响应编码
		// 6 13560436666 120.196.100.99 www.baidu.com 1116 954 200
		// 7 13560436666 120.196.100.99 						1116 954 200
		// 1 获取一行
		String line = value.toString();
		// 2 切割 \t
		String[] fields = line.split("\t");
		// 3 封装对象
		k.set(fields[1]);
		// 封装手机号,中间数据可能会有空,从最后开始取值
		long upFlow = Long.parseLong(fields[fields.length - 3]);
		long downFlow = Long.parseLong(fields[fields.length - 2]);
		v.setUpFlow(upFlow);
		v.setDownFlow(downFlow);
		// 4 写出
		context.write(k, v);
	}
}