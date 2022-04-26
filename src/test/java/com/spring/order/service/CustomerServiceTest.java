package com.spring.order.service;

import static com.wix.mysql.EmbeddedMysql.*;
import static com.wix.mysql.ScriptResolver.*;
import static com.wix.mysql.config.MysqldConfig.*;
import static com.wix.mysql.distribution.Version.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.spring.order.domain.Customer;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {

	private static final Logger log = LoggerFactory.getLogger(CustomerServiceTest.class);

	@Configuration
	// @EnableTransactionManagement
	@ComponentScan(basePackages = "com.spring.order")
	static class Config {
		@Bean
		public DataSource dataSource() {
			HikariDataSource dataSource = DataSourceBuilder
					.create()
					.url("jdbc:mysql://localhost:2215/kdt_order")
					.username("test")
					.password("test")
					.type(HikariDataSource.class)
					.build();

			dataSource.setMaximumPoolSize(1000);
			dataSource.setMinimumIdle(100);

			return dataSource;
		}

		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}

		@Bean
		public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
			return new NamedParameterJdbcTemplate(jdbcTemplate);
		}
	}

	private static EmbeddedMysql embeddedMysql;

	private static Customer newCustomer;

	@Autowired
	CustomerService customerService;

	// @Autowired
	// CustomerRepository customerRepository;

	@BeforeAll
	static void init() {
		newCustomer = new Customer(UUID.randomUUID()
				, "test-user"
				, "test@programmers.co.kr"
				, LocalDateTime.now());

		MysqldConfig mysqldConfig = aMysqldConfig(v8_0_11)
				.withCharset(Charset.UTF8)
				.withPort(2215)
				.withUser("test", "test")
				.withTimeZone("Asia/Seoul")
				.build();

		embeddedMysql = anEmbeddedMysql(mysqldConfig)
				.addSchema("kdt_order", classPathScript("schema.sql"))
				.start();
	}

	@AfterAll
	static void destory() {
		embeddedMysql.stop();
	}

	@AfterEach
	void cleanUpEach(){
		customerService.deleteAll();
	}

	@Test
	@DisplayName("multi-insert ")
	void testMultiInsert() {
		//given
		List<Customer> customers = getUniqueCustomers();
		//when
		List<Customer> retCustomers = customerService.createCustomers(customers);
		//then
		MatcherAssert.assertThat(retCustomers.size(), Matchers.is(customers.size()));
		AtomicInteger index = new AtomicInteger();
		retCustomers.forEach(customer -> {
			log.info("customer : {}", customer.toString());
			MatcherAssert.assertThat(customer, Matchers.samePropertyValuesAs(customers.get(index.getAndIncrement())));
		});
	}

	@Test
	@DisplayName("multi-insert 실패시 전체 롤백!")
	void testRollBack() {
		//given
		String email = "test1@programmers.co.kr";
		List<Customer> customers = getNotUniqueCustomers(email);
		//when
		try {
			customerService.createCustomers(customers);
		} catch (DataAccessException e) {
			log.error("=========== roll back ===========");
		}

		List<Customer> everyCustomer = customerService.findAll();
		//then
		MatcherAssert.assertThat(everyCustomer.size(), Matchers.is(0));
	}

	private List<Customer> getUniqueCustomers() {
		return List.of(
				new Customer(UUID.randomUUID()
						, "test-user1"
						, "test1@programmers.co.kr"
						, LocalDateTime.now()),
				new Customer(UUID.randomUUID()
						, "test-user2"
						, "test2@programmers.co.kr"
						, LocalDateTime.now())
		);
	}

	private List<Customer> getNotUniqueCustomers(String email) {
		return List.of(
				new Customer(UUID.randomUUID()
						, "test-user1"
						, email
						, LocalDateTime.now()),
				new Customer(UUID.randomUUID()
						, "test-user2"
						, email
						, LocalDateTime.now())
		);
	}
}