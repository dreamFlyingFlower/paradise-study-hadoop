package com.wy.video;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 利用hadoop对原始数据进行清洗
 * 
 * @author ParadiseWY
 * @date 2020-11-05 09:40:59
 * @git {@link https://github.com/mygodness100}
 */
public class VideoMapper extends Mapper<Object, Text, NullWritable, Text> {

	Text text = new Text();

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String etlString = VideoUtil.ori2ETL(value.toString());
		if (StringUtils.isBlank(etlString))
			return;
		text.set(etlString);
		context.write(NullWritable.get(), text);
	}
}