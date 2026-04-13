package com.example.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

data class VariableRequest(
    @field:NotBlank(message = "Variable name must not be blank")
    val name: String,

    @field:PositiveOrZero(message = "Variable value must be zero or positive")
    val value: Double
)
