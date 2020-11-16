package com.wy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * 使用原生jdbc的方式连接到hive数据仓库,数据仓库需要开启hiveserver2服务
	 */
	public static void getConnection() {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = DriverManager.getConnection("jdbc:hive2://192.168.1.150:10000/mydb2");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select id , name ,age from t");
			while (rs.next()) {
				System.out.println(rs.getInt(1) + "," + rs.getString(2));
			}
			rs.close();
			st.close();
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}