package com.example.services

import com.example.cache.CoinDetailsCache
import com.example.dto.ConversionQuoteResponse
import com.example.dto.MultiConversionResponse
import com.example.exceptions.ResourceNotFoundException
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class ConversionService(
    private val coinGeckoClient: CoinGeckoClient,
    private val coinDetailsCache: CoinDetailsCache
) {
    private val variables = ConcurrentHashMap<String, Double>()

    suspend fun getConversionRate(coinId: String, targetCurrency: String): ConversionQuoteResponse {
        val cachedEntry = coinDetailsCache.get(coinId)
        val details = cachedEntry ?: coinDetailsCache.put(coinId, coinGeckoClient.fetchCoinDetails(coinId))
        val rate = extractRate(details.payload, targetCurrency)

        return ConversionQuoteResponse(
            coinId = coinId,
            targetCurrency = targetCurrency.lowercase(),
            rate = rate,
            cached = details.cached,
            fetchedAt = details.fetchedAt
        )
    }

    suspend fun getMultipleConversionRates(coinId: String, targetCurrencies: List<String>): MultiConversionResponse {
        if (targetCurrencies.isEmpty()) {
            throw IllegalArgumentException("At least one target currency must be provided")
        }

        val cachedEntry = coinDetailsCache.get(coinId)
        val details = cachedEntry ?: coinDetailsCache.put(coinId, coinGeckoClient.fetchCoinDetails(coinId))
        val quotes = targetCurrencies.distinct().map { targetCurrency ->
            ConversionQuoteResponse(
                coinId = coinId,
                targetCurrency = targetCurrency.lowercase(),
                rate = extractRate(details.payload, targetCurrency),
                cached = details.cached,
                fetchedAt = details.fetchedAt
            )
        }

        return MultiConversionResponse(
            coinId = coinId,
            quotes = quotes,
            fetchedAt = Instant.now()
        )
    }

    fun addVariable(name: String, value: Double) {
        variables[name] = value
    }

    fun getVariable(name: String): Double =
        variables[name] ?: throw ResourceNotFoundException("Variable '$name' was not found")

    private fun extractRate(coinDetails: JsonNode, targetCurrency: String) =
        coinDetails.at("/market_data/current_price/${targetCurrency.lowercase()}").takeIf { !it.isMissingNode && !it.isNull }
            ?.decimalValue()
            ?: throw ResourceNotFoundException("Currency '${targetCurrency.lowercase()}' not found for requested coin")
}
