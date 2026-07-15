package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设hiredis的 Key 使用字符串序列化器
        redisTemplate.setKeySerializer(
                new StringRedisSerializer());
        // 普通 Value
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash 的字段名
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Hash 的字段值
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;

    }
}
