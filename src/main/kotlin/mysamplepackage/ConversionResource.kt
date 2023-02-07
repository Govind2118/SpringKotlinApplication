package mysamplepackage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import javax.ws.rs.NotFoundException
import javax.ws.rs.Produces

@RestController
class ConversionResource {

    @Bean
    fun getRestTemplate(): RestTemplate? {
        return RestTemplate()
    }

    @GetMapping("/fetch/coin-to-usd/{currency1}/{currency2}")
    @Produces("application/json")
    fun fetchConversionRate(
            @PathVariable("currency1") currency1: String,
            @PathVariable("currency2") currency2: String
    ): String {
        val coinGeckoDataUrl = "https://api.coingecko.com/api/v3/coins/${currency1}?localization=false&tickers=true&market_data=true&community_data=false&developer_data=false&sparkline=false"
        val restTemplate = getRestTemplate() ?: throw NotFoundException("Failed to initialize rest template.");
        val response: ResponseEntity<CoinDetails> = restTemplate.getForEntity(coinGeckoDataUrl, CoinDetails::class.java)
        val coinDetails = response.body;
        return coinDetails?.marketData?.currentPrice?.usdValue?.toString() ?: "Price not found!"
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinDetails(
        @JsonProperty("market_data")
        val marketData: MarketData
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MarketData(
        @JsonProperty("current_price")
        val currentPrice: CurrentPriceUSD
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentPriceUSD(
        @JsonProperty("usd")
        val usdValue: Double,
        @JsonProperty("btc")
        val btcValue: Double,
        @JsonProperty("eth")
        val ethValue: Double,
)
