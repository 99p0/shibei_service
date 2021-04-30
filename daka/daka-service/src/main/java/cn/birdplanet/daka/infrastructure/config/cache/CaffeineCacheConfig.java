//package com.birdplanet.config.cache;
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.caffeine.CaffeineCache;
//import org.springframework.cache.support.SimpleCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: CaffeineConfig
// * @date 2019-07-19 02:04
// */
//@Slf4j
//@Configuration
//public class CaffeineCacheConfig extends CachingConfigurerSupport {
//
//    private static final int DEFAULT_MAXSIZE = 50000;
//    private static final int DEFAULT_TTL = 10;
//
//    /**
//     * 定義cache名稱、超時時長（秒）、最大容量
//     * 每个cache缺省：10秒超时、最多缓存50000条数据，需要修改可以在                构造方法的参数中指定。
//     */
//    public enum Caches {
//        getPersonById(5), // 有效期5秒
//        getSomething, // 缺省10秒
//        getOtherthing(300, 1000), // 5分钟，最大容量1000
//        ;
//
//        Caches() {
//        }
//
//        Caches(int ttl) {
//            this.ttl = ttl;
//        }
//
//        Caches(int ttl, int maxSize) {
//            this.ttl = ttl;
//            this.maxSize = maxSize;
//        }
//
//        // 最大數量
//        private int maxSize = DEFAULT_MAXSIZE;
//        // 过期时间（秒）
//        private int ttl = DEFAULT_TTL;
//
//        public int getMaxSize() {
//            return maxSize;
//        }
//
//        public int getTtl() {
//            return ttl;
//        }
//    }
//
//    @Bean
//    @Override
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        ArrayList<CaffeineCache> caches = new ArrayList<>();
//        for (Caches c : Caches.values()) {
//            caches.add(new CaffeineCache(c.name(),
//                    Caffeine.newBuilder().recordStats()
//                            .expireAfterWrite(c.getTtl(), TimeUnit.SECONDS)
//                            .maximumSize(c.getMaxSize())
//                            .build())
//            );
//        }
//        cacheManager.setCaches(caches);
//        return cacheManager;
//    }
//}
