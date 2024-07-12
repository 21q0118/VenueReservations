package com.example.reservedassistance.service.impl;

import com.example.reservedassistance.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final long EXPIRATION_TIME = 5; // 5 minutes
    @Override
    public void writeToRedis(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String readFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void updateRedisValue(String key, String newValue) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, newValue);
        } else {
            // Handle the case where the key doesn't exist, or take appropriate action
            throw new RuntimeException("Key not found in Redis: " + key);
        }
    }


    public void storeVerificationCode(String email, String code) {

        redisTemplate.opsForValue().set(email, code, EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {

        return redisTemplate.opsForValue().get(email);
    }

    public void removeVerificationCode(String email) {
        redisTemplate.delete(email);
    }
}
