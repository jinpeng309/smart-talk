package com.capslock.im.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by capslock1874.
 */
@Configuration
public class CuratorConfig {
    @Bean
    public CuratorFramework getCuratorClient() {
        String zkConnString = "139.162.57.135:2181";

        final CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(zkConnString,
                new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        return curatorClient;
    }
}
