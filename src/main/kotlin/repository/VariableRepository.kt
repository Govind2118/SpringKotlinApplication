package com.example.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

@Repository
class VariableRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun upsert(name: String, value: Double) {
        jdbcTemplate.update(
            """
            MERGE INTO variables (name, variable_value, updated_at)
            KEY(name)
            VALUES (?, ?, ?)
            """.trimIndent(),
            name,
            value,
            Timestamp.from(Instant.now())
        )
    }

    fun findByName(name: String): StoredVariable? = jdbcTemplate.query(
        "SELECT name, variable_value, updated_at FROM variables WHERE name = ?",
        name
    ) { rs, _ ->
        StoredVariable(
            name = rs.getString("name"),
            value = rs.getDouble("variable_value"),
            updatedAt = rs.getTimestamp("updated_at").toInstant()
        )
    }.firstOrNull()

    data class StoredVariable(
        val name: String,
        val value: Double,
        val updatedAt: Instant
    )
}