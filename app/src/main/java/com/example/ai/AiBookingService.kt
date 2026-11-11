package com.example.ai

import android.util.Log
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiAiService"

data class HallRecommendation(
    val recommendedHallId: String,
    val hallName: String,
    val reason: String,
    val suggestedDuration: String,
    val suggestedEquipment: List<String>
)

class AiBookingService {
    // Lazy model initialization as mandated by Firebase AI Logic
    private val generalModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    أنت المستشار الذكي والمساعد الإداري لنظام حجز القاعات التعليمية وقاعات الاجتماعات في مستشفى الرعاية المديدة بالظهران.
                    القاعات المتوفرة في المستشفى هي:
                    1. القاعة الكبيرة: سعة 100 شخص، مجهزة بشاشات عرض عملاقة ونظام صوتي متكامل ومنصة وميكروفونات لاسلكية، مخصصة للمؤتمرات الكبرى والندوات واللقاءات العامة.
                    2. القاعة الزجاجية: سعة 40 شخص، ذات إطلالة زجاجية هادئة ومجهزة ببروجكتر وشاشة ذكية وطاولات تفاعلية، مخصصة لورش العمل والبرامج التدريبية للأقسام.
                    3. القاعة الداخلية: سعة 25 شخص، مجهزة بشاشة ذكية وطاولة اجتماعات دائرية ونظام اتصال مرئي، مخصصة لاجتماعات رؤساء الأقسام واللجان الطبية والإدارية.
                    
                    أوقات العمل المتاحة للحجز: من الأحد إلى الخميس، من 08:00 صباحاً وحتى 16:00 عصراً (عطلة نهاية الأسبوع الجمعة والسبت مغلقة).
                    قدم إجابات احترافية، دقيقة، منظمة ومفيدة جداً باللغة العربية الطبية والإدارية الراقية.
                    """.trimIndent()
                )
            }
        )
    }

    // Recommendation structured schema
    private val recommendationSchema = Schema.obj(
        properties = mapOf(
            "recommendedHallId" to Schema.string(description = "h1 for Large Hall, h2 for Glass Hall, h3 for Inner Hall"),
            "hallName" to Schema.string(description = "Full Arabic name of the recommended hall"),
            "reason" to Schema.string(description = "Clear Arabic explanation of why this hall is the best fit"),
            "suggestedDuration" to Schema.string(description = "Recommended duration for this type of event in Arabic"),
            "suggestedEquipment" to Schema.array(
                items = Schema.string(),
                description = "List of required hospital audio-visual and medical conference equipment in Arabic"
            )
        )
    )

    private val structuredModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = recommendationSchema
            }
        )
    }

    /**
     * Ask Gemini to recommend the ideal hall and equipment based on event parameters
     */
    suspend fun recommendHall(
        eventTitle: String,
        attendeesCount: Int,
        department: String,
        description: String
    ): HallRecommendation = withContext(Dispatchers.IO) {
        val prompt = """
            بناءً على المعطيات التالية لفعالية بمستشفى الرعاية المديدة بالظهران:
            - عنوان الفعالية: $eventTitle
            - القسم المستفيد: $department
            - عدد الحضور المتوقع: $attendeesCount شخص
            - تفاصيل وملاحظات الفعالية: $description
            
            حدد القاعة الأنسب من بين (h1 القاعة الكبيرة، h2 القاعة الزجاجية، h3 القاعة الداخلية)، مع توضيح سبب الاختيار والتجهيزات الصوتية والمرئية المطلوبة بدقة.
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                structuredModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("لم يتم استلام رد من النموذج")
            val json = JSONObject(jsonText)

            val hallId = json.optString("recommendedHallId", "h1")
            val hallName = json.optString("hallName", HallConstants.getHallById(hallId).name)
            val reason = json.optString("reason", "القاعة تتناسب مع سعة الحضور ونوعية الفعالية الطبية.")
            val suggestedDuration = json.optString("suggestedDuration", "ساعتان")
            val equipmentArray = json.optJSONArray("suggestedEquipment")
            val equipmentList = mutableListOf<String>()
            if (equipmentArray != null) {
                for (i in 0 until equipmentArray.length()) {
                    equipmentList.add(equipmentArray.getString(i))
                }
            }

            HallRecommendation(
                recommendedHallId = hallId,
                hallName = hallName,
                reason = reason,
                suggestedDuration = suggestedDuration,
                suggestedEquipment = equipmentList.ifEmpty { listOf("شاشة عرض ذكية", "نظام صوتي وميكروفون", "منصة متحدث") }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini recommendation failed", e)
            throw e
        }
    }

    /**
     * Stream interactive AI consultant responses with Gemini
     */
    fun streamChatResponse(userQuery: String, currentBookingsSummary: String): Flow<String> = flow {
        val prompt = """
            ملخص الحجوزات الحالية للقاعات في المستشفى:
            $currentBookingsSummary
            
            سؤال الموظف / منسق القسم:
            $userQuery
            
            أجب باحترافية، وساعده في جدولة الحجز وتنسيق القاعات والإجابة على استفساراته.
        """.trimIndent()

        withTimeout(60_000L) {
            generalModel.generateContentStream(prompt).collect { chunk ->
                val chunkText = chunk.text
                if (!chunkText.isNullOrBlank()) {
                    emit(chunkText)
                }
            }
        }
    }

    /**
     * Generate comprehensive event agenda and checklist
     */
    suspend fun generateEventPlan(title: String, hallName: String, duration: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            اكتب جدول أعمال متكامل (Agenda) مع قائمة مراجعة التجهيزات (Checklist) لفعالية في مستشفى الرعاية المديدة بالظهران:
            - عنوان الفعالية: $title
            - القاعة المختارة: $hallName
            - المدة المقترحة: $duration
            
            قم بتضمين:
            1. التوقيت الزمني لفقرات الفعالية (استقبال وتسجيل، كلمة الافتتاح، العرض العلمي/الإداري، حلقة النقاش، التوصيات والختام).
            2. قائمة التجهيزات التقنية والضيافة والتنظيمية المطلوبة قبل بدء الفعالية.
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                generalModel.generateContent(prompt)
            }
            response.text ?: "تم تجهيز جدول الأعمال بنجاح."
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate event plan", e)
            throw e
        }
    }
}
