package com.wy.hdfs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * hdfs原生使用
 * 
 * @author ParadiseWY
 * @date 2020-10-19 14:26:36
 * @git {@link https://github.com/mygodness100}
 */
public class HdfsClient {

	public static void main(String[] args) {

	}

	/**
	 * 创建目录
	 */
	public static void mkdir() {
		Configuration configuration = new Configuration();
		// 设置NameNode节点地址
		configuration.set("dfs.defaultFS", "http://hadoop001:9000");
		// 直接获得客户端对象,最后一个参数是访问hadoop的用户
		// FileSystem.get(new URI("http://hadoop001:9000"), configuration, "hadoop");
		// 利用configuration获得hdfs客户端对象,该方式需要在启动的时候添加-DHADOOP_USER_NAME=root
		try (FileSystem fs = FileSystem.get(configuration);
				FSDataInputStream fis = fs.open(new Path("/test/index.html"));) {
			// 在hdfs上创建目录
			fs.mkdirs(new Path("/test/test001"));
			// 访问文件
			byte[] buf = new byte[1024];
			int len = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = fis.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			System.out.println(new String(baos.toByteArray()));
			// 将文件下载到本地
			IOUtils.copyBytes(fis, new FileOutputStream(new File("/app/test/inde.html")), configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件上传,还有其他根据api的操作,如增删查等
	 */
	public static void uploadFile() {
		// 代码中会自动寻找资源目录下的配置文件,若找到配置文件,将会有优先级的问题
		// 代码中的配置>项目资源目录下的配置>服务器上的配置
		Configuration configuration = new Configuration();
		configuration.set("dfs.replication", "2");
		try (FileSystem fs = FileSystem.get(new URI("http://hadoop001:9000"), configuration, "hadoop");) {
			fs.copyFromLocalFile(new Path("d:/test/test002.txt"), new Path("/test/test002.txt"));
			// 写入文件的同时定制副本数和blocksize
			fs.create(new Path("/test/test003.txt"), true, 1024, (short) 2, 1024);
			// 第二个参数:若删除的是一个目录,是否递归参数
			fs.delete(new Path("/test/test003.txt"), true);
		} catch (IOException | InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过io流上传或下载文件
	 * 
	 * @apiNote fs.create:上传文件时,创建一个hdfs的输出流;fs.open:下载文件时,创建一个hdfs的输出流
	 */
	public static void uploadFileByIO() {
		Configuration configuration = new Configuration();
		try (FileSystem fs = FileSystem.get(new URI("http://hadoop001:9000"), configuration, "hadoop");
				FileInputStream fileInputStream = new FileInputStream("d:/test/test002.txt");
				FSDataOutputStream fsdos = fs.create(new Path("/test/test002.txt"));) {
			IOUtils.copyBytes(fileInputStream, fsdos, configuration);
			// 立即刷新hdfs中的DN上的数据.对数据的一致性较高的条件时才用
			fsdos.hflush();
			fs.copyFromLocalFile(new Path("d:/test/test002.txt"), new Path("/test/test002.txt"));
		} catch (IOException | InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}