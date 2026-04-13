package com.example.resources

import com.example.dto.MultiConversionResponse
import com.example.dto.VariableRequest
import com.example.dto.VariableResponse
import com.example.services.ConversionService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
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
    fun addVariable(@RequestBody request: VariableRequest): VariableResponse {
        conversionService.addVariable(name = request.name, value = request.value)
        return VariableResponse(name = request.name, value = request.value)
    }

    @GetMapping(
        value = ["/variables/{name}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getVariable(@PathVariable name: String): VariableResponse {
        val value = conversionService.getVariable(name)
        return VariableResponse(name = name, value = value)
    }

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
