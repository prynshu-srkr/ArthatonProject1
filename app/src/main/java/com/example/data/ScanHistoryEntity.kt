package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.FraudAssessmentResult
import org.json.JSONArray

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val sender: String,
    val channel: String,
    val timestamp: Long = System.currentTimeMillis(),
    val riskScore: Int,
    val threatLevel: String,
    val redFlagsJson: String,
    val explanation: String,
    val explanationHi: String
) {
    fun getRedFlagsList(): List<String> {
        return try {
            val array = JSONArray(redFlagsJson)
            (0 until array.length()).map { array.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun toAssessmentResult(): FraudAssessmentResult {
        return FraudAssessmentResult(
            riskScore = riskScore,
            threatLevel = threatLevel,
            redFlags = getRedFlagsList(),
            explanation = explanation,
            explanationHi = explanationHi
        )
    }

    companion object {
        fun encodeRedFlags(flags: List<String>): String {
            val jsonArray = JSONArray()
            flags.forEach { jsonArray.put(it) }
            return jsonArray.toString()
        }
    }
}
