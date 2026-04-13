package com.example.resources

import com.example.dto.MultiConversionResponse
import com.example.dto.QuoteHistoryResponse
import com.example.dto.VariableRequest
import com.example.dto.VariableResponse
import com.example.services.ConversionService
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1")
@Validated
class ConversionResource(
    private val conversionService: ConversionService
) {

    @GetMapping(
        value = ["/quotes/{coinId}/{targetCurrency}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun fetchConversionRate(
        @PathVariable coinId: String,
        @PathVariable targetCurrency: String
    ) = conversionService.getConversionRate(coinId = coinId, targetCurrency = targetCurrency)

    @GetMapping(
        value = ["/quotes/{coinId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun fetchMultipleConversionRates(
        @PathVariable coinId: String,
        @RequestParam currencies: List<String>
    ): MultiConversionResponse = conversionService.getMultipleConversionRates(coinId = coinId, targetCurrencies = currencies)

    @PostMapping(
        value = ["/variables"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addVariable(@Valid @RequestBody request: VariableRequest): VariableResponse {
        conversionService.addVariable(name = request.name, value = request.value)
        val stored = conversionService.getVariable(request.name)
        return VariableResponse(name = stored.name, value = stored.value, updatedAt = stored.updatedAt)
    }

    @GetMapping(
        value = ["/variables/{name}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getVariable(@PathVariable name: String): VariableResponse {
        val stored = conversionService.getVariable(name)
        return VariableResponse(name = stored.name, value = stored.value, updatedAt = stored.updatedAt)
    }

    @GetMapping(
        value = ["/quotes/history/{coinId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getQuoteHistory(@PathVariable coinId: String): List<QuoteHistoryResponse> =
        conversionService.getQuoteHistory(coinId)

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
