package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.ui.theme.HospitalAmber
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenBg
import com.example.ui.theme.StatusBookedRed
import com.example.ui.theme.StatusBookedRedBg
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusPendingAmberBg
import java.util.Calendar

@Composable
fun EditBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var editTitle by remember { mutableStateOf(booking.title) }
    var editHallId by remember { mutableStateOf(booking.hallId) }
    var editDate by remember { mutableStateOf(booking.date) }
    var editDept by remember { mutableStateOf(booking.dept) }
    var editLecturer by remember { mutableStateOf(booking.lecturer) }
    var editApplicant by remember { mutableStateOf(booking.applicant) }
    var editPhone by remember { mutableStateOf(booking.phone) }
    var editNotes by remember { mutableStateOf(booking.notes) }
    var editStatus by remember { mutableStateOf(booking.status) }

    // Parse start and end time from existing slot
    var editStartTime by remember {
        val parts = booking.timeSlot.split(" - ")
        mutableStateOf(if (parts.isNotEmpty() && parts[0].contains(":")) parts[0].trim() else "09:00")
    }
    var editEndTime by remember {
        val parts = booking.timeSlot.split(" - ")
        mutableStateOf(if (parts.size >= 2 && parts[1].contains(":")) parts[1].trim() else "10:00")
    }

    fun showDatePicker() {
        val cal = Calendar.getInstance()
        try {
            val parts = editDate.split("-")
            if (parts.size == 3) cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                editDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showStartTimePicker() {
        var hour = 9
        var minute = 0
        try {
            val parts = editStartTime.split(":")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, h, m -> editStartTime = String.format("%02d:%02d", h, m) },
            hour,
            minute,
            true
        ).show()
    }

    fun showEndTimePicker() {
        var hour = 10
        var minute = 0
        try {
            val parts = editEndTime.split(":")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, h, m -> editEndTime = String.format("%02d:%02d", h, m) },
            hour,
            minute,
            true
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(HospitalTealPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "تعديل بيانات الحجز",
                                color = Slate900,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "رمز الحجز: ${booking.id}",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Slate500)
                    }
                }

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "اسم الفعالية / عنوان المحاضرة *", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HospitalTealPrimary,
                            unfocusedBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
                    )
                }

                // Hall Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "اختيار القاعة *", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HallConstants.HALLS.forEach { h ->
                            val isSelected = h.id == editHallId
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) h.color.copy(alpha = 0.15f) else Slate100)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) h.color else Slate200,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { editHallId = h.id }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = h.name,
                                        color = if (isSelected) h.color else Slate700,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${h.capacity} شخص",
                                        color = Slate500,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Date Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "تاريخ الحجز *", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                            .clickable { showDatePicker() }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = editDate, color = HospitalTealDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                // Start and End Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start Time
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                            .clickable { showStartTimePicker() }
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = HospitalTealPrimary, modifier = Modifier.size(13.dp))
                            Text(text = "وقت البدء", color = Slate600, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = editStartTime, color = HospitalTealDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // End Time
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                            .clickable { showEndTimePicker() }
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = HospitalAmber, modifier = Modifier.size(13.dp))
                            Text(text = "وقت الانتهاء", color = Slate600, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = editEndTime, color = HospitalTealDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Status Selector (مؤكد / في الانتظار / مرفوض)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "حالة الحجز", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("approved", "مؤكد 🔒", StatusAvailableGreen),
                            Triple("pending", "في الانتظار ⏳", StatusPendingAmber),
                            Triple("rejected", "مرفوض ❌", StatusBookedRed)
                        ).forEach { (statusKey, label, color) ->
                            val isSelected = editStatus.equals(statusKey, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) color.copy(alpha = 0.15f) else Slate100)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) color else Slate200,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editStatus = statusKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) color else Slate700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Department & Lecturer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "القسم المستفيد", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = editDept,
                            onValueChange = { editDept = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "المحاضر", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = editLecturer,
                            onValueChange = { editLecturer = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Applicant & Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "مقدم الطلب", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = editApplicant,
                            onValueChange = { editApplicant = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "التحويلة / الجوال", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Notes
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "ملاحظات وتجهيزات", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = Slate600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val updatedBooking = booking.copy(
                                title = editTitle.trim(),
                                hallId = editHallId,
                                hallName = HallConstants.getHallById(editHallId).name,
                                date = editDate.trim(),
                                timeSlot = "$editStartTime - $editEndTime",
                                dept = editDept.trim(),
                                lecturer = editLecturer.trim(),
                                applicant = editApplicant.trim(),
                                phone = editPhone.trim(),
                                notes = editNotes.trim(),
                                status = editStatus
                            )
                            onSave(updatedBooking)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HospitalTealPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_edit_booking_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ التعديلات", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
