package com.example.redisratelimiter.model

import java.time.Duration

enum class RateLimiterType(
    val limit: Long,
    val duration: Duration,
) {
    TEST1(
        limit = 5,
        duration = Duration.ofMinutes(10),
    ),

    MZS(
        limit = 80,
        duration = Duration.ofSeconds(1),
    ),
    ;
}
