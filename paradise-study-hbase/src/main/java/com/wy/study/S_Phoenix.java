package com.wy.study;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @apiNote Phoenix学习使用,传统的jdbc方式可以直接使用phoenix;若是使用mybatis,无法在xml文件中使用标签,
 *          但是可以在接口上使用注解来执行sql
 * @author ParadiseWY
 * @date 2020年2月11日 下午5:46:26
 */
public class S_Phoenix {

	public static void main(String[] args) {
		try {
			Class.forName("org.apache.phoenix.jbdc.PhoenixDriver");
			Connection connection = DriverManager.getConnection("jdbc:phoenix:localhost:2181");
			PreparedStatement ps = connection.prepareStatement("select * from tablename");
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				System.out.println(resultSet.getString("id"));
			}
			ps.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}