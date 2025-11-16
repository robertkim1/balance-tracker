package com.pikel.balancetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BalanceTrackerApplication {

    public static void main(String[] args) {
//        System.out.println(System.getenv("CONNECTION_STRING"));
        SpringApplication.run(BalanceTrackerApplication.class, args);
    }
}
