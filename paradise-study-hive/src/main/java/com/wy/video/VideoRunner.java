package com.wy.video;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * hadoop数据清洗,将程序打包后在yarn上运行,需要输入数据来源和数据存储地址
 * 
 * @author ParadiseWY
 * @date 2020-11-05 09:45:56
 * @git {@link https://github.com/mygodness100}
 */
public class VideoRunner implements Tool {

	private Configuration conf = null;

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public int run(String[] args) throws Exception {
		conf = this.getConf();
		conf.set("inpath", args[0]);
		conf.set("outpath", args[1]);
		Job job = Job.getInstance(conf);
		job.setJarByClass(VideoRunner.class);
		job.setMapperClass(VideoMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setNumReduceTasks(0);
		this.initJobInputPath(job);
		this.initJobOutputPath(job);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	private void initJobOutputPath(Job job) throws IOException {
		Configuration conf = job.getConfiguration();
		String outPathString = conf.get("outpath");
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(outPathString);
		if (fs.exists(outPath)) {
			fs.delete(outPath, true);
		}
		FileOutputFormat.setOutputPath(job, outPath);
	}

	private void initJobInputPath(Job job) throws IOException {
		Configuration conf = job.getConfiguration();
		String inPathString = conf.get("inpath");
		FileSystem fs = FileSystem.get(conf);
		Path inPath = new Path(inPathString);
		if (fs.exists(inPath)) {
			FileInputFormat.addInputPath(job, inPath);
		} else {
			throw new RuntimeException("HDFS中该文件目录不存在:" + inPathString);
		}
	}

	public static void main(String[] args) {
		try {
			int resultCode = ToolRunner.run(new VideoRunner(), args);
			if (resultCode == 0) {
				System.out.println("Success!");
			} else {
				System.out.println("Fail!");
			}
			System.exit(resultCode);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}