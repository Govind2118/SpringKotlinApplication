package com.example.dto

import java.time.Instant

data class MultiConversionResponse(
    val coinId: String,
    val quotes: List<ConversionQuoteResponse>,
    val fetchedAt: Instant
)
