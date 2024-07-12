package com.example.reservedassistance.service;

public interface RedisService {

    void writeToRedis(String key, String value);

    String readFromRedis(String key);

    void updateRedisValue(String key, String newValue);


    void storeVerificationCode(String email, String code);

    String getVerificationCode(String email);

    void removeVerificationCode(String email);
}
