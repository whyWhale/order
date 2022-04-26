package com.spring.order.test.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

public class JdbcConfig {
	private static Logger log = LoggerFactory.getLogger(JdbcConfig.class);

	public static Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost/kdt_order", "root", "");
		} catch (SQLException exception) {
			exception.printStackTrace();
			log.error("db connection error !");
		}
		return getConnection();
	}
}
