package com.pikel.balancetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BalanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BalanceTrackerApplication.class, args);
    }

}
