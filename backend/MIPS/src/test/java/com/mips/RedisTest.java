package com.mips;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("local")
class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void redisSetAndGet() {
        String key = "test:redis:connection";
        String value = "PONG";

        try {
            redisTemplate.opsForValue()
                    .set(key, value, Duration.ofMinutes(1));

            String result =
                    redisTemplate.opsForValue().get(key);

            assertEquals(value, result);
        } finally {
            redisTemplate.delete(key);
        }
    }
}
