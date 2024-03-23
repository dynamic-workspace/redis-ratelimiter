package com.example.redisratelimiter.configuration

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import io.lettuce.core.AbstractRedisClient
import io.lettuce.core.cluster.RedisClusterClient
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class BucketConfiguration(
    private val lettuceConnectionFactory: LettuceConnectionFactory,
) {
    @Bean
    fun lettuceBasedProxyManager(): LettuceBasedProxyManager<ByteArray> {
        val client = lettuceConnectionFactory.nativeClient
            ?: throw IllegalStateException("Redis client is not initialized.")

        return initBuilder(client)
            .withExpirationStrategy(
                ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                    Duration.ofSeconds(10)
                )
            )
            .build()
    }

    private fun initBuilder(client: AbstractRedisClient): LettuceBasedProxyManager.LettuceBasedProxyManagerBuilder<ByteArray> =
        when (client) {
            is RedisClusterClient -> LettuceBasedProxyManager.builderFor(client)
            else -> throw IllegalStateException("지원하지 않는 Redis 클라이언트 타입입니다: ${client::class.java.name}")
        }

}