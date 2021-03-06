package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
@EnableDiscoveryClient//注册到注册中心     开启服务注册和发现
public class ServiceProductApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceProductApplication.class,args);

    }
}
