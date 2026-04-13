package com.example.resources

import com.example.services.ConversionService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class ConversionResource(
    private val conversionService: ConversionService
) {

    @GetMapping(
        value = ["/fetch/conversion/{currency1}/{currency2Ticker}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun fetchConversionRate(
        @PathVariable("currency1") currency1: String,
        @PathVariable("currency2Ticker") currency2Ticker: String
    ): BigDecimal = conversionService.getConversionRate(currency1 = currency1, currency2Ticker = currency2Ticker)

    @GetMapping(
        value = ["/add-variable/{name}/{value}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun addVariable(
        @PathVariable("name") name: String,
        @PathVariable("value") value: Double
    ): Map<String, Any> {
        conversionService.addVariable(name = name, value = value)
        return mapOf("name" to name, "value" to value)
    }
}
