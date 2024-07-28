package com.baas.backend.service;

import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void set(String key, Object value, Duration duration) {
    redisTemplate.opsForValue().set(key, value, duration);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}
