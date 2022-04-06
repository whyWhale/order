package com.spring.order.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * what : voucher service, order service, repository 생성 책임을 가짐
 * 	각각 서비스, 저장소의 wiring(의존관계 담당) 을 담당.
 * 	:= 인스턴스 객체 생성의 제어권을 가지고 있는 클래스이다.(IoC container)
 * 	- 객체들의 생성과 파괴를
 */

@Configuration
@ComponentScan(basePackages = "com.spring.order")
public class ApplicationConfiguration {

}
