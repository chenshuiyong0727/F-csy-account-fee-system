package com.yj.accountfee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.yj.accountfee")
@SpringBootApplication
public class AccountFeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountFeeApplication.class, args);
    }
}
