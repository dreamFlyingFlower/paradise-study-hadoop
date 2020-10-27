package com.wy.Table;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.wy.model.Table;

public class TableMapper extends Mapper<LongWritable, Text, Text, Table> {

	Table bean = null;

	Text k = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		FileSplit inputSplit = (FileSplit) context.getInputSplit();
		String name = inputSplit.getPath().getName();
		String line = value.toString();

		/**
		 * 不同文件分别处理
		 */
		if (name.startsWith("order")) {
			// 订单相关信息处理
			String[] fields = line.split("\t");
			bean = Table.builder().order_id(fields[0]).p_id(fields[1]).amount(Integer.parseInt(fields[2])).pname("")
					.flag("0").build();
			k.set(fields[1]);
		} else {
			// 产品表信息处理
			String[] fields = line.split("\t");
			bean = Table.builder().order_id("").p_id(fields[0]).amount(0).pname(fields[1]).flag("1").build();
			k.set(fields[0]);
		}
		context.write(k, bean);
	}
}