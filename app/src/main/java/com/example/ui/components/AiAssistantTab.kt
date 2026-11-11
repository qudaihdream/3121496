package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.HallRecommendation
import com.example.data.model.HallConstants
import com.example.ui.ChatMessage
import com.example.ui.theme.Emerald600
import com.example.ui.theme.HospitalAmber
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAssistantTab(
    aiRecommendation: HallRecommendation?,
    isAiLoading: Boolean,
    aiGeneratedAgenda: String?,
    chatMessages: List<ChatMessage>,
    currentStreamingAiText: String,
    onAskRecommendation: (title: String, attendees: Int, dept: String, desc: String) -> Unit,
    onApplyRecommendation: (HallRecommendation) -> Unit,
    onSendMessage: (String) -> Unit,
    onGenerateAgenda: (title: String, hallName: String, duration: String) -> Unit,
    onClearAgenda: () -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sectionTitles = listOf("مستشار القاعات الذكي ✨", "مساعد الفعاليات والجدول 📋", "محادثة Gemini المباشرة 💬")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_assistant_tab")
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HospitalTealDark,
                            Color(0xFF0F3E3D)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF67E8F9), Color(0xFFFDE047))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "مستشار Gemini الذكي",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Gemini 3.8 Flash",
                                    color = Color(0xFF67E8F9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "نظام الذكاء الاصطناعي لاختيار القاعة الأنسب وتخطيط الفعاليات الطبية والإدارية",
                            color = Color(0xFFCCFBF1),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Section Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color.White,
            contentColor = HospitalTealPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = HospitalTealPrimary,
                    height = 3.dp
                )
            }
        ) {
            sectionTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    modifier = Modifier.testTag("ai_subtab_$index"),
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedSection == index) HospitalTealPrimary else Slate600
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = Slate200)

        // Section Body
        when (selectedSection) {
            0 -> SmartHallAdvisorSection(
                aiRecommendation = aiRecommendation,
                isAiLoading = isAiLoading,
                onAskRecommendation = onAskRecommendation,
                onApplyRecommendation = onApplyRecommendation,
                onGenerateAgenda = { title, hallName, dur ->
                    selectedSection = 1
                    onGenerateAgenda(title, hallName, dur)
                }
            )
            1 -> EventPlannerSection(
                aiGeneratedAgenda = aiGeneratedAgenda,
                isAiLoading = isAiLoading,
                onGenerateAgenda = onGenerateAgenda,
                onClearAgenda = onClearAgenda
            )
            2 -> InteractiveChatSection(
                chatMessages = chatMessages,
                currentStreamingAiText = currentStreamingAiText,
                isAiLoading = isAiLoading,
                onSendMessage = onSendMessage
            )
        }
    }
}

