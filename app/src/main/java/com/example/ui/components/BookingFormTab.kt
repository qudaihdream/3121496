package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.data.model.TimeSlotConstants
import com.example.ui.theme.Emerald600
import com.example.ui.theme.HospitalAmber
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusBookedRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingFormTab(
    hallId: String,
    date: String,
    startTime: String,
    endTime: String,
    timeSlot: String,
    dept: String,
    title: String,
    lecturer: String,
    applicant: String,
    phone: String,
    notes: String,
    allBookings: List<Booking>,
    onHallChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onTimeSlotChange: (String) -> Unit,
    onDeptChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onLecturerChange: (String) -> Unit,
    onApplicantChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onOpenAiAdvisor: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isAdditionalInfoExpanded by remember { mutableStateOf(true) }

    val selectedHall = HallConstants.getHallById(hallId)

    // Calculate Arabic day and weekend status
    val isWeekend = remember(date) {
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
            val cal = Calendar.getInstance()
            if (parsed != null) cal.time = parsed
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            dow == Calendar.FRIDAY || dow == Calendar.SATURDAY
        } catch (_: Exception) {
            false
        }
    }

    val arabicDateText = remember(date) {
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
            if (parsed != null) {
                SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(parsed)
            } else date
        } catch (_: Exception) {
            date
        }
    }

    // Calculate duration in hours/minutes
    val durationText = remember(startTime, endTime) {
        try {
            val sParts = startTime.split(":")
            val eParts = endTime.split(":")
            val sMin = sParts[0].toInt() * 60 + sParts[1].toInt()
            val eMin = eParts[0].toInt() * 60 + eParts[1].toInt()
            val diff = eMin - sMin
            if (diff > 0) {
                val hours = diff / 60
                val mins = diff % 60
                when {
                    hours > 0 && mins > 0 -> "$hours ساعة و $mins دقيقة"
                    hours == 1 -> "ساعة واحدة"
                    hours == 2 -> "ساعتان"
                    hours in 3..10 -> "$hours ساعات"
                    hours > 10 -> "$hours ساعة"
                    else -> "$mins دقيقة"
                }
            } else {
                "وقت الانتهاء يجب أن يكون بعد وقت البدء"
            }
        } catch (_: Exception) {
            ""
        }
    }

    // Check conflict for current slot
    val currentSlotFormatted = "$startTime - $endTime"
    val hasConflict = remember(hallId, date, startTime, endTime, allBookings) {
        allBookings.any {
            it.date == date && it.hallId == hallId && !it.isRejected &&
                    (it.timeSlot == currentSlotFormatted || it.timeSlot == timeSlot)
        }
    }

    // DatePicker Launcher
    fun showDatePicker() {
        val cal = Calendar.getInstance()
        try {
            val parts = date.split("-")
            if (parts.size == 3) cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateChange(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Start Time Picker
    fun showStartTimePicker() {
        var hour = 9
        var minute = 0
        try {
            val parts = startTime.split(":")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, h, m ->
                val formatted = String.format("%02d:%02d", h, m)
                onStartTimeChange(formatted)
            },
            hour,
            minute,
            true
        ).show()
    }

    // End Time Picker
    fun showEndTimePicker() {
        var hour = 10
        var minute = 0
        try {
            val parts = endTime.split(":")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, h, m ->
                val formatted = String.format("%02d:%02d", h, m)
                onEndTimeChange(formatted)
            },
            hour,
            minute,
            true
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HospitalTealPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = HospitalTealPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = "جدولة حجز قاعة جديدة",
                        color = Slate900,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "أدخل اسم الفعالية، اختيار القاعة، التاريخ، ووقت البدء والانتهاء",
                        color = Slate500,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Gemini AI Advisor Prompt Card
        if (onOpenAiAdvisor != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiAdvisor() }
                    .testTag("form_open_ai_advisor_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFDCFCE7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "هل تحتاج مساعدة في اختيار القاعة أو التجهيزات؟",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "استشر مساعد Gemini الذكي لاقتراح القاعة الأنسب وتخطيط الفعالية ✨",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ================= SECTION 1: EVENT NAME (اسم الفعالية) =================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "اسم الفعالية / عنوان المحاضرة *",
                        color = Slate900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = { Text("مثال: ورشة إدارة أمن وسلامة المرضى السريرية", fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Slate100.copy(alpha = 0.3f),
                        unfocusedContainerColor = Slate100.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_title_input")
                )
            }
        }

        // ================= SECTION 2: HALL SELECTION (اختيار القاعة) =================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "اختيار القاعة *",
                        color = Slate900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HallConstants.HALLS.forEach { hall ->
                        val isSelected = hall.id == hallId
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) hall.color.copy(alpha = 0.08f) else Slate100
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) hall.color else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onHallChange(hall.id) }
                                .testTag("form_hall_option_${hall.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(hall.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MeetingRoom,
                                            contentDescription = null,
                                            tint = hall.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = hall.name,
                                            color = Slate900,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = hall.description,
                                            color = Slate600,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(hall.color.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${hall.capacity} شخص",
                                            color = hall.color,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "تم الاختيار",
                                            tint = hall.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================= SECTION 3: DATE & TIME (التاريخ، وقت البدء، وقت الانتهاء) =================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "التاريخ وأوقات الفعالية *",
                        color = Slate900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3.1 Date Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "تاريخ الحجز (يوم العمل) *",
                        color = Slate700,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100)
                            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                            .clickable { showDatePicker() }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("form_date_picker_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = date,
                                color = HospitalTealDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = arabicDateText,
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "تغيير التاريخ",
                                color = HospitalTealPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = HospitalTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isWeekend) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF1F2))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = StatusBookedRed, modifier = Modifier.size(16.dp))
                            Text(
                                text = "تنبيه: هذا التاريخ يصادف عطلة نهاية أسبوع (الجمعة أو السبت).",
                                color = StatusBookedRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3.2 Start Time and End Time (وقت البدء ووقت الانتهاء)
                Text(
                    text = "وقت البدء ووقت الانتهاء *",
                    color = Slate700,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Time Box
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartTimePicker() }
                            .testTag("start_time_picker_btn")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "وقت البدء",
                                    color = Slate600,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = startTime,
                                color = HospitalTealDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اضغط لتعديل الوقت 🕒",
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // End Time Box
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndTimePicker() }
                            .testTag("end_time_picker_btn")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = HospitalAmber, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "وقت الانتهاء",
                                    color = Slate600,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = endTime,
                                color = HospitalTealDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اضغط لتعديل الوقت 🕒",
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Duration & Conflict Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (hasConflict) Color(0xFFFFF1F2) else Color(0xFFECFDF5))
                        .border(
                            1.dp,
                            if (hasConflict) Color(0xFFFECDD3) else Color(0xFFA7F3D0),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timelapse,
                            contentDescription = null,
                            tint = if (hasConflict) StatusBookedRed else StatusAvailableGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "مدة الفعالية: $durationText",
                            color = if (hasConflict) StatusBookedRed else Color(0xFF065F46),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (hasConflict) "❌ محجوز مسبقاً" else "🟢 متاح للحجز",
                        color = if (hasConflict) StatusBookedRed else StatusAvailableGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Quick Preset Slots
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "أو اختر من الفترات الزمنية المعتمدة مسبقاً:",
                        color = Slate600,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeSlotConstants.SLOTS.forEach { slot ->
                            val isBooked = allBookings.any {
                                it.date == date && it.hallId == hallId && it.timeSlot == slot && !it.isRejected
                            }
                            val isSelected = slot == currentSlotFormatted || slot == timeSlot

                            val (bg, border, textColor) = when {
                                isBooked -> Triple(Color(0xFFFFF1F2), Color(0xFFFECDD3), StatusBookedRed)
                                isSelected -> Triple(HospitalTealPrimary, HospitalTealPrimary, Color.White)
                                else -> Triple(Color(0xFFECFDF5), Color(0xFFA7F3D0), StatusAvailableGreen)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isBooked) {
                                        val parts = slot.split(" - ")
                                        if (parts.size == 2) {
                                            onStartTimeChange(parts[0].trim())
                                            onEndTimeChange(parts[1].trim())
                                        }
                                        onTimeSlotChange(slot)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("preset_slot_${slot}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBooked) "❌ $slot" else if (isSelected) "✓ $slot" else "🟢 $slot",
                                    color = textColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected || isBooked) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= SECTION 4: DEPARTMENT & APPLICANT INFO =================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAdditionalInfoExpanded = !isAdditionalInfoExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "بيانات القسم ومقدم الطلب",
                            color = Slate900,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = if (isAdditionalInfoExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Slate500
                    )
                }

                AnimatedVisibility(visible = isAdditionalInfoExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Department & Lecturer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "اسم القسم المستفيد", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = dept,
                                    onValueChange = onDeptChange,
                                    placeholder = { Text("مثال: قسم التمريض / التدريب", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HospitalTealPrimary,
                                        unfocusedBorderColor = Slate200
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("form_dept_input")
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "اسم المحاضر / المتحدث", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = lecturer,
                                    onValueChange = onLecturerChange,
                                    placeholder = { Text("د. أحمد المحمد", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HospitalTealPrimary,
                                        unfocusedBorderColor = Slate200
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("form_lecturer_input")
                                )
                            }
                        }

                        // Applicant & Phone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "اسم مقدم الطلب", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = applicant,
                                    onValueChange = onApplicantChange,
                                    placeholder = { Text("سارة خالد", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HospitalTealPrimary,
                                        unfocusedBorderColor = Slate200
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("form_applicant_input")
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "رقم التحويلة / الجوال", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = onPhoneChange,
                                    placeholder = { Text("تحويلة 4421 أو 050xxxxxxx", fontSize = 11.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HospitalTealPrimary,
                                        unfocusedBorderColor = Slate200
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("form_phone_input")
                                )
                            }
                        }

                        // Notes & Equipment
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "ملاحظات وتجهيزات مطلوبة (اختياري)", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = notes,
                                onValueChange = onNotesChange,
                                placeholder = { Text("تجهيز أجهزة العرض الصوتية والبروجكتر والميكروفونات...", fontSize = 11.sp) },
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HospitalTealPrimary,
                                    unfocusedBorderColor = Slate200
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("form_notes_input")
                            )
                        }
                    }
                }
            }
        }

        // ================= ACTION BUTTONS =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("إلغاء والعودة للجدول", color = Slate600, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HospitalTealPrimary),
                modifier = Modifier
                    .weight(1.4f)
                    .height(52.dp)
                    .testTag("submit_booking_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تأكيد وجدولة الحجز", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
