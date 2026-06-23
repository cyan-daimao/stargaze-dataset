package com.cyan.stargaze.dataset;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 数据集平台启动类。
 * <p>
 * 承载数据源接入、数据集建模、字段定义、物化加速配置与元数据刷新;
 * 对 metric 提供 /rpc/v1/dataset 字段契约。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cyan")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cyan")
@EnableScheduling
@MapperScan("com.cyan.stargaze.dataset.infra.persistence.**.mappers")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
