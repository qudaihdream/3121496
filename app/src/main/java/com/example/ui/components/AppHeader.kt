package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PreferencesManager
import com.example.ui.theme.HospitalAmber
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.HospitalTealSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppHeader(
    isAdmin: Boolean,
    isSyncing: Boolean,
    onRefreshClick: () -> Unit,
    onQrClick: () -> Unit,
    onAdminClick: () -> Unit,
    onWebPortalClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(HospitalTealDark, HospitalTealPrimary, HospitalTealSecondary)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Row: Brand & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0F3B38))
                                .border(1.5.dp, HospitalAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_fg_1794414150354),
                                contentDescription = "شعار المستشفى",
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "مستشفى الرعاية المديدة بالظهران",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "نظام حجز القاعات الموحد لجميع الأقسام",
                                color = Color(0xFF99F6E4),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Cloud Sync Live Status Chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, Color(0x3334D399), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "متصل بالسحابة",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "مزامنة سحابية 🟢",
                            color = Color(0xFFA7F3D0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Action Controls Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Google Sheets Link Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF059669))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PreferencesManager.GOOGLE_SHEET_URL))
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("google_sheets_link_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "شيت الحجوزات",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Google Sheets 📊",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Manual Refresh Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x26FFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                            .clickable(enabled = !isSyncing) { onRefreshClick() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("refresh_sync_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .then(if (isSyncing) Modifier.rotate(rotation) else Modifier)
                        )
                        Text(
                            text = if (isSyncing) "جاري التحديث..." else "تحديث 🔄",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // QR Code Poster Launcher
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HospitalAmber)
                            .clickable { onQrClick() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("qr_launcher_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "رمز QR",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "رمز QR للحجز",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Web Portal Interactive Site Launcher
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D9488))
                            .clickable { onWebPortalClick() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("web_portal_launcher_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "بوابة الموقع",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "بوابة الموقع 🌐",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Admin Mode Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAdmin) HospitalAmber else Color(0x26FFFFFF))
                            .border(
                                1.dp,
                                if (isAdmin) HospitalAmber else Color(0x33FFFFFF),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onAdminClick() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("admin_mode_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "وضع المسؤول",
                            tint = if (isAdmin) Color(0xFF0F172A) else HospitalAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isAdmin) "مسؤول القاعات (مُفعل 🔓)" else "وضع مسؤول القاعات",
                            color = if (isAdmin) Color(0xFF0F172A) else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