// ==========================================
// Section 1: Smart Hall Advisor
// ==========================================
@Composable
private fun SmartHallAdvisorSection(
    aiRecommendation: HallRecommendation?,
    isAiLoading: Boolean,
    onAskRecommendation: (title: String, attendees: Int, dept: String, desc: String) -> Unit,
    onApplyRecommendation: (HallRecommendation) -> Unit,
    onGenerateAgenda: (title: String, hallName: String, duration: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var attendeesText by remember { mutableStateOf("30") }
    var department by remember { mutableStateOf("التعليم الطبي المستمر") }
    var description by remember { mutableStateOf("") }

    val departmentsList = listOf(
        "التعليم الطبي المستمر",
        "التمريض",
        "إدارة الجودة ومكافحة العدوى",
        "الشؤون الصحية والأطباء",
        "الموارد البشرية والتدريب",
        "إدارة المستشفى والمجلس التنفيذي"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = HospitalAmber
                    )
                    Text(
                        text = "بيانات الفعالية لتحليل Gemini",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان أو نوع الفعالية") },
                    placeholder = { Text("مثال: ورشة تدريبية في إنعاش القلب الرئوي BLS") },
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = Slate500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_advisor_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        cursorColor = HospitalTealPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = attendeesText,
                        onValueChange = { if (it.all { char -> char.isDigit() }) attendeesText = it },
                        label = { Text("عدد الحضور المتوقع") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = Slate500) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_advisor_attendees_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HospitalTealPrimary,
                            cursorColor = HospitalTealPrimary
                        )
                    )

                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("القسم المستفيد") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Slate500) },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("ai_advisor_dept_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HospitalTealPrimary,
                            cursorColor = HospitalTealPrimary
                        )
                    )
                }

                // Quick Dept Chips
                Text(
                    text = "اختيار سريع للقسم:",
                    fontSize = 12.sp,
                    color = Slate600
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    departmentsList.take(3).forEach { dep ->
                        Box(
                            modifier = Modifier
                                .background(
                                    if (department == dep) Color(0xFFCCFBF1) else Color(0xFFF1F5F9),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { department = dep }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = dep,
                                fontSize = 11.sp,
                                color = if (department == dep) HospitalTealDark else Slate700,
                                fontWeight = if (department == dep) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("تفاصيل إضافية أو متطلبات خاصة (اختياري)") },
                    placeholder = { Text("مثال: نحتاج شاشات عرض، اتصال مرئي لنقل مباشر، طاولة مستديرة...") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = Slate500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("ai_advisor_desc_input"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        cursorColor = HospitalTealPrimary
                    )
                )

                Button(
                    onClick = {
                        val count = attendeesText.toIntOrNull() ?: 25
                        val finalTitle = title.ifBlank { "فعالية تدريبية" }
                        onAskRecommendation(finalTitle, count, department, description)
                    },
                    enabled = !isAiLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ai_advisor_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HospitalTealPrimary)
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("جاري استشارة Gemini الذكي...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اقتراح القاعة الأنسب بـ Gemini ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Recommendation Result Card
        aiRecommendation?.let { rec ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC)),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.testTag("ai_recommendation_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "توصية Gemini المعتمدة",
                                fontWeight = FontWeight.Bold,
                                color = Emerald600,
                                fontSize = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = rec.hallName,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534),
                                fontSize = 13.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFBBF7D0))

                    // Rationale
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "لماذا تم اختيار هذه القاعة؟",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate800
                        )
                        Text(
                            text = rec.reason,
                            fontSize = 13.sp,
                            color = Slate700,
                            lineHeight = 20.sp
                        )
                    }

                    // Duration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = HospitalTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "المدة الزمنية المقترحة: ${rec.suggestedDuration}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = Slate800
                        )
                    }

                    // Suggested Equipment
                    if (rec.suggestedEquipment.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "التجهيزات الصوتية والمرئية المقترحة:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate800
                            )
                            rec.suggestedEquipment.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = null,
                                        tint = Emerald600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onApplyRecommendation(rec) },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("apply_recommendation_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = HospitalTealDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تطبيق في نموذج الحجز 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val evTitle = title.ifBlank { "فعالية ${rec.hallName}" }
                                onGenerateAgenda(evTitle, rec.hallName, rec.suggestedDuration)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("generate_agenda_from_rec_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إنشاء جدول الفعالية 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Section 2: Event Planner & Agenda Generator
// ==========================================
@Composable
private fun EventPlannerSection(
    aiGeneratedAgenda: String?,
    isAiLoading: Boolean,
    onGenerateAgenda: (title: String, hallName: String, duration: String) -> Unit,
    onClearAgenda: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedHall by remember { mutableStateOf(HallConstants.HALLS.first().name) }
    var duration by remember { mutableStateOf("ساعتان (من 09:00 إلى 11:00)") }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = HospitalTealPrimary
                    )
                    Text(
                        text = "مولد جدول أعمال الفعالية وقائمة المراجعة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("موضوع أو عنوان الفعالية") },
                    placeholder = { Text("مثال: المؤتمر السنوي للجودة وسلامة المرضى") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("agenda_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        cursorColor = HospitalTealPrimary
                    )
                )

                // Select Hall
                Text(
                    text = "القاعة المستهدفة:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate700
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HallConstants.HALLS.forEach { hall ->
                        val isSelected = selectedHall == hall.name
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFFCCFBF1) else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) HospitalTealPrimary else Slate300,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedHall = hall.name }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hall.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) HospitalTealDark else Slate700
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("مدة الفعالية المقترحة") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Slate500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("agenda_duration_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        cursorColor = HospitalTealPrimary
                    )
                )

                Button(
                    onClick = {
                        val finalTitle = title.ifBlank { "الفعالية التوعوية والتدريبية" }
                        onGenerateAgenda(finalTitle, selectedHall, duration)
                    },
                    enabled = !isAiLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("agenda_generate_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HospitalTealPrimary)
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("جاري صياغة جدول الأعمال بـ Gemini...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توليد جدول الأعمال وقائمة المراجعة 📋",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Generated Output Card
        aiGeneratedAgenda?.let { agenda ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF99F6E4)),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.testTag("generated_agenda_result_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = HospitalTealPrimary
                            )
                            Text(
                                text = "جدول الأعمال والتجهيزات المقترحة",
                                fontWeight = FontWeight.Bold,
                                color = HospitalTealDark,
                                fontSize = 15.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(agenda)) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ",
                                    tint = Slate600
                                )
                            }
                            IconButton(
                                onClick = onClearAgenda,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق",
                                    tint = Slate500
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Slate200)

                    Text(
                        text = agenda,
                        fontSize = 13.sp,
                        color = Slate800,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// Section 3: Interactive Gemini Chat
// ==========================================
@Composable
private fun InteractiveChatSection(
    chatMessages: List<ChatMessage>,
    currentStreamingAiText: String,
    isAiLoading: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive or stream updates
    LaunchedEffect(chatMessages.size, currentStreamingAiText.length) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    val quickQuestions = listOf(
        "ما هي سعة وتجهيزات كل قاعة؟",
        "اقترح قاعة مناسبة لـ 50 شخص",
        "هل يوجد نظام اتصال مرئي Zoom بالقاعة الداخلية؟",
        "ما هي مواعيد العمل الرسمية للحجز؟"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_interactive_chat_section")
    ) {
        // Quick questions bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScrollRow(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { question ->
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(question) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = question,
                        fontSize = 11.sp,
                        color = HospitalTealDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        HorizontalDivider(color = Slate200)

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                ChatBubble(msg = msg)
            }

            // Live streaming bubble
            if (currentStreamingAiText.isNotBlank()) {
                item {
                    ChatBubble(
                        msg = ChatMessage(
                            sender = "gemini",
                            text = currentStreamingAiText
                        ),
                        isStreaming = true
                    )
                }
            } else if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = HospitalTealPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Gemini يفكر في الإجابة...",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        HorizontalDivider(color = Slate200)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("اكتب استفسارك لـ Gemini حول القاعات...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank() && !isAiLoading) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HospitalTealPrimary,
                    cursorColor = HospitalTealPrimary,
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedContainerColor = Color.White
                )
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isAiLoading) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isAiLoading,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (inputText.isNotBlank() && !isAiLoading) HospitalTealPrimary else Slate300,
                        CircleShape
                    )
                    .testTag("ai_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage, isStreaming: Boolean = false) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFE0F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Gemini",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) HospitalTealDark
                    else Color(0xFFFFFFFF)
                )
                .then(
                    if (!isUser) Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Gemini",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                        if (isStreaming) {
                            Text(
                                text = "• يكتب الآن...",
                                fontSize = 10.sp,
                                color = HospitalTealPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else Slate800,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// Helper Modifier for horizontal scrolling row
@Composable
private fun Modifier.horizontalScrollRow(): Modifier {
    return this.horizontalScroll(androidx.compose.foundation.rememberScrollState())
}
