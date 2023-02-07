package mysamplepackage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Guice
import controllers.ConversionController
import controllers.DemoApplicationModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.ws.rs.Produces

@RestController
class ConversionResource {

    private val injector = Guice.createInjector(DemoApplicationModule())
    private val conversionController = injector.getInstance(ConversionController::class.java)

    @GetMapping("/fetch/coin-to-usd/{currency1}/{currency2}")
    @Produces("application/json")
    suspend fun fetchConversionRate(
            @PathVariable("currency1") currency1: String,
            @PathVariable("currency2") currency2: String
    ): String = withContext(Dispatchers.IO) {
        conversionController.getStringifiedConversionRate(currency1, currency2)
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
