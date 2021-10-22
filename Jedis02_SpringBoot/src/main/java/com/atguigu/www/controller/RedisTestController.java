package com.atguigu.www.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("get")
    public String testRedis(){
        redisTemplate.opsForValue().set("name","lucy");
        String value = (String)redisTemplate.opsForValue().get("name");
        return value;
    }
}
