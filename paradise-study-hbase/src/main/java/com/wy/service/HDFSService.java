package com.wy.service;

import java.io.InputStream;

/**
 * @apiNote HDFS业务接口
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:25:52
 */
public interface HDFSService {

	public void saveFile(String dir, String name, InputStream inputStream, long length, short replication)
			throws Exception;

	public void deleteFile(String dir, String name) throws Exception;

	public InputStream openFile(String dir, String name) throws Exception;

	public void makeDir(String dir) throws Exception;

	public void deleteDir(String dir) throws Exception;
}