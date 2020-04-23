package com.study.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisCluster;

@RestController
@RequestMapping("/jesis")
public class JedisClusterTest {

    @Autowired
    private JedisCluster jedisCluster;

    @GetMapping("/test")
    public void test() {
        jedisCluster.set("a", "1");
    }
}
