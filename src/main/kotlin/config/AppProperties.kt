package com.example.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties {
    var cacheTtl: Duration = Duration.ofMinutes(2)
    var quoteHistoryLimit: Int = 20
}
