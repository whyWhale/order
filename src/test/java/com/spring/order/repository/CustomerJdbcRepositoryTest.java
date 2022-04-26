package com.spring.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.distribution.Version.v8_0_11;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;

import com.spring.order.domain.Customer;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerJdbcRepositoryTest {

	private final static boolean NOT_EMPTY = false;

	@Configuration
	@ComponentScan(basePackages = {"com.spring.order"})
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

	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CustomerRepository customerRepository;

	private Customer newCustomer;

	private EmbeddedMysql embeddedMysql;

	@DisplayName("최초 딱 한번 실행 메소드")
	@BeforeAll
	void init() {
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
	public void embededDataBaseStop() {
		embeddedMysql.stop();
	}

	@Order(1)
	@Test
	@DisplayName("application context loading test")
	public void ContextLoadingTest() {
		// given
		// when
		// then
		Assertions.assertNotNull(applicationContext);
	}

	@Order(2)
	@Test
	@DisplayName("hikari connect test")
	public void connectHikariTest() {
		//given
		//when
		//then
		MatcherAssert.assertThat(dataSource.getClass().getName(), Matchers.is("com.zaxxer.hikari.HikariDataSource"));

	}

	@Disabled
	@Order(3)
	@Test
	@DisplayName("all delete")
	public void DeleteAllTest() {
		//given
		//when
		customerRepository.deleteAll();
		List<Customer> customers = customerRepository.findAll();
		//then
		Assertions.assertEquals(customers.size(), 0);

	}

	@Order(4)
	@Test
	@DisplayName("insert test")
	public void insertTest() {
		try {
			// given
			// when
			customerRepository.insert(newCustomer);
		} catch (BadSqlGrammarException e) {
			e.getSQLException().getErrorCode();
		}

		// then
		Optional<Customer> customer = customerRepository.findById(newCustomer.getId());
		MatcherAssert.assertThat(customer.isEmpty(), Matchers.is(NOT_EMPTY));
		MatcherAssert.assertThat(customer.get().getId(), Matchers.is(newCustomer.getId()));
	}

	@Order(5)
	@Test
	@DisplayName("update test")
	void updateTest() {
		//given

		Customer previousCustomer = customerRepository.findById(newCustomer.getId())
				.orElseThrow(() -> new RuntimeException("No data ..."));

		String updatedName = "update-name";
		newCustomer.changeName(updatedName);

		//when
		Customer updatedCustomer = customerRepository.update(newCustomer);

		//then
		Assertions.assertNotNull(updatedCustomer);
		Assertions.assertNotEquals(updatedCustomer.getName(), previousCustomer.getName());
		Assertions.assertEquals(updatedCustomer.getName(), updatedName);
	}

	@Order(6)
	@Test
	@DisplayName("findAll test")
	public void findAllTest() {
		//given

		//when
		List<Customer> customers = customerRepository.findAll();

		//then
		Assertions.assertEquals(customers.size(), 1);
		// MatcherAssert.assertThat(customers.get(0), Matchers.samePropertyValuesAs(newCustomer));
		MatcherAssert.assertThat(customers.get(0).getId(), Matchers.is(newCustomer.getId()));
	}

	@Order(7)
	@Test
	@DisplayName("count query test")
	public void countQueryTest() {
		//given

		//when
		int count = customerRepository.count();

		//then
		Assertions.assertEquals(count, 1);
	}
}
