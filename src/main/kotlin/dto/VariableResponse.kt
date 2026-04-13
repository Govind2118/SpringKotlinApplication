package com.example.dto

import java.time.Instant

data class VariableResponse(
    val name: String,
    val value: Double,
    val updatedAt: Instant? = null
)
