package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.HallGlassColor
import com.example.ui.theme.HallInnerColor
import com.example.ui.theme.HallLargeColor

data class Hall(
    val id: String,
    val name: String,
    val capacity: Int,
    val description: String,
    val tag: String,
    val color: Color
)

object HallConstants {
    val HALLS = listOf(
        Hall(
            id = "h1",
            name = "القاعة الكبيرة",
            capacity = 100,
            description = "سعة 100 شخص (مجهزة بالكامل بشاشات وأنظمة صوتية متطورة)",
            tag = "مؤتمرات وفعاليات كبرى",
            color = HallLargeColor
        ),
        Hall(
            id = "h2",
            name = "القاعة الزجاجية",
            capacity = 40,
            description = "سعة 40 شخص (إطلالة زجاجية واجتماعات تدريبية)",
            tag = "ورش عمل وتدريب",
            color = HallGlassColor
        ),
        Hall(
            id = "h3",
            name = "القاعة الداخلية",
            capacity = 25,
            description = "سعة 25 شخص (اجتماعات رؤساء الأقسام ولجان المستشفى)",
            tag = "اجتماعات ولجان",
            color = HallInnerColor
        )
    )

    fun getHallById(id: String): Hall {
        return HALLS.firstOrNull { it.id == id } ?: HALLS[0]
    }

    fun getHallIdByName(name: String): String {
        return when {
            name.contains("الكبيرة") || name == "h1" -> "h1"
            name.contains("الزجاجية") || name == "h2" -> "h2"
            name.contains("الداخلية") || name == "h3" -> "h3"
            else -> "h1"
        }
    }
}
