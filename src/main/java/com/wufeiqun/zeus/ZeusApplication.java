package com.wufeiqun.zeus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wufeiqun.zeus.mapper")
public class ZeusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeusApplication.class, args);
	}

}
