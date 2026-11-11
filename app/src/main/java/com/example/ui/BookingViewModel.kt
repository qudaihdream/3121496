package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
    SCHEDULE("جدول القاعات"),
    BOOK("طلب حجز"),
    AI_ASSISTANT("مساعد الذكاء الاصطناعي ✨"),
    LOG("سجل الحجوزات"),
    PENDING("طلبات الانتظار")
}

data class ChatMessage(
    val id: String = (1000..9999).random().toString(),
    val sender: String, // "user" or "gemini"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiToast(val message: String, val isError: Boolean = false)

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookingRepository.create(application)
    private val prefs = PreferencesManager(application)

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val arabicDisplayDateFormatter = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))

    // Tab state
    private val _currentTab = MutableStateFlow(AppTab.SCHEDULE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Date navigation state
    private val _selectedDate = MutableStateFlow(dateFormatter.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Admin & Policy State
    private val _isAdmin = MutableStateFlow(prefs.isAdmin)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _isApprovalRequired = MutableStateFlow(prefs.isApprovalRequired)
    val isApprovalRequired: StateFlow<Boolean> = _isApprovalRequired.asStateFlow()

    private val _scriptUrl = MutableStateFlow(prefs.googleScriptUrl)
    val scriptUrl: StateFlow<String> = _scriptUrl.asStateFlow()

    // Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Toast and events
    private val _toastEvent = MutableSharedFlow<UiToast>()
    val toastEvent: SharedFlow<UiToast> = _toastEvent.asSharedFlow()

    // Dialogs
    private val _showPasscodeDialog = MutableStateFlow(false)
    val showPasscodeDialog: StateFlow<Boolean> = _showPasscodeDialog.asStateFlow()

    private val _showQrDialog = MutableStateFlow(false)
    val showQrDialog: StateFlow<Boolean> = _showQrDialog.asStateFlow()

    private val _showWebPortalDialog = MutableStateFlow(false)
    val showWebPortalDialog: StateFlow<Boolean> = _showWebPortalDialog.asStateFlow()

    private val _showSyncSettingsDialog = MutableStateFlow(false)
    val showSyncSettingsDialog: StateFlow<Boolean> = _showSyncSettingsDialog.asStateFlow()

    private val _activeReceiptBooking = MutableStateFlow<Booking?>(null)
    val activeReceiptBooking: StateFlow<Booking?> = _activeReceiptBooking.asStateFlow()

    private val _activeEditBooking = MutableStateFlow<Booking?>(null)
    val activeEditBooking: StateFlow<Booking?> = _activeEditBooking.asStateFlow()

    private val _bookingToDelete = MutableStateFlow<Booking?>(null)
    val bookingToDelete: StateFlow<Booking?> = _bookingToDelete.asStateFlow()

    // Gemini AI Assistant State
    private val aiService = com.example.ai.AiBookingService()
    private val _aiRecommendation = MutableStateFlow<com.example.ai.HallRecommendation?>(null)
    val aiRecommendation: StateFlow<com.example.ai.HallRecommendation?> = _aiRecommendation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiGeneratedAgenda = MutableStateFlow<String?>(null)
    val aiGeneratedAgenda: StateFlow<String?> = _aiGeneratedAgenda.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "gemini",
                text = "مرحباً بك! أنا مستشار Gemini الذكي لنظام حجز القاعات بمستشفى الرعاية المديدة بالظهران. كيف يمكنني مساعدتك في تنظيم فعاليتك أو اختيار القاعة المناسبة والتجهيزات اليوم؟"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _currentStreamingAiText = MutableStateFlow("")
    val currentStreamingAiText: StateFlow<String> = _currentStreamingAiText.asStateFlow()

    // Search and filters for Log Tab
    val searchQuery = MutableStateFlow("")
    val filterHallId = MutableStateFlow<String?>(null)
    val filterStatus = MutableStateFlow<String?>(null)

    // Form inputs state
    val formHallId = MutableStateFlow("h1")
    val formDate = MutableStateFlow(dateFormatter.format(Date()))
    val formStartTime = MutableStateFlow("09:00")
    val formEndTime = MutableStateFlow("10:00")
    val formTimeSlot = MutableStateFlow("09:00 - 10:00")
    val formDept = MutableStateFlow("")
    val formTitle = MutableStateFlow("")
    val formLecturer = MutableStateFlow("")
    val formApplicant = MutableStateFlow("")
    val formPhone = MutableStateFlow("")
    val formNotes = MutableStateFlow("")

    // Raw Bookings flows
    val allBookings: StateFlow<List<Booking>> = repository.allBookingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingBookings: StateFlow<List<Booking>> = repository.pendingBookingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered bookings for Log Tab
    val filteredBookings: StateFlow<List<Booking>> = combine(
        allBookings,
        searchQuery,
        filterHallId,
        filterStatus
    ) { list, query, hallId, status ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.dept.contains(query, ignoreCase = true) ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.lecturer.contains(query, ignoreCase = true) ||
                    item.applicant.contains(query, ignoreCase = true) ||
                    item.id.contains(query, ignoreCase = true)

            val matchesHall = hallId == null || item.hallId == hallId
            val matchesStatus = status == null || when (status) {
                "approved" -> item.isApproved
                "pending" -> item.isPending
                "rejected" -> item.isRejected
                else -> true
            }

            matchesQuery && matchesHall && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            triggerSync(isManual = false)
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        formDate.value = date
    }

    fun shiftDate(days: Int) {
        try {
            val cal = Calendar.getInstance()
            val parsed = dateFormatter.parse(_selectedDate.value) ?: Date()
            cal.time = parsed
            cal.add(Calendar.DAY_OF_YEAR, days)
            val newDateStr = dateFormatter.format(cal.time)
            setSelectedDate(newDateStr)
        } catch (_: Exception) {
            setSelectedDate(dateFormatter.format(Date()))
        }
    }

    fun setToday() {
        val today = dateFormatter.format(Date())
        setSelectedDate(today)
    }

    fun isDateWeekend(dateStr: String): Boolean {
        return try {
            val date = dateFormatter.parse(dateStr) ?: return false
            val cal = Calendar.getInstance()
            cal.time = date
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            // In Java Calendar: Friday is Calendar.FRIDAY (6), Saturday is Calendar.SATURDAY (7)
            dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY
        } catch (_: Exception) {
            false
        }
    }

    fun getArabicFormattedDate(dateStr: String): String {
        return try {
            val date = dateFormatter.parse(dateStr) ?: return dateStr
            arabicDisplayDateFormatter.format(date)
        } catch (_: Exception) {
            dateStr
        }
    }

    fun setFormStartTime(start: String) {
        formStartTime.value = start
        formTimeSlot.value = "$start - ${formEndTime.value}"
    }

    fun setFormEndTime(end: String) {
        formEndTime.value = end
        formTimeSlot.value = "${formStartTime.value} - $end"
    }

    fun setFormTimeRange(start: String, end: String) {
        formStartTime.value = start
        formEndTime.value = end
        formTimeSlot.value = "$start - $end"
    }

    fun onQuickBook(hallId: String, date: String, slot: String) {
        formHallId.value = hallId
        formDate.value = date
        formTimeSlot.value = slot
        val parts = slot.split(" - ")
        if (parts.size == 2) {
            formStartTime.value = parts[0].trim()
            formEndTime.value = parts[1].trim()
        }
        _currentTab.value = AppTab.BOOK
    }

    fun submitBooking(): Boolean {
        val hallId = formHallId.value
        val date = formDate.value
        val startTime = formStartTime.value.trim()
        val endTime = formEndTime.value.trim()
        val timeSlot = if (formTimeSlot.value.isNotBlank()) formTimeSlot.value.trim() else "$startTime - $endTime"
        val title = formTitle.value.trim()
        val dept = formDept.value.trim().ifBlank { "قسم طبي / مستفيد" }
        val lecturer = formLecturer.value.trim().ifBlank { "غير محدد" }
        val applicant = formApplicant.value.trim().ifBlank { "منسق القسم" }
        val phone = formPhone.value.trim().ifBlank { "تحويلة داخلية" }
        val notes = formNotes.value.trim()

        if (title.isBlank()) {
            emitToast("الرجاء إدخال اسم الفعالية / عنوان المحاضرة", isError = true)
            return false
        }

        if (startTime.isBlank() || endTime.isBlank()) {
            emitToast("الرجاء تحديد وقت البدء ووقت الانتهاء", isError = true)
            return false
        }

        // Compare hours/minutes to ensure end > start
        try {
            val startParts = startTime.split(":")
            val endParts = endTime.split(":")
            val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
            val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
            if (endMinutes <= startMinutes) {
                emitToast("يجب أن يكون وقت الانتهاء بعد وقت البدء!", isError = true)
                return false
            }
        } catch (_: Exception) {}

        if (isDateWeekend(date)) {
            emitToast("عفواً، لا يمكن الحجز في عطلة نهاية الأسبوع (الجمعة والسبت).", isError = true)
            return false
        }

        // Check conflict with existing approved or pending booking
        val isConflicting = allBookings.value.any {
            it.date == date && it.hallId == hallId && it.timeSlot == timeSlot && !it.isRejected
        }
        if (isConflicting) {
            emitToast("عفواً، هذا الموعد محجوز مسبقاً في هذه القاعة!", isError = true)
            return false
        }

        val status = if (_isApprovalRequired.value) "pending" else "approved"
        val newBooking = Booking(
            id = "BK-" + (1000..9999).random(),
            hallId = hallId,
            hallName = HallConstants.getHallById(hallId).name,
            date = date,
            timeSlot = timeSlot,
            dept = dept,
            title = title,
            lecturer = lecturer,
            applicant = applicant,
            phone = phone,
            notes = notes,
            status = status
        )

        viewModelScope.launch {
            repository.addBooking(newBooking, autoSync = true)
            // Clear input fields
            formTitle.value = ""
            formLecturer.value = ""
            formApplicant.value = ""
            formPhone.value = ""
            formNotes.value = ""
            formTimeSlot.value = ""

            emitToast(
                if (status == "pending") "تم إرسال طلب الحجز بنجاح ⏳ بانتظار موافقة المسؤول"
                else "تم تأكيد الحجز المباشر بنجاح 🟢"
            )
            setSelectedDate(date)
            _currentTab.value = AppTab.SCHEDULE
        }
        return true
    }

    fun approveBooking(id: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, "approved")
            emitToast("تم اعتماد الحجز ($id) وتحديث Google Sheets 🟢")
        }
    }

    fun rejectBooking(id: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, "rejected")
            emitToast("تم رفض طلب الحجز ($id) وتحديث Google Sheets ❌")
        }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch {
            repository.deleteBooking(id)
            emitToast("تم حذف الحجز ($id) بنجاح وإزالته من السجلات 🗑️")
        }
    }

    fun triggerSync(isManual: Boolean = false) {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.fetchAndMergeRemoteBookings()
            _isSyncing.value = false
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                if (isManual) {
                    emitToast(if (count > 0) "تمت المزامنة بنجاح وجلب $count حجز من Google Sheets 🔄" else "البيانات محدثة ومتزامنة سحابياً ☁️")
                }
            } else if (isManual) {
                emitToast("فشل الاتصال بـ Google Sheets، تم الاعتماد على البيانات المحلية", isError = true)
            }
        }
    }

    // Passcode Verification
    fun verifyPasscode(input: String): Boolean {
        if (input.trim() == PreferencesManager.ADMIN_PASSCODE) {
            _isAdmin.value = true
            prefs.isAdmin = true
            _showPasscodeDialog.value = false
            emitToast("تم تسجيل الدخول كمسؤول القاعات بنجاح 🔑")
            return true
        } else {
            emitToast("الرقم السري غير صحيح!", isError = true)
            return false
        }
    }

    fun logoutAdmin() {
        _isAdmin.value = false
        prefs.isAdmin = false
        emitToast("تم تسجيل الخروج من وضع المسؤول")
    }

    fun toggleApprovalPolicy(required: Boolean) {
        _isApprovalRequired.value = required
        prefs.isApprovalRequired = required
        emitToast(
            if (required) "تم اعتماد سياسة: بموافقة مسؤول القاعات (معتمد)"
            else "تم اعتماد سياسة: الحجز المباشر التلقائي"
        )
    }

    fun saveScriptUrl(url: String) {
        _scriptUrl.value = url.trim()
        prefs.googleScriptUrl = url.trim()
        _showSyncSettingsDialog.value = false
        emitToast("تم حفظ رابط Google Apps Script ومزامنة الإعدادات ⚙️")
        triggerSync(isManual = true)
    }

    fun openPasscodeDialog() { _showPasscodeDialog.value = true }
    fun closePasscodeDialog() { _showPasscodeDialog.value = false }

    fun openQrDialog() { _showQrDialog.value = true }
    fun closeQrDialog() { _showQrDialog.value = false }

    fun openWebPortalDialog() { _showWebPortalDialog.value = true }
    fun closeWebPortalDialog() { _showWebPortalDialog.value = false }

    fun openSyncSettingsDialog() { _showSyncSettingsDialog.value = true }
    fun closeSyncSettingsDialog() { _showSyncSettingsDialog.value = false }

    fun openReceipt(booking: Booking) { _activeReceiptBooking.value = booking }
    fun closeReceipt() { _activeReceiptBooking.value = null }

    fun openEditBooking(booking: Booking) { _activeEditBooking.value = booking }
    fun closeEditBooking() { _activeEditBooking.value = null }

    fun promptDeleteBooking(booking: Booking) { _bookingToDelete.value = booking }
    fun cancelDeleteBooking() { _bookingToDelete.value = null }
    fun confirmDeleteBooking() {
        val b = _bookingToDelete.value ?: return
        deleteBooking(b.id)
        _bookingToDelete.value = null
    }

    fun saveEditedBooking(updated: Booking) {
        viewModelScope.launch {
            repository.updateBooking(updated)
            _activeEditBooking.value = null
            emitToast("تم تحديث وحفظ بيانات الحجز (${updated.id}) بنجاح 🟢")
        }
    }

    fun requestHallRecommendation(title: String, attendeesCount: Int, dept: String, desc: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiRecommendation.value = null
            try {
                val rec = aiService.recommendHall(title, attendeesCount, dept, desc)
                _aiRecommendation.value = rec
                emitToast("تم استلام توصية Gemini الذكية بنجاح ✨")
            } catch (e: Exception) {
                emitToast("تعذر الحصول على التوصية: ${e.localizedMessage}", isError = true)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun applyRecommendationToForm(rec: com.example.ai.HallRecommendation) {
        formHallId.value = rec.recommendedHallId
        val currentNotes = formNotes.value.trim()
        val eqText = "التجهيزات المقترحة من Gemini: " + rec.suggestedEquipment.joinToString("، ")
        formNotes.value = if (currentNotes.isNotBlank()) "$currentNotes\n$eqText" else eqText
        _currentTab.value = AppTab.BOOK
        emitToast("تم تطبيق توصية القاعة (${rec.hallName}) في نموذج الحجز 🚀")
    }

    fun sendAiChatMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isAiLoading.value) return

        val userMsg = ChatMessage(sender = "user", text = trimmed)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiLoading.value = true
        _currentStreamingAiText.value = ""

        viewModelScope.launch {
            try {
                val summary = StringBuilder()
                summary.append("إجمالي الحجوزات: ${allBookings.value.size}\n")
                allBookings.value.take(5).forEach {
                    summary.append("- ${it.title} في ${it.hallName} (${it.date} ${it.timeSlot}) [${it.status}]\n")
                }

                val accumulated = StringBuilder()
                aiService.streamChatResponse(trimmed, summary.toString()).collect { chunk ->
                    accumulated.append(chunk)
                    _currentStreamingAiText.value = accumulated.toString()
                }

                val aiMsg = ChatMessage(sender = "gemini", text = accumulated.toString())
                _chatMessages.value = _chatMessages.value + aiMsg
                _currentStreamingAiText.value = ""
            } catch (e: Exception) {
                val errMsg = ChatMessage(sender = "gemini", text = "عذراً، حدث خطأ أثناء الاتصال بـ Gemini: ${e.localizedMessage}")
                _chatMessages.value = _chatMessages.value + errMsg
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun generateEventAgenda(title: String, hallName: String, duration: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiGeneratedAgenda.value = null
            try {
                val agenda = aiService.generateEventPlan(title, hallName, duration)
                _aiGeneratedAgenda.value = agenda
                emitToast("تم إنشاء جدول أعمال الفعالية بـ Gemini ✨")
            } catch (e: Exception) {
                emitToast("فشل إنشاء جدول الأعمال: ${e.localizedMessage}", isError = true)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearGeneratedAgenda() {
        _aiGeneratedAgenda.value = null
    }

    fun getExportCsvString(): String {
        return repository.exportBookingsCsv(allBookings.value)
    }

    private fun emitToast(msg: String, isError: Boolean = false) {
        viewModelScope.launch {
            _toastEvent.emit(UiToast(msg, isError))
        }
    }
}
