package com.wy.service;

import java.util.Collection;

import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.hadoop.fs.FsShell;
import org.springframework.stereotype.Component;

/**
 *	springboot整合hadoop访问hdfs
 *	
 *	@author ParadiseWY
 *	@date 2020-10-27 09:18:30
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class HadoopService implements CommandLineRunner{
	
	@Autowired
	private FsShell fsShell;

	@Override
	public void run(String... args) throws Exception {
		Collection<FileStatus> lsr = fsShell.lsr("path");
		for (FileStatus fileStatus : lsr) {
			System.out.println(fileStatus.isDirectory());
		}
	}
}