package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.HallConstants
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

@Composable
fun ReceiptDialog(
    booking: Booking,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val hall = HallConstants.getHallById(booking.hallId)
    val hallName = if (booking.hallName.isNotBlank()) booking.hallName else hall.name

    val statusText = when {
        booking.isApproved -> "مؤكد 🔒"
        booking.isPending -> "بانتظار الموافقة ⏳"
        else -> "مرفوض ❌"
    }

    fun shareReceipt() {
        val receiptText = """
════════════════════════════════
مستشفى الرعاية المديدة بالظهران
إشعار حجز قاعة رسمية
════════════════════════════════
رمز الحجز: ${booking.id}
القاعة: $hallName
التاريخ: ${booking.date}
الفترة الزمنية: ${booking.timeSlot}
القسم المستفيد: ${booking.dept}
عنوان الفعالية: ${booking.title}
المحاضر / المتحدث: ${booking.lecturer}
مقدم الطلب: ${booking.applicant} (تحويلة: ${booking.phone})
الحالة: $statusText
${if (booking.notes.isNotBlank()) "ملاحظات: ${booking.notes}" else ""}
════════════════════════════════
نظام حجز القاعات الموحد لجميع الأقسام
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "إشعار حجز قاعة - ${booking.id}")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة إشعار الحجز"))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "إشعار حجز قاعة رسمية",
                        color = Slate900,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Slate500)
                    }
                }

                // Official Voucher Content
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Hospital Brand
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_icon_fg_1794414150354),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "مستشفى الرعاية المديدة بالظهران",
                                    color = HospitalTealPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "إشعار اعتماد وجدولة القاعات",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = booking.id,
                                color = HospitalTealPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFCCFBF1))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Slate200)
                        )

                        // Details Grid
                        DetailRow(label = "القاعة:", value = hallName, isBold = true)
                        DetailRow(label = "التاريخ:", value = booking.date)
                        DetailRow(label = "الموعد:", value = booking.timeSlot, isBold = true)
                        DetailRow(label = "القسم المستفيد:", value = booking.dept)
                        DetailRow(label = "عنوان الفعالية:", value = booking.title, isBold = true)
                        if (booking.lecturer.isNotBlank()) {
                            DetailRow(label = "المحاضر:", value = booking.lecturer)
                        }
                        DetailRow(label = "مقدم الطلب:", value = "${booking.applicant} (تحويلة: ${booking.phone})")

                        // Status Row
                        val (sBg, sColor) = when {
                            booking.isApproved -> Pair(StatusAvailableGreenBg, StatusAvailableGreen)
                            booking.isPending -> Pair(StatusPendingAmberBg, StatusPendingAmber)
                            else -> Pair(StatusBookedRedBg, StatusBookedRed)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "الحالة:", color = Slate600, fontSize = 12.sp)
                            Text(
                                text = statusText,
                                color = sColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(sBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (booking.notes.isNotBlank()) {
                            DetailRow(label = "ملاحظات:", value = booking.notes)
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إغلاق", color = Slate600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { shareReceipt() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HospitalTealPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("share_receipt_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة الإشعار", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, color = Slate600, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = Slate900,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
