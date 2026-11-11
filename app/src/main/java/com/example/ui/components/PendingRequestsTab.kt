package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.ui.theme.HospitalAmber
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
import com.example.ui.theme.StatusPendingAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PendingRequestsTab(
    pendingBookings: List<Booking>,
    isApprovalRequired: Boolean,
    isAdmin: Boolean,
    onTogglePolicy: (Boolean) -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onUnlockAdmin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Policy & Admin Control Card
        Card(
            shape = RoundedCornerShape(16.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(HospitalAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = HospitalAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "لوحة معالجة طلبات مسؤول القاعات",
                                color = Slate900,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "مراجعة الطلبات المعلقة واعتمادها أو رفضها فوراً",
                                color = Slate500,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Count Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(HospitalAmber)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${pendingBookings.size}",
                            color = Slate900,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Booking Policy Switcher
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "سياسة الحجز المعتمدة بالمستشفى:",
                        color = Slate700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Option B: Approval required (Default)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isApprovalRequired) HospitalTealPrimary else Slate200)
                                .clickable { onTogglePolicy(true) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("policy_btn_approval"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ب. بموافقة المسؤول (معتمد)",
                                color = if (isApprovalRequired) Color.White else Slate700,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Option A: Direct booking
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isApprovalRequired) HospitalTealPrimary else Slate200)
                                .clickable { onTogglePolicy(false) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("policy_btn_direct"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "أ. حجز مباشر تلقائي",
                                color = if (!isApprovalRequired) Color.White else Slate700,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Admin Notice if not unlocked
                if (!isAdmin) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                            .clickable { onUnlockAdmin() }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = HospitalAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = "أنت في وضع المشاهدة. اضغط لتفعيل صلاحيات المسؤول",
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "فتح القفل 🔑",
                            color = HospitalAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // List of Pending Requests
        if (pendingBookings.isEmpty()) {
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusAvailableGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "لا توجد طلبات حجز بانتظار الموافقة حالياً",
                        color = Slate800,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "جميع الطلبات السابقة تمت معالجتها واعتمادها",
                        color = Slate500,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
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
                items(pendingBookings, key = { it.id }) { booking ->
                    val hall = HallConstants.getHallById(booking.hallId)
                    val hallName = if (booking.hallName.isNotBlank()) booking.hallName else hall.name

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pending_card_${booking.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top Row: Code, Hall, Title
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
                                        color = Color(0xFF78350F),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFDE68A))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = hallName,
                                        color = hall.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(hall.color.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = "بانتظار القرار ⏳",
                                    color = HospitalAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Event Title
                            Text(
                                text = booking.title,
                                color = Slate900,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Dept & Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                                Text(text = booking.dept, color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate500, modifier = Modifier.size(13.dp))
                                Text(text = "${booking.date} (${booking.timeSlot})", color = Slate700, fontSize = 12.sp)
                            }

                            // Applicant, Phone, Lecturer
                            Text(
                                text = "مقدم الطلب: ${booking.applicant} | تحويلة/جوال: ${booking.phone} | المحاضر: ${booking.lecturer}",
                                color = Slate600,
                                fontSize = 11.sp
                            )

                            if (booking.notes.isNotBlank()) {
                                Text(
                                    text = "ملاحظات وتجهيزات: ${booking.notes}",
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            }

                            // Action Buttons (Approve / Reject)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Reject
                                Button(
                                    onClick = {
                                        if (isAdmin) onReject(booking.id) else onUnlockAdmin()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusBookedRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .testTag("reject_btn_${booking.id}")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("رفض", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Approve
                                Button(
                                    onClick = {
                                        if (isAdmin) onApprove(booking.id) else onUnlockAdmin()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusAvailableGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("approve_btn_${booking.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("اعتماد وتثبيت", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
