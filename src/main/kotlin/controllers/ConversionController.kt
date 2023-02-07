package controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException

class ConversionController {

    /* Fetches conversion rate using CoinGecko's free API for market data. */
    suspend fun getStringifiedConversionRate(
            currency1: String,
            currency2Ticker: String
    ): String {
        val coinGeckoDataUrl = "https://api.coingecko.com/api/v3/coins/${currency1}?localization=false&tickers=true&market_data=true&community_data=false&developer_data=false&sparkline=false"
        val restTemplate = getRestTemplate() ?: throw NotFoundException("Failed to initialize rest template.")
        val response: ResponseEntity<String> = restTemplate.getForEntity(coinGeckoDataUrl, String::class.java)
        val coinDetails = response.body ?: throw NotFoundException("Response body empty")
        return getCurrentPriceFromResponse(currency2Ticker, coinDetails)
    }

    private fun getCurrentPriceFromResponse(
            currency2Ticker: String,
            coinDetails: String
    ): String {
        val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val node: ObjectNode = mapper.readValue(coinDetails, ObjectNode::class.java)
        val value = try {
            node.get("market_data").get("current_price").get(currency2Ticker)
        } catch (e: Exception) {
            throw BadRequestException("Ticker $currency2Ticker not found, error: ${e.message}")
        }
        return value.toString()
    }

    @Bean
    private fun getRestTemplate(): RestTemplate? = RestTemplate()
}
