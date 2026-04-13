package com.example.cache

import com.example.config.AppProperties
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class CoinDetailsCache(
    private val appProperties: AppProperties
) {
    private val cache = ConcurrentHashMap<String, CacheEntry>()

    fun get(coinId: String): CacheHit? {
        val entry = cache[coinId] ?: return null
        if (Instant.now().isAfter(entry.expiresAt)) {
            cache.remove(coinId)
            return null
        }
        return CacheHit(entry.payload, true, entry.cachedAt)
    }

    fun put(coinId: String, payload: JsonNode): CacheHit {
        val now = Instant.now()
        cache[coinId] = CacheEntry(
            payload = payload,
            cachedAt = now,
            expiresAt = now.plus(appProperties.cacheTtl)
        )
        return CacheHit(payload, false, now)
    }

    data class CacheEntry(
        val payload: JsonNode,
        val cachedAt: Instant,
        val expiresAt: Instant
    )

    data class CacheHit(
        val payload: JsonNode,
        val cached: Boolean,
        val fetchedAt: Instant
    )
}
