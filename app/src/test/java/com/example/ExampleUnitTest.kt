package com.example

import com.example.domain.LocalFraudHeuristics
import com.example.model.KavachStaticData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun fakeUpiCollectScam_isDetectedAsHighRisk() {
        val sample = KavachStaticData.SAMPLE_SCENARIOS.first { it.key == "upi_collect" }
        val result = LocalFraudHeuristics.evaluate(sample.text, sample.sender)
        assertTrue(result.riskScore >= 70)
        assertEquals("High Risk / Scam", result.threatLevel)
        assertTrue(result.redFlags.any { it.contains("UPI Collect", ignoreCase = true) })
    }

    @Test
    fun honeytrapSmsScam_isDetectedAsHighRisk() {
        val sample = KavachStaticData.SAMPLE_SCENARIOS.first { it.key == "honeytrap_sms" }
        val result = LocalFraudHeuristics.evaluate(sample.text, sample.sender)
        assertTrue(result.riskScore >= 70)
        assertEquals("High Risk / Scam", result.threatLevel)
        assertTrue(result.redFlags.any { it.contains("Honeytrap", ignoreCase = true) })
    }

    @Test
    fun safeBankDebitAlert_isDetectedAsSafeAndVerified() {
        val sample = KavachStaticData.SAMPLE_SCENARIOS.first { it.key == "safe_bank" }
        val result = LocalFraudHeuristics.evaluate(sample.text, sample.sender)
        assertEquals(5, result.riskScore)
        assertEquals("Safe", result.threatLevel)
        assertTrue(result.redFlags.isEmpty())
        assertTrue(LocalFraudHeuristics.isVerifiedSafeBankAlert(sample.text, sample.sender))
    }
}
