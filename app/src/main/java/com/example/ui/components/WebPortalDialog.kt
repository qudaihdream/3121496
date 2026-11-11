package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.PreferencesManager
import com.example.ui.theme.Emerald600
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showDeployGuide by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF0F766E), RoundedCornerShape(16.dp))
                .testTag("web_portal_dialog"),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HospitalTealDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
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
                                    .size(32.dp)
                                    .background(Color(0xFF0D9488), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Web Portal",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "بوابة نظام حجز القاعات على الويب 🌐",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "النسخة التفاعلية الكاملة لموقع الإنترنت",
                                    color = Color(0xFFCCFBF1),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Deployment Guide toggle
                            IconButton(
                                onClick = { showDeployGuide = !showDeployGuide },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "طريقة النشر أونلاين",
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Reload WebView
                            IconButton(
                                onClick = { webViewInstance?.reload() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "إعادة تحميل",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Open in external browser
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PreferencesManager.GOOGLE_SHEET_URL))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "فتح الشيت السحابي",
                                    tint = Color(0xFFA7F3D0),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Close Dialog
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // If guide is open, show guide banner
                if (showDeployGuide) {
                    WebDeploymentGuideCard(onClose = { showDeployGuide = false })
                }

                // Embedded Web Portal (Local assets index.html with live remote Google Sheets sync)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    displayZoomControls = false
                                    builtInZoomControls = true
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                }
                                webChromeClient = WebChromeClient()
                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                        return false
                                    }
                                }
                                loadUrl("file:///android_asset/web/index.html")
                                webViewInstance = this
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WebDeploymentGuideCard(onClose: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val appsScriptSnippet = """
function doGet() {
  return HtmlService.createHtmlOutputFromFile('index')
    .setTitle('مستشفى الرعاية المديدة بالظهران - نظام حجز القاعات')
    .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL);
}
    """.trimIndent()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "كيف تنشر هذا النظام كموقع على الإنترنت مجاناً؟ 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Slate500, modifier = Modifier.size(18.dp))
                }
            }

            Text(
                text = "ملف الموقع الكامل موجود في المشروع باسم: web/index.html ويمكنك نشره أونلاين بـ 3 طرق:",
                fontSize = 12.sp,
                color = Slate700
            )

            // Method 1: Google Apps Script Web App
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "1. نشره كرابط مباشر من Google Apps Script (موصى به وبدون سيرفر):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = "• في شيت Google Sheets اضغط Extensions > Apps Script.\n• أنشئ ملف HTML باسم index والصق فيه محتوى ملف web/index.html.\n• أضف كود doGet() التالي ثم اضغط Deploy > New deployment > Web app.",
                        fontSize = 11.sp,
                        color = Color(0xFF047857),
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF064E3B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = appsScriptSnippet,
                            color = Color(0xFF6EE7B7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(appsScriptSnippet)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Method 2: GitHub Pages or Firebase Hosting
            Text(
                text = "2. نشره على GitHub Pages أو Firebase Hosting أو Netlify: ما عليك سوى رفع ملف web/index.html وسيعمل الموقع فوراً برابط عالمي HTTPS.",
                fontSize = 11.sp,
                color = Slate600
            )
        }
    }
}
