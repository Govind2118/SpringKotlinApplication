package applicationtests

import com.example.resources.ConversionResource
import com.example.services.ConversionService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ApplicationTests {

    private val conversionService = mockk<ConversionService>()
    private val resource = ConversionResource(conversionService)

    @Test
    fun `fetch single quote delegates to service`() {
        val response = com.example.dto.ConversionQuoteResponse(
            coinId = "bitcoin",
            targetCurrency = "usd",
            rate = BigDecimal("68000.12"),
            cached = false,
            fetchedAt = Instant.parse("2026-01-01T00:00:00Z")
        )

        coEvery { conversionService.getConversionRate("bitcoin", "usd") } returns response

        val actual = kotlinx.coroutines.runBlocking {
            resource.fetchConversionRate("bitcoin", "usd")
        }

        assertEquals(response, actual)
    }

    @Test
    fun `get variable returns stored value`() {
        every { conversionService.getVariable("threshold") } returns 1.5

        val actual = resource.getVariable("threshold")

        assertEquals("threshold", actual.name)
        assertEquals(1.5, actual.value)
    }
}
