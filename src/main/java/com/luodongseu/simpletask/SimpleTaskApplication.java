package com.luodongseu.simpletask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 简单的任务发布/认领/执行系统
 *
 * @author luodongseu
 */
@EnableScheduling
@SpringBootApplication
public class SimpleTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleTaskApplication.class, args);
    }
}
