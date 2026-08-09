package com.example.demo.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.example.demo.dto.FeedPostDTO;
import com.example.demo.dto.PostDetailDTO;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Deliberately NOT calling activateDefaultTyping anymore — see
        // the comment on getPostsByUser's cache config below for why.

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();

        // Each cache gets a serializer built for its EXACT type, instead
        // of one generic serializer that has to guess the type back from
        // embedded metadata on every read. This is what actually fixes
        // the recurring deserialization errors — not another typing
        // flag tweak.
        perCache.put("myPosts", typed(base, mapper,
                mapper.getTypeFactory().constructCollectionType(List.class, FeedPostDTO.class),
                Duration.ofMinutes(10)));

        perCache.put("postDetail", typed(base, mapper, PostDetailDTO.class, Duration.ofSeconds(30)));

        perCache.put("friendIds", typed(base, mapper,
                mapper.getTypeFactory().constructCollectionType(List.class, Long.class),
                Duration.ofMinutes(30)));

        // Long is a simple scalar — a plain string serializer avoids
        // even needing Jackson for this one.
        perCache.put("friendCount", base.entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericToStringSerializer<>(Long.class))));

        // feed stays unconfigured/unused — see prior discussion on why
        // getFeed isn't cached.

        return RedisCacheManager.builder(factory)
                .cacheDefaults(base.entryTtl(Duration.ofMinutes(5)))
                .withInitialCacheConfigurations(perCache)
                .build();
    }

    private RedisCacheConfiguration typed(RedisCacheConfiguration base, ObjectMapper mapper, JavaType type, Duration ttl) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(type);
        serializer.setObjectMapper(mapper);
        return base.entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private RedisCacheConfiguration typed(RedisCacheConfiguration base, ObjectMapper mapper, Class<?> type, Duration ttl) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(mapper.getTypeFactory().constructType(type));
        serializer.setObjectMapper(mapper);
        return base.entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    // Unchanged from before — still valuable regardless of the
    // serialization strategy above. Keeps a future cache problem from
    // ever reaching a live request as a 500.
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis GET failed for cache={} key={} — falling back to a cache miss", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis PUT failed for cache={} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis EVICT failed for cache={} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis CLEAR failed for cache={}", cache.getName(), exception);
            }
        };
    }
}
