package com.example.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun coinGeckoWebClient(): WebClient =
        WebClient.builder()
            .baseUrl("https://api.coingecko.com")
            .build()
}
