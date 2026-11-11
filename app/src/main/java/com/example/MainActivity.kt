package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.BookingViewModel
import com.example.ui.components.AiAssistantTab
import com.example.ui.components.AppHeader
import com.example.ui.components.BookingFormTab
import com.example.ui.components.BookingLogTab
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.EditBookingDialog
import com.example.ui.components.PasscodeDialog
import com.example.ui.components.PendingRequestsTab
import com.example.ui.components.QrCodeDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.ScheduleGridTab
import com.example.ui.components.SyncSettingsDialog
import com.example.ui.theme.DhahranHallsTheme
import com.example.ui.theme.HospitalAmber
import com.example.ui.theme.HospitalTealDark
import com.example.ui.theme.HospitalTealPrimary
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.util.configureAppCheck
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            DhahranHallsTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    HospitalHallBookingApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HospitalHallBookingApp(viewModel: BookingViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentTab by viewModel.currentTab.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val isApprovalRequired by viewModel.isApprovalRequired.collectAsState()
    val scriptUrl by viewModel.scriptUrl.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val allBookings by viewModel.allBookings.collectAsState()
    val filteredBookings by viewModel.filteredBookings.collectAsState()
    val pendingBookings by viewModel.pendingBookings.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterHallId by viewModel.filterHallId.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()

    val formHallId by viewModel.formHallId.collectAsState()
    val formDate by viewModel.formDate.collectAsState()
    val formStartTime by viewModel.formStartTime.collectAsState()
    val formEndTime by viewModel.formEndTime.collectAsState()
    val formTimeSlot by viewModel.formTimeSlot.collectAsState()
    val formDept by viewModel.formDept.collectAsState()
    val formTitle by viewModel.formTitle.collectAsState()
    val formLecturer by viewModel.formLecturer.collectAsState()
    val formApplicant by viewModel.formApplicant.collectAsState()
    val formPhone by viewModel.formPhone.collectAsState()
    val formNotes by viewModel.formNotes.collectAsState()

    val showPasscodeDialog by viewModel.showPasscodeDialog.collectAsState()
    val showQrDialog by viewModel.showQrDialog.collectAsState()
    val showSyncSettingsDialog by viewModel.showSyncSettingsDialog.collectAsState()
    val activeReceiptBooking by viewModel.activeReceiptBooking.collectAsState()
    val activeEditBooking by viewModel.activeEditBooking.collectAsState()
    val bookingToDelete by viewModel.bookingToDelete.collectAsState()

    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiGeneratedAgenda by viewModel.aiGeneratedAgenda.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val currentStreamingAiText by viewModel.currentStreamingAiText.collectAsState()

    val isWeekend = viewModel.isDateWeekend(selectedDate)
    val dateDisplayArabic = viewModel.getArabicFormattedDate(selectedDate)

    // Back button handling
    BackHandler(enabled = currentTab != AppTab.SCHEDULE) {
        viewModel.selectTab(AppTab.SCHEDULE)
    }

    // Toast/Snackbar notifications observer
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { toast ->
            snackbarHostState.showSnackbar(toast.message)
        }
    }

    fun shareExportedCsv() {
        val csv = viewModel.getExportCsvString()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csv)
            putExtra(Intent.EXTRA_SUBJECT, "سجل حجوزات مستشفى الرعاية المديدة بالظهران")
            type = "text/csv"
        }
        context.startActivity(Intent.createChooser(sendIntent, "تصدير سجل الحجوزات إلى Excel"))
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HospitalTealDark)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                AppHeader(
                    isAdmin = isAdmin,
                    isSyncing = isSyncing,
                    onRefreshClick = { viewModel.triggerSync(isManual = true) },
                    onQrClick = { viewModel.openQrDialog() },
                    onAdminClick = {
                        if (isAdmin) viewModel.logoutAdmin() else viewModel.openPasscodeDialog()
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                // 1. Live Schedule
                NavigationBarItem(
                    selected = currentTab == AppTab.SCHEDULE,
                    onClick = { viewModel.selectTab(AppTab.SCHEDULE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "جدول القاعات",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "جدول القاعات",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == AppTab.SCHEDULE) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HospitalTealPrimary,
                        selectedTextColor = HospitalTealPrimary,
                        indicatorColor = Color(0xFFCCFBF1),
                        unselectedIconColor = Slate600,
                        unselectedTextColor = Slate600
                    ),
                    modifier = Modifier.testTag("nav_item_schedule")
                )

                // 2. Submit Booking
                NavigationBarItem(
                    selected = currentTab == AppTab.BOOK,
                    onClick = { viewModel.selectTab(AppTab.BOOK) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "طلب حجز",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "طلب حجز",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == AppTab.BOOK) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HospitalTealPrimary,
                        selectedTextColor = HospitalTealPrimary,
                        indicatorColor = Color(0xFFCCFBF1),
                        unselectedIconColor = Slate600,
                        unselectedTextColor = Slate600
                    ),
                    modifier = Modifier.testTag("nav_item_book")
                )

                // 3. Gemini AI Assistant
                NavigationBarItem(
                    selected = currentTab == AppTab.AI_ASSISTANT,
                    onClick = { viewModel.selectTab(AppTab.AI_ASSISTANT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "مساعد الذكاء",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "مساعد الذكاء ✨",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == AppTab.AI_ASSISTANT) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HospitalTealPrimary,
                        selectedTextColor = HospitalTealPrimary,
                        indicatorColor = Color(0xFFCCFBF1),
                        unselectedIconColor = Slate600,
                        unselectedTextColor = Slate600
                    ),
                    modifier = Modifier.testTag("nav_item_ai")
                )

                // 4. Full Log
                NavigationBarItem(
                    selected = currentTab == AppTab.LOG,
                    onClick = { viewModel.selectTab(AppTab.LOG) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "سجل الحجوزات",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "سجل الحجوزات",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == AppTab.LOG) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HospitalTealPrimary,
                        selectedTextColor = HospitalTealPrimary,
                        indicatorColor = Color(0xFFCCFBF1),
                        unselectedIconColor = Slate600,
                        unselectedTextColor = Slate600
                    ),
                    modifier = Modifier.testTag("nav_item_log")
                )

                // 4. Pending Approvals with Badge
                NavigationBarItem(
                    selected = currentTab == AppTab.PENDING,
                    onClick = { viewModel.selectTab(AppTab.PENDING) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (pendingBookings.isNotEmpty()) {
                                    Badge(
                                        containerColor = HospitalAmber,
                                        contentColor = Slate900
                                    ) {
                                        Text(
                                            text = "${pendingBookings.size}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PendingActions,
                                contentDescription = "طلبات الانتظار",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "طلبات الانتظار",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == AppTab.PENDING) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HospitalTealPrimary,
                        selectedTextColor = HospitalTealPrimary,
                        indicatorColor = Color(0xFFCCFBF1),
                        unselectedIconColor = Slate600,
                        unselectedTextColor = Slate600
                    ),
                    modifier = Modifier.testTag("nav_item_pending")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate50)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.SCHEDULE -> ScheduleGridTab(
                        selectedDate = selectedDate,
                        dateDisplayArabic = dateDisplayArabic,
                        isWeekend = isWeekend,
                        bookings = allBookings,
                        onDateChange = { viewModel.setSelectedDate(it) },
                        onDateShift = { viewModel.shiftDate(it) },
                        onTodayClick = { viewModel.setToday() },
                        onQuickBook = { hId, d, s -> viewModel.onQuickBook(hId, d, s) },
                        onBookingClick = { viewModel.openReceipt(it) }
                    )

                    AppTab.BOOK -> BookingFormTab(
                        hallId = formHallId,
                        date = formDate,
                        startTime = formStartTime,
                        endTime = formEndTime,
                        timeSlot = formTimeSlot,
                        dept = formDept,
                        title = formTitle,
                        lecturer = formLecturer,
                        applicant = formApplicant,
                        phone = formPhone,
                        notes = formNotes,
                        allBookings = allBookings,
                        onHallChange = { viewModel.formHallId.value = it },
                        onDateChange = { viewModel.formDate.value = it },
                        onStartTimeChange = { viewModel.setFormStartTime(it) },
                        onEndTimeChange = { viewModel.setFormEndTime(it) },
                        onTimeSlotChange = { viewModel.formTimeSlot.value = it },
                        onDeptChange = { viewModel.formDept.value = it },
                        onTitleChange = { viewModel.formTitle.value = it },
                        onLecturerChange = { viewModel.formLecturer.value = it },
                        onApplicantChange = { viewModel.formApplicant.value = it },
                        onPhoneChange = { viewModel.formPhone.value = it },
                        onNotesChange = { viewModel.formNotes.value = it },
                        onSubmit = { viewModel.submitBooking() },
                        onCancel = { viewModel.selectTab(AppTab.SCHEDULE) },
                        onOpenAiAdvisor = { viewModel.selectTab(AppTab.AI_ASSISTANT) }
                    )

                    AppTab.AI_ASSISTANT -> AiAssistantTab(
                        aiRecommendation = aiRecommendation,
                        isAiLoading = isAiLoading,
                        aiGeneratedAgenda = aiGeneratedAgenda,
                        chatMessages = chatMessages,
                        currentStreamingAiText = currentStreamingAiText,
                        onAskRecommendation = { title, attendees, dept, desc ->
                            viewModel.requestHallRecommendation(title, attendees, dept, desc)
                        },
                        onApplyRecommendation = { rec ->
                            viewModel.applyRecommendationToForm(rec)
                        },
                        onSendMessage = { text ->
                            viewModel.sendAiChatMessage(text)
                        },
                        onGenerateAgenda = { title, hallName, dur ->
                            viewModel.generateEventAgenda(title, hallName, dur)
                        },
                        onClearAgenda = {
                            viewModel.clearGeneratedAgenda()
                        }
                    )

                    AppTab.LOG -> BookingLogTab(
                        bookings = filteredBookings,
                        searchQuery = searchQuery,
                        selectedHallFilter = filterHallId,
                        selectedStatusFilter = filterStatus,
                        isAdmin = isAdmin,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        onHallFilterChange = { viewModel.filterHallId.value = it },
                        onStatusFilterChange = { viewModel.filterStatus.value = it },
                        onEditClick = { viewModel.openEditBooking(it) },
                        onDeleteClick = { viewModel.promptDeleteBooking(it) },
                        onReceiptClick = { viewModel.openReceipt(it) },
                        onExportCsv = { shareExportedCsv() },
                        onOpenSyncSettings = { viewModel.openSyncSettingsDialog() }
                    )

                    AppTab.PENDING -> PendingRequestsTab(
                        pendingBookings = pendingBookings,
                        isApprovalRequired = isApprovalRequired,
                        isAdmin = isAdmin,
                        onTogglePolicy = { viewModel.toggleApprovalPolicy(it) },
                        onApprove = { viewModel.approveBooking(it) },
                        onReject = { viewModel.rejectBooking(it) },
                        onUnlockAdmin = { viewModel.openPasscodeDialog() }
                    )
                }
            }
        }
    }

    // Modal Overlays
    if (showPasscodeDialog) {
        PasscodeDialog(
            onDismiss = { viewModel.closePasscodeDialog() },
            onConfirm = { input -> viewModel.verifyPasscode(input) }
        )
    }

    if (showQrDialog) {
        QrCodeDialog(
            onDismiss = { viewModel.closeQrDialog() }
        )
    }

    if (showSyncSettingsDialog) {
        SyncSettingsDialog(
            initialUrl = scriptUrl,
            onDismiss = { viewModel.closeSyncSettingsDialog() },
            onSaveUrl = { viewModel.saveScriptUrl(it) }
        )
    }

    activeReceiptBooking?.let { booking ->
        ReceiptDialog(
            booking = booking,
            onDismiss = { viewModel.closeReceipt() }
        )
    }

    activeEditBooking?.let { booking ->
        EditBookingDialog(
            booking = booking,
            onDismiss = { viewModel.closeEditBooking() },
            onSave = { updated -> viewModel.saveEditedBooking(updated) }
        )
    }

    bookingToDelete?.let { booking ->
        DeleteConfirmDialog(
            booking = booking,
            onDismiss = { viewModel.cancelDeleteBooking() },
            onConfirm = { viewModel.confirmDeleteBooking() }
        )
    }
}
