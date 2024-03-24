package com.example.redisratelimiter.service

import com.example.redisratelimiter.model.RateLimiterType
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import jakarta.annotation.PostConstruct
import java.util.function.Supplier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BucketService(
    private val lettuceBasedProxyManager: LettuceBasedProxyManager<ByteArray>,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val bucketMap = mutableMapOf<RateLimiterType, Bucket>()

    @PostConstruct
    fun init() {
        RateLimiterType.values().forEach {
            val key = "rate-limiter:bucket:${it.name}".toByteArray()

            val bandwidth = Bandwidth.builder()
                .capacity(it.limit)
                .refillGreedy(it.limit, it.duration)
                .build()

            val config = Supplier {
                BucketConfiguration.builder()
                    .addLimit(bandwidth)
                    .build()
            }

            val bucket = lettuceBasedProxyManager.builder()
                .build(key, config)

            bucketMap[it] = bucket
        }
    }

    fun <T> executeOnBackPressure(
        rateLimiterType: RateLimiterType,
        defaultResult: T,
        action: () -> T,
    ): T {
        try {
            val bucket = bucketMap[rateLimiterType] ?: throw IllegalArgumentException("Not found bucket: $rateLimiterType")
            println(bucket.availableTokens)

            return if (bucket.tryConsume(1)) {
                action()
            } else {
                logger.warn("[onBackPressureDrop] drop request, rateLimiter: $rateLimiterType")
                defaultResult
            }
        } catch (e: Exception) {
            logger.error("[onBackPressureDrop] $rateLimiterType", e)
            throw e
        }
    }
}
