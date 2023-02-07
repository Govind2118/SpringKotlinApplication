package resources

import com.google.inject.Guice
import controllers.ConversionController
import controllers.TestApplicationModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.ws.rs.Produces

@RestController
class CoinConversionResource {

    private val injector = Guice.createInjector(TestApplicationModule())
    private val conversionController = injector.getInstance(ConversionController::class.java)

    @GetMapping("/fetch/coin-to-usd/{currency1}/{currency2Ticker}")
    @Produces("application/json")
    suspend fun fetchConversionRate(
            @PathVariable("currency1") currency1: String,
            @PathVariable("currency2Ticker") currency2Ticker: String
    ): String = withContext(Dispatchers.IO) {
        conversionController.getStringifiedConversionRate(currency1, currency2Ticker)
    }

}
