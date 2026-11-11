package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PreferencesManager
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.ui.theme.HallGlassColor
import com.example.ui.theme.HallInnerColor
import com.example.ui.theme.HallLargeColor
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenBg
import com.example.ui.theme.StatusBookedRed
import com.example.ui.theme.StatusBookedRedBg
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusPendingAmberBg
import com.example.ui.theme.StatusRejectedRose
import com.example.ui.theme.StatusRejectedRoseBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingLogTab(
    bookings: List<Booking>,
    searchQuery: String,
    selectedHallFilter: String?,
    selectedStatusFilter: String?,
    isAdmin: Boolean,
    onSearchChange: (String) -> Unit,
    onHallFilterChange: (String?) -> Unit,
    onStatusFilterChange: (String?) -> Unit,
    onEditClick: (Booking) -> Unit,
    onDeleteClick: (Booking) -> Unit,
    onReceiptClick: (Booking) -> Unit,
    onExportCsv: () -> Unit,
    onOpenSyncSettings: () -> Unit
) {
    val context = LocalContext.current
    val filterScrollState = rememberScrollState()

    val largeHallCount = bookings.count { it.hallId == "h1" && !it.isRejected }
    val glassHallCount = bookings.count { it.hallId == "h2" && !it.isRejected }
    val innerHallCount = bookings.count { it.hallId == "h3" && !it.isRejected }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary & Stats Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title and Quick Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "شاشة عرض وإدارة القاعات المحجوزة",
                            color = Slate900,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "استعراض كافة الحجوزات مع إمكانية تعديل أو حذف أي حجز موجود",
                            color = Slate500,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Google Sheets Link
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF047857))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PreferencesManager.GOOGLE_SHEET_URL))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Sheets", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Export CSV
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate800)
                                .clickable { onExportCsv() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("export_csv_btn"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Excel", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Sync Settings
                        IconButton(
                            onClick = onOpenSyncSettings,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate100)
                                .testTag("sync_settings_btn")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "إعداد المزامنة", tint = Slate700, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Stats Chips Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        title = "القاعة الكبيرة",
                        count = largeHallCount,
                        color = HallLargeColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        title = "القاعة الزجاجية",
                        count = glassHallCount,
                        color = HallGlassColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        title = "القاعة الداخلية",
                        count = innerHallCount,
                        color = HallInnerColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("بحث باسم الفعالية، القاعة، القسم، المحاضر أو الرمز...", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Slate500, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Slate500, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HospitalTealPrimary,
                        unfocusedBorderColor = Slate200
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_search_input")
                )

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        label = "جميع القاعات (${bookings.size})",
                        isSelected = selectedHallFilter == null,
                        onClick = { onHallFilterChange(null) }
                    )
                    HallConstants.HALLS.forEach { h ->
                        FilterChip(
                            label = h.name,
                            isSelected = selectedHallFilter == h.id,
                            onClick = { onHallFilterChange(h.id) }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    FilterChip(
                        label = "الكل",
                        isSelected = selectedStatusFilter == null,
                        onClick = { onStatusFilterChange(null) }
                    )
                    FilterChip(
                        label = "مؤكد 🔒",
                        isSelected = selectedStatusFilter == "approved",
                        onClick = { onStatusFilterChange("approved") }
                    )
                    FilterChip(
                        label = "في الانتظار ⏳",
                        isSelected = selectedStatusFilter == "pending",
                        onClick = { onStatusFilterChange("pending") }
                    )
                    FilterChip(
                        label = "مرفوض ❌",
                        isSelected = selectedStatusFilter == "rejected",
                        onClick = { onStatusFilterChange("rejected") }
                    )
                }
            }
        }

        // List of Bookings
        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لا توجد حجوزات مطابقة للبحث أو الفلتر المختار",
                        color = Slate600,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bookings, key = { it.id }) { booking ->
                    BookingCard(
                        booking = booking,
                        onEditClick = { onEditClick(booking) },
                        onDeleteClick = { onDeleteClick(booking) },
                        onReceiptClick = { onReceiptClick(booking) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReceiptClick: () -> Unit
) {
    val hall = HallConstants.getHallById(booking.hallId)
    val hallName = if (booking.hallName.isNotBlank()) booking.hallName else hall.name

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_item_${booking.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Code, Hall, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = booking.id,
                        color = Slate700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate100)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )

                    Text(
                        text = hallName,
                        color = hall.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(hall.color.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Status Badge
                val (statusBg, statusColor, statusLabel) = when {
                    booking.isApproved -> Triple(StatusAvailableGreenBg, StatusAvailableGreen, "مؤكد 🔒")
                    booking.isPending -> Triple(StatusPendingAmberBg, StatusPendingAmber, "في الانتظار ⏳")
                    else -> Triple(StatusRejectedRoseBg, StatusRejectedRose, "مرفوض ❌")
                }

                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Event Title
            Text(
                text = booking.title,
                color = Slate900,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Date, Time, Department
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                Text(text = "${booking.date} (${booking.timeSlot})", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Business, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                Text(text = booking.dept, color = Slate600, fontSize = 12.sp)
            }

            // Lecturer and Applicant
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (booking.lecturer.isNotBlank()) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                    Text(text = booking.lecturer, color = Slate700, fontSize = 11.sp)
                }
                if (booking.applicant.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Person, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                    Text(text = "${booking.applicant} (${booking.phone})", color = Slate600, fontSize = 11.sp)
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Slate200)
            )

            // Action Buttons Row: Edit (تعديل), Delete (حذف), Receipt (إشعار)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Receipt Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .clickable { onReceiptClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("receipt_booking_${booking.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Slate700, modifier = Modifier.size(14.dp))
                    Text(text = "الإشعار", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Edit Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2FE))
                        .clickable { onEditClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("edit_booking_${booking.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                    Text(text = "تعديل", color = Color(0xFF0369A1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF1F2))
                        .clickable { onDeleteClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("delete_booking_${booking.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusBookedRed, modifier = Modifier.size(14.dp))
                    Text(text = "حذف", color = StatusBookedRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "$count حجز", color = Slate900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) HospitalTealPrimary else Slate100)
            .border(1.dp, if (isSelected) HospitalTealPrimary else Slate200, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Slate700,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
