package com.wy.config;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wy.service.HOSStoreService;
import com.wy.service.impl.HDFSServiceImpl;
import com.wy.service.impl.HOSStoreServiceImpl;

/**
 * @apiNote
 * @author ParadiseWY
 * @date 2020年2月10日 上午11:57:23
 */
@Configuration
public class HBaseConfig {

	@Autowired
	private SecurityInterceptor securityInterceptor;

	/**
	 * 获取hbase connection注入到bean中
	 * @return
	 * @throws IOException
	 */
	@Bean
	public Connection getConnection() throws IOException {
		org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
		HOSConfig hosConfig = HOSConfig.getConfiguration();
		configuration.set("hbase.zookeeper.quorum", hosConfig.getString("hbase.zookeeper.quorum"));
		configuration.set("hbase.zookeeper.property.clientPort", hosConfig.getString("hbase.zookeeper.property.clientPort"));
		return ConnectionFactory.createConnection(configuration);
	}

	/**
	 * 实例化一个hosstore
	 * @param connection 连接
	 * @return
	 * @throws Exception
	 */
	@Bean
	public HOSStoreService getHosStore(@Autowired Connection connection) throws Exception {
		HOSConfig hosConfig = HOSConfig.getConfiguration();
		String zkhosts = hosConfig.getString("hbase.zookeeper.quorum");
		HOSStoreServiceImpl hosStoreServiceImpl = new HOSStoreServiceImpl(connection, new HDFSServiceImpl(), zkhosts);
		return hosStoreServiceImpl;
	}
	
	@Bean
	public WebMvcConfigurer configurer(InterceptorRegistry registry) {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(securityInterceptor);
			};
		};
	}
}