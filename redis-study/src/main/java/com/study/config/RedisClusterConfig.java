package com.study.config;

import io.netty.util.internal.StringUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisClusterConfig {

    @Value("${spring.redis.timeout}")
    private Integer redisTimeout;
    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer poolMaxActive;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer poolMaxIdle;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer poolMinIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private Integer poolMaxWait;
    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;
    @Value("${spring.redis.cluster.max-redirects}")
    private Integer clusterMaxRedirects;
    @Value("${spring.redis.password}")
    private String password;



    @Bean("JedisCluster")
    public JedisCluster getJedisCluster() {
        Set<HostAndPort> nodes = new HashSet<>();
        for (String ipPort : clusterNodes) {
            String [] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        // 不需要密码和需要密码的创建方式
        if (StringUtils.isEmpty(password)) {
            return new JedisCluster(nodes, redisTimeout, 1000, 1, new GenericObjectPoolConfig());
        } else {
            return new JedisCluster(nodes, redisTimeout, 1000, 1, password, new GenericObjectPoolConfig());
        }
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(poolMaxActive);
        jedisPoolConfig.setMaxIdle(poolMaxIdle);
        jedisPoolConfig.setMinIdle(poolMinIdle);
        jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(jedisPoolConfig)
                .and()
                .readTimeout(Duration.ofMillis(redisTimeout))
                .build();
        // cluster模式
        RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
        redisConfig.setMaxRedirects(clusterMaxRedirects);
        for (String ipPort : clusterNodes) {
            String [] ipPortArr = ipPort.split(":");
            redisConfig.clusterNode(ipPortArr[0], Integer.parseInt(ipPortArr[1].trim()));
        }
        return new JedisConnectionFactory(redisConfig, clientConfiguration);
    }
}
