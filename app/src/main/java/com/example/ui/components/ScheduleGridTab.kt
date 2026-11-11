package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.data.model.TimeSlotConstants
import com.example.ui.theme.HallGlassColor
import com.example.ui.theme.HallInnerColor
import com.example.ui.theme.HallLargeColor
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenBg
import com.example.ui.theme.StatusBookedRed
import com.example.ui.theme.StatusPendingAmber
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleGridTab(
    selectedDate: String,
    dateDisplayArabic: String,
    isWeekend: Boolean,
    bookings: List<Booking>,
    onDateChange: (String) -> Unit,
    onDateShift: (Int) -> Unit,
    onTodayClick: () -> Unit,
    onQuickBook: (hallId: String, date: String, slot: String) -> Unit,
    onBookingClick: (Booking) -> Unit
) {
    val context = LocalContext.current
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    fun showSystemDatePicker() {
        val cal = Calendar.getInstance()
        try {
            val parts = selectedDate.split("-")
            if (parts.size == 3) {
                cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Date Controls Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Previous day
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .clickable { onDateShift(-1) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("prev_day_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "اليوم السابق",
                            tint = Slate700,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "اليوم السابق",
                            color = Slate700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Date selector button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE6FFFA))
                            .border(1.5.dp, HospitalTealPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { showSystemDatePicker() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("date_picker_dialog_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "اختيار التاريخ",
                            tint = HospitalTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = selectedDate,
                            color = HospitalTealPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Next day
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .clickable { onDateShift(1) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("next_day_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "اليوم التالي",
                            color = Slate700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "اليوم التالي",
                            tint = Slate700,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Today quick button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HospitalTealPrimary)
                            .clickable { onTodayClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("today_btn")
                    ) {
                        Text(
                            text = "اليوم",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Date Label in Arabic
                Text(
                    text = dateDisplayArabic,
                    color = Slate500,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Visual Color Legend
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendItem(color = StatusBookedRed, label = "محجوز 🔒 (غير متاح)")
                    Spacer(modifier = Modifier.width(10.dp))
                    LegendItem(color = StatusPendingAmber, label = "في الانتظار ⏳ (غير متاح)")
                    Spacer(modifier = Modifier.width(10.dp))
                    LegendItem(color = StatusAvailableGreen, label = "متاح للحجز 🟢")
                }
            }
        }

        // Weekend Warning Notice Banner
        if (isWeekend) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFECDD3)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = "إجازة أسبوعية",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        text = "عطلة نهاية الأسبوع (إجازة رسمية)",
                        color = Color(0xFF881337),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "القاعات التعليمية والاجتماعات غير متاحة للحجز يومي الجمعة والسبت. الرجاء اختيار تاريخ عمل من الأحد إلى الخميس.",
                        color = Color(0xFFBE123C),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Halls Live Grid Matrix
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .background(Slate800)
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الوقت / القاعة",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(110.dp)
                        )

                        HallHeaderColumn(
                            title = "القاعة الكبيرة",
                            capacity = "سعة 100 شخص (مجهزة)",
                            color = HallLargeColor,
                            width = 200.dp
                        )

                        HallHeaderColumn(
                            title = "القاعة الزجاجية",
                            capacity = "سعة 40 شخص (إطلالة)",
                            color = HallGlassColor,
                            width = 200.dp
                        )

                        HallHeaderColumn(
                            title = "القاعة الداخلية",
                            capacity = "سعة 25 شخص (اجتماعات)",
                            color = HallInnerColor,
                            width = 200.dp
                        )
                    }

                    // Table Body - Time Slots Rows
                    TimeSlotConstants.SLOTS.forEach { slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Slate200)
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Time Slot Cell
                            Row(
                                modifier = Modifier
                                    .width(110.dp)
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Slate500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = slot,
                                    color = Slate700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Hall Cells
                            HallConstants.HALLS.forEach { hall ->
                                val booking = bookings.firstOrNull {
                                    it.date == selectedDate && it.hallId == hall.id && it.timeSlot == slot && !it.isRejected
                                }

                                Box(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(4.dp)
                                ) {
                                    if (booking != null) {
                                        if (booking.isApproved) {
                                            // Red Booked Cell
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(StatusBookedRed)
                                                    .clickable { onBookingClick(booking) }
                                                    .padding(8.dp)
                                                    .testTag("booked_cell_${hall.id}_${slot}")
                                            ) {
                                                Text(
                                                    text = booking.title,
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Business,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFD4D4),
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = booking.dept,
                                                        color = Color(0xFFFFE4E4),
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0x33000000))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "محجوز 🔒",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            // Amber Pending Cell
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(StatusPendingAmber)
                                                    .clickable { onBookingClick(booking) }
                                                    .padding(8.dp)
                                                    .testTag("pending_cell_${hall.id}_${slot}")
                                            ) {
                                                Text(
                                                    text = booking.title,
                                                    color = Color(0xFF0F172A),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Business,
                                                        contentDescription = null,
                                                        tint = Color(0xFF334155),
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = booking.dept,
                                                        color = Color(0xFF1E293B),
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0x26000000))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "في الانتظار ⏳",
                                                        color = Color(0xFF0F172A),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = Color(0xFF0F172A),
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Green Available Button
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(StatusAvailableGreenBg)
                                                .border(1.dp, Color(0xFF6EE7B7), RoundedCornerShape(10.dp))
                                                .clickable { onQuickBook(hall.id, selectedDate, slot) }
                                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                                .testTag("available_slot_${hall.id}_${slot}"),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "حجز",
                                                tint = StatusAvailableGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "متاح للحجز",
                                                color = StatusAvailableGreen,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HallHeaderColumn(
    title: String,
    capacity: String,
    color: Color,
    width: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = capacity,
            color = Color(0xFFCBD5E1),
            fontSize = 10.sp
        )
    }
}
