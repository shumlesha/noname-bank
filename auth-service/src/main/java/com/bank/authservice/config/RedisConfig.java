package com.bank.authservice.config;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    private static final String REDIS_ADDRESS_TEMPLATE = "redis://%s:%s";

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();

        String address = REDIS_ADDRESS_TEMPLATE.formatted(redisProperties.getHost(), redisProperties.getPort());

        config.useSingleServer()
                .setAddress(address)
                .setUsername(redisProperties.getUsername())
                .setPassword(redisProperties.getPassword())
                .setConnectionPoolSize(16)
                .setConnectionMinimumIdleSize(8)
                .setSubscriptionConnectionPoolSize(10);

        return Redisson.create(config);
    }

    @Bean
    public RBloomFilter<String> revokedTokensBloomFilter(RedissonClient redisson) {
        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("revoked-tokens-bloom");

        bloomFilter.tryInit(1_000_000L, 0.03);
        return bloomFilter;
    }

    @Bean
    public RMapCache<String, String> refreshTokensMap(RedissonClient redisson) {
        return redisson.getMapCache("refresh-tokens");
    }

    @Bean
    public RSetCache<String> revokedTokensSet(RedissonClient redisson) {
        return redisson.getSetCache("revoked-tokens");
    }
}
