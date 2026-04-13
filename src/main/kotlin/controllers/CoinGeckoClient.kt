package com.example.services

import com.example.exceptions.ExternalServiceException
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
    suspend fun fetchCoinDetails(coinId: String): JsonNode {
        val path =
            "/api/v3/coins/$coinId?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"

        val responseBody: String = webClient
            .get()
            .uri(path)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ status -> status.isError }) {
                throw ExternalServiceException(
                    "CoinGecko request failed for coin '$coinId'"
                )
            }
            .awaitBody()

        return try {
            objectMapper.readTree(responseBody)
        } catch (ex: Exception) {
            throw ExternalServiceException(
                "Failed to parse CoinGecko response for coin '$coinId'"
            )
        }
    }
}