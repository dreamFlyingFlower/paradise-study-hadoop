package com.wy.hdfs;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.DeflateCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.Lz4Codec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * hadoop有多种压缩方式,Snappy需要安装额外的包才能使用
 * 
 * @author ParadiseWY
 * @date 2020-11-16 15:48:34
 * @git {@link https://github.com/mygodness100}
 */
public class Compress {

	public static void main(String[] args) throws Exception {
		Class<?>[] zipClasses = { DeflateCodec.class, GzipCodec.class, BZip2Codec.class, Lz4Codec.class,
				SnappyCodec.class };

		for (Class<?> c : zipClasses) {
			zip(c);
		}
		System.out.println("=================");
		for (Class<?> c : zipClasses) {
			unzip(c);
		}
	}

	/**
	 * 压缩
	 * 
	 * @param codecClass 压缩方式
	 * @throws Exception
	 */
	public static void zip(Class<?> codecClass) throws Exception {
		long start = System.currentTimeMillis();
		// 实例化对象
		CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, new Configuration());
		// 创建文件输出流,得到默认扩展名
		FileOutputStream fos = new FileOutputStream("/home/centos/zip/b" + codec.getDefaultExtension());
		// 得到压缩流
		CompressionOutputStream zipOut = codec.createOutputStream(fos);
		IOUtils.copyBytes(new FileInputStream("/home/centos/zip/a.txt"), zipOut, 1024);
		zipOut.close();
		System.out.println(codecClass.getSimpleName() + " : " + (System.currentTimeMillis() - start));
	}

	/**
	 * 解压缩
	 * 
	 * @param codecClass 解压缩方式
	 * @throws Exception
	 */
	public static void unzip(Class<?> codecClass) throws Exception {
		long start = System.currentTimeMillis();
		// 实例化对象
		CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, new Configuration());
		// 创建文件输出流,得到默认扩展名
		FileInputStream fis = new FileInputStream("/home/centos/zip/b" + codec.getDefaultExtension());
		// 得到压缩流
		CompressionInputStream zipIn = codec.createInputStream(fis);
		IOUtils.copyBytes(zipIn, new FileOutputStream("/home/centos/zip/b" + codec.getDefaultExtension() + ".txt"),
				1024);
		zipIn.close();
		System.out.println(codecClass.getSimpleName() + " : " + (System.currentTimeMillis() - start));
	}
}