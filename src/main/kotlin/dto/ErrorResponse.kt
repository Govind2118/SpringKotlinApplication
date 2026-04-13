package com.example.dto

import java.time.Instant

data class ErrorResponse(
    val message: String,
    val timestamp: Instant = Instant.now()
)
