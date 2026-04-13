package com.example.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Instant

@Repository
class QuoteAuditRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun save(coinId: String, targetCurrency: String, rate: BigDecimal, cached: Boolean, fetchedAt: Instant) {
        jdbcTemplate.update(
            """
            INSERT INTO quote_audit (coin_id, target_currency, rate, cached, fetched_at)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent(),
            coinId,
            targetCurrency,
            rate,
            cached,
            Timestamp.from(fetchedAt)
        )
    }

    fun findLatest(coinId: String, limit: Int): List<QuoteAuditEntry> = jdbcTemplate.query(
        """
        SELECT coin_id, target_currency, rate, cached, fetched_at
        FROM quote_audit
        WHERE coin_id = ?
        ORDER BY fetched_at DESC
        LIMIT ?
        """.trimIndent(),
        arrayOf(coinId, limit)
    ) { rs, _ ->
        QuoteAuditEntry(
            coinId = rs.getString("coin_id"),
            targetCurrency = rs.getString("target_currency"),
            rate = rs.getBigDecimal("rate"),
            cached = rs.getBoolean("cached"),
            fetchedAt = rs.getTimestamp("fetched_at").toInstant()
        )
    }

    data class QuoteAuditEntry(
        val coinId: String,
        val targetCurrency: String,
        val rate: BigDecimal,
        val cached: Boolean,
        val fetchedAt: Instant
    )
}
