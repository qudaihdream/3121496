package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "bookings")
@JsonClass(generateAdapter = true)
data class Booking(
    @PrimaryKey
    @Json(name = "id")
    val id: String,

    @Json(name = "hallId")
    val hallId: String = "h1",

    @Json(name = "hallName")
    val hallName: String = "",

    @Json(name = "date")
    val date: String,

    @Json(name = "timeSlot")
    val timeSlot: String,

    @Json(name = "dept")
    val dept: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "lecturer")
    val lecturer: String = "",

    @Json(name = "applicant")
    val applicant: String = "",

    @Json(name = "phone")
    val phone: String = "",

    @Json(name = "notes")
    val notes: String = "",

    @Json(name = "status")
    val status: String = "pending", // "approved", "pending", "rejected"

    @Json(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
) {
    val isApproved: Boolean get() = status.equals("approved", ignoreCase = true) || status.contains("محجوز") || status.contains("مؤكد")
    val isPending: Boolean get() = status.equals("pending", ignoreCase = true) || status.contains("انتظار")
    val isRejected: Boolean get() = status.equals("rejected", ignoreCase = true) || status.contains("مرفوض")
}
