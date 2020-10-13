package com.wy.config;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @apiNote 配置文件加载类
 * @author ParadiseWY
 * @date 2020年2月9日 下午11:27:38
 */
public class HOSConfig {

	private static HOSConfig configuration;

	private static Properties properties;

	// 获取classpath下所有的配置
	static {
		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		configuration = new HOSConfig();
		try {
			HOSConfig.properties = new Properties();
			// 获取所有的properties文件
			Resource[] resources = resourcePatternResolver.getResources("classpath:*.properties");
			resourcePatternResolver.getResources("classpath:*.properties");
			// 遍历获取的properties文件
			for (Resource resource : resources) {
				Properties prop = new Properties();
				InputStream inputStream = resource.getInputStream();
				prop.load(inputStream);
				inputStream.close();
				// 将获得的配置添加到configuration的properties中
				HOSConfig.properties.putAll(prop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HOSConfig() {
	}

	public static HOSConfig getConfiguration() {
		return configuration;
	}

	public String getString(String key) {
		return properties.get(key).toString();
	}

	public int getInt(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
}