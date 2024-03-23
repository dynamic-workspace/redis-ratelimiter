package com.example.redisratelimiter.model

import java.time.Duration

enum class RateLimiterType(
    val limit: Long,
    val duration: Duration,
) {
    KIS(
        limit = 50,
        duration = Duration.ofMinutes(10),
    ),

    MERITZ(
        limit = 80,
        duration = Duration.ofSeconds(1),
    ),
    ;
}