package com.zm.project_template.common.constant;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Describle This Class Is
 * @Author ZengMin
 * @Date 2020/2/16 11:47
 */
@Configuration
public class CacheConfig {

    /**
     * GuavaCache
     *
     * @return
     */
    @Bean
    public Cache<String, Integer> cache() {
        return CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.SECONDS)         // 写入后30s删除
                .expireAfterAccess(10, TimeUnit.SECONDS)        // 10s不访问删除
                .initialCapacity(1)    // 设置初始容量为1
                .concurrencyLevel(10) // 设置并发级别为10
                .recordStats() // 开启缓存统计
                .build();
    }

}
