package com.example.services

import com.example.cache.CoinDetailsCache
import com.example.config.AppProperties
import com.example.dto.ConversionQuoteResponse
import com.example.dto.MultiConversionResponse
import com.example.dto.QuoteHistoryResponse
import com.example.exceptions.ResourceNotFoundException
import com.example.repository.QuoteAuditRepository
import com.example.repository.VariableRepository
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ConversionService(
    private val coinGeckoClient: CoinGeckoClient,
    private val coinDetailsCache: CoinDetailsCache,
    private val variableRepository: VariableRepository,
    private val quoteAuditRepository: QuoteAuditRepository,
    private val appProperties: AppProperties
) {
    suspend fun getConversionRate(coinId: String, targetCurrency: String): ConversionQuoteResponse {
        val details = fetchCoinDetails(coinId)
        val rate = extractRate(details.payload, targetCurrency)

        quoteAuditRepository.save(
            coinId = coinId,
            targetCurrency = targetCurrency.lowercase(),
            rate = rate,
            cached = details.cached,
            fetchedAt = details.fetchedAt
        )

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

        val details = fetchCoinDetails(coinId)
        val quotes = targetCurrencies.distinct().map { targetCurrency ->
            val rate = extractRate(details.payload, targetCurrency)
            quoteAuditRepository.save(
                coinId = coinId,
                targetCurrency = targetCurrency.lowercase(),
                rate = rate,
                cached = details.cached,
                fetchedAt = details.fetchedAt
            )
            ConversionQuoteResponse(
                coinId = coinId,
                targetCurrency = targetCurrency.lowercase(),
                rate = rate,
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
        variableRepository.upsert(name = name, value = value)
    }

    fun getVariable(name: String) =
        variableRepository.findByName(name) ?: throw ResourceNotFoundException("Variable '$name' was not found")

    fun getQuoteHistory(coinId: String): List<QuoteHistoryResponse> =
        quoteAuditRepository.findLatest(coinId, appProperties.quoteHistoryLimit).map {
            QuoteHistoryResponse(
                coinId = it.coinId,
                targetCurrency = it.targetCurrency,
                rate = it.rate,
                cached = it.cached,
                fetchedAt = it.fetchedAt
            )
        }

    private suspend fun fetchCoinDetails(coinId: String): CoinDetailsCache.CacheHit {
        val cachedEntry = coinDetailsCache.get(coinId)
        return cachedEntry ?: coinDetailsCache.put(coinId, coinGeckoClient.fetchCoinDetails(coinId))
    }

    private fun extractRate(coinDetails: JsonNode, targetCurrency: String) =
        coinDetails.at("/market_data/current_price/${targetCurrency.lowercase()}").takeIf { !it.isMissingNode && !it.isNull }
            ?.decimalValue()
            ?: throw ResourceNotFoundException("Currency '${targetCurrency.lowercase()}' not found for requested coin")
}
