package com.example.redisratelimiter.controller

import com.example.redisratelimiter.model.RateLimiterType
import com.example.redisratelimiter.service.BucketService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BucketTestController(
    private val bucketService: BucketService,
) {

    @GetMapping("/bucket-test")
    fun test(
        rateLimiterType: RateLimiterType,
    ): String {
        return bucketService.executeOnBackPressure(
            rateLimiterType = rateLimiterType,
            defaultResult = "Default"
        ) {
            "Success"
        }
    }
}