package com.example.data.remote

import android.util.Log
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.StringReader
import java.util.concurrent.TimeUnit

class GoogleSheetSyncService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
) {
    private val tag = "GoogleSheetSync"

    /**
     * Send booking creation, status update, or deletion to the Apps Script Web App
     */
    suspend fun syncBookingToSheet(
        scriptUrl: String,
        booking: Booking,
        action: String // "create", "update_status", "delete"
    ): Boolean = withContext(Dispatchers.IO) {
        if (scriptUrl.isBlank()) return@withContext false
        try {
            val json = JSONObject().apply {
                put("id", booking.id)
                put("hallId", booking.hallId)
                put("hallName", if (booking.hallName.isNotBlank()) booking.hallName else HallConstants.getHallById(booking.hallId).name)
                put("date", booking.date)
                put("timeSlot", booking.timeSlot)
                put("dept", booking.dept)
                put("title", booking.title)
                put("lecturer", booking.lecturer)
                put("applicant", booking.applicant)
                put("phone", booking.phone)
                put("notes", booking.notes)
                put("status", booking.status)
                put("action", action)
            }

            val requestBody = json.toString().toRequestBody("text/plain; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(scriptUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            response.close()
            Log.d(tag, "Sync to Apps Script successful: $success")
            success
        } catch (e: Exception) {
            Log.w(tag, "Failed to sync to Apps Script: ${e.message}")
            false
        }
    }

    /**
     * Fetch live bookings from Apps Script doGet or fallback to published Google Sheet CSV
     */
    suspend fun fetchLiveBookings(scriptUrl: String, csvUrl: String): List<Booking> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Booking>()

        // 1. Attempt doGet on Apps Script Web App
        if (scriptUrl.isNotBlank()) {
            try {
                val request = Request.Builder().url(scriptUrl).get().build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank() && bodyString.trim().startsWith("[")) {
                        val jsonArray = JSONArray(bodyString)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.optJSONObject(i) ?: continue
                            val parsed = parseBookingJson(obj)
                            if (parsed != null) results.add(parsed)
                        }
                    }
                }
                response.close()
            } catch (e: Exception) {
                Log.d(tag, "Apps Script doGet failed, trying CSV: ${e.message}")
            }
        }

        // 2. If results still empty, fallback to Published CSV
        if (results.isEmpty() && csvUrl.isNotBlank()) {
            try {
                val request = Request.Builder().url(csvUrl).get().build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val csvText = response.body?.string()
                    if (!csvText.isNullOrBlank()) {
                        val csvBookings = parseCsv(csvText)
                        results.addAll(csvBookings)
                    }
                }
                response.close()
            } catch (e: Exception) {
                Log.w(tag, "CSV fetch failed: ${e.message}")
            }
        }

        results
    }

    private fun parseBookingJson(obj: JSONObject): Booking? {
        val date = obj.optString("date", "")
        val timeSlot = obj.optString("timeSlot", "")
        if (date.isBlank() || timeSlot.isBlank()) return null

        val hallName = obj.optString("hallName", "")
        val hallId = when {
            obj.has("hallId") && obj.getString("hallId").isNotBlank() -> obj.getString("hallId")
            else -> HallConstants.getHallIdByName(hallName)
        }

        val rawStatus = obj.optString("status", "pending").lowercase()
        val status = when {
            rawStatus.contains("approved") || rawStatus.contains("محجوز") || rawStatus.contains("مؤكد") -> "approved"
            rawStatus.contains("rejected") || rawStatus.contains("مرفوض") -> "rejected"
            else -> "pending"
        }

        return Booking(
            id = obj.optString("id", "BK-" + (1000..9999).random()),
            hallId = hallId,
            hallName = if (hallName.isNotBlank()) hallName else HallConstants.getHallById(hallId).name,
            date = date,
            timeSlot = timeSlot,
            dept = obj.optString("dept", "قسم مستفيد"),
            title = obj.optString("title", "فعالية"),
            lecturer = obj.optString("lecturer", ""),
            applicant = obj.optString("applicant", ""),
            phone = obj.optString("phone", ""),
            notes = obj.optString("notes", ""),
            status = status
        )
    }

    private fun parseCsv(csvText: String): List<Booking> {
        val list = mutableListOf<Booking>()
        val reader = BufferedReader(StringReader(csvText))
        var line = reader.readLine() // skip header line
        while (reader.readLine().also { line = it } != null) {
            val currentLine = line ?: continue
            if (currentLine.isBlank()) continue
            val tokens = parseCsvLine(currentLine)
            if (tokens.size >= 4) {
                val id = tokens.getOrNull(0)?.ifBlank { null } ?: ("BK-" + (1000..9999).random())
                val hallName = tokens.getOrNull(1) ?: ""
                val hallId = HallConstants.getHallIdByName(hallName)
                val date = tokens.getOrNull(2) ?: ""
                val timeSlot = tokens.getOrNull(3) ?: ""
                val dept = tokens.getOrNull(4) ?: "قسم مستفيد"
                val title = tokens.getOrNull(5) ?: "فعالية"
                val lecturer = tokens.getOrNull(6) ?: ""
                val applicant = tokens.getOrNull(7) ?: ""
                val phone = tokens.getOrNull(8) ?: ""
                val notes = tokens.getOrNull(9) ?: ""
                val rawStatus = tokens.getOrNull(10)?.lowercase() ?: "approved"

                val status = when {
                    rawStatus.contains("approved") || rawStatus.contains("محجوز") || rawStatus.contains("مؤكد") -> "approved"
                    rawStatus.contains("rejected") || rawStatus.contains("مرفوض") -> "rejected"
                    else -> "pending"
                }

                if (date.isNotBlank() && timeSlot.isNotBlank()) {
                    list.add(
                        Booking(
                            id = id,
                            hallId = hallId,
                            hallName = if (hallName.isNotBlank()) hallName else HallConstants.getHallById(hallId).name,
                            date = date,
                            timeSlot = timeSlot,
                            dept = dept,
                            title = title,
                            lecturer = lecturer,
                            applicant = applicant,
                            phone = phone,
                            notes = notes,
                            status = status
                        )
                    )
                }
            }
        }
        return list
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (char in line) {
            when (char) {
                '"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        sb.append(char)
                    } else {
                        result.add(sb.toString().trim())
                        sb.setLength(0)
                    }
                }
                else -> sb.append(char)
            }
        }
        result.add(sb.toString().trim())
        return result
    }
}
