package com.example.dto

import java.math.BigDecimal
import java.time.Instant

data class QuoteHistoryResponse(
    val coinId: String,
    val targetCurrency: String,
    val rate: BigDecimal,
    val cached: Boolean,
    val fetchedAt: Instant
)
