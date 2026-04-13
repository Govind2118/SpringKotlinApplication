package com.example.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class CoinGeckoClient(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {
    suspend fun fetchCoinDetails(currency1: String): JsonNode {
        val path =
            "/api/v3/coins/${currency1}?localization=false&tickers=true&market_data=true&community_data=false&developer_data=false&sparkline=false"

        val responseBody: String = webClient
            .get()
            .uri(path)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()

        return objectMapper.readTree(responseBody)
    }
}
