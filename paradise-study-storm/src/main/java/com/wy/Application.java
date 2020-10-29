package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Storm运行模式:<br>
 * 本地模式(Local Mode):即Topology(相当于一个任务)运行在本地机器的单一JVM上,这个模式主要用来开发,调试<br>
 * 远程模式(Remote Mode):把Topology提交到集群,该模式中,Storm的所有组件都是线程安全的
 * 
 * Storm整个其他框架官方文档:http://storm.apache.org/releases/1.2.3/index.html
 * 
 * @author ParadiseWY
 * @date 2020-10-28 10:45:54
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
