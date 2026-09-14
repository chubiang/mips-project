package com.mips.domain.stock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.stock.dto.RealtimeQuoteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisQuoteStore {

    private static final String KEY_PREFIX = "quote:latest:";
    private static final Duration TTL = Duration.ofDays(1);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /* 실시간 종목 저장 */
    public void save(RealtimeQuoteCache quote) {
        try {
            String key = KEY_PREFIX + quote.ticker();
            String value = objectMapper.writeValueAsString(quote);

            redisTemplate.opsForValue()
                    .set(key, value, TTL);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("실시간 시세 직렬화 실패", e);
        }
    }

    /* 모든 종목 KEY 조회 */
    public List<RealtimeQuoteCache> findAllLatest() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        List<RealtimeQuoteCache> result = new ArrayList<>();
        try {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value == null)
                {
                    throw new NullPointerException(key+" 저장된 시세가 없습니다.");
                }
                result.add(
                        objectMapper.readValue(
                                value,
                                RealtimeQuoteCache.class
                        )
                );
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Redis 실시간 시세 역직렬화 실패",
                    e
            );
        }
        return result;
    }
}