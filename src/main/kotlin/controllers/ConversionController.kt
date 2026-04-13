package com.example.services

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@Service
class ConversionService(
    private val coinGeckoClient: CoinGeckoClient
) {
    private val variables = ConcurrentHashMap<String, Double>()

    suspend fun getConversionRate(currency1: String, currency2Ticker: String): BigDecimal {
        val coinDetails = coinGeckoClient.fetchCoinDetails(currency1 = currency1)
        val valueNode = coinDetails.at("/market_data/current_price/${currency2Ticker.lowercase()}")
        if (valueNode.isMissingNode || valueNode.isNull) {
            throw IllegalArgumentException("Ticker '$currency2Ticker' not found in response")
        }
        return valueNode.decimalValue()
    }

    fun addVariable(name: String, value: Double) {
        variables[name] = value
    }

    fun getVariable(name: String): Double? = variables[name]
}
