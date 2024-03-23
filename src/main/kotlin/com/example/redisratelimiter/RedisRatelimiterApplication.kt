package com.example.redisratelimiter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisRatelimiterApplication

fun main(args: Array<String>) {
    runApplication<RedisRatelimiterApplication>(*args)
}
