package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.BookingDao
import com.example.data.local.PreferencesManager
import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.data.remote.GoogleSheetSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingRepository(
    private val bookingDao: BookingDao,
    private val preferencesManager: PreferencesManager,
    private val syncService: GoogleSheetSyncService = GoogleSheetSyncService()
) {
    val allBookingsFlow: Flow<List<Booking>> = bookingDao.getAllBookingsFlow()
    val pendingBookingsFlow: Flow<List<Booking>> = bookingDao.getPendingBookingsFlow()

    fun getBookingsForDateFlow(date: String): Flow<List<Booking>> {
        return bookingDao.getBookingsByDateFlow(date)
    }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        if (!preferencesManager.isInitialSeedDone) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val seedBooking = Booking(
                id = "BK-1001",
                hallId = "h1",
                hallName = "القاعة الكبيرة",
                date = today,
                timeSlot = "09:00 - 10:00",
                dept = "قسم التمريض",
                title = "الممارسة السريرية المتقدمة",
                lecturer = "د. عبدالمجيد الغامدي",
                applicant = "فاطمة الشهري",
                phone = "4421",
                notes = "تجهيز بروجكتر ونظام صوتي",
                status = "approved"
            )
            bookingDao.insertBooking(seedBooking)
            preferencesManager.isInitialSeedDone = true
        }
    }

    suspend fun addBooking(booking: Booking, autoSync: Boolean = true): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            bookingDao.insertBooking(booking)
            if (autoSync) {
                CoroutineScope(Dispatchers.IO).launch {
                    syncService.syncBookingToSheet(
                        scriptUrl = preferencesManager.googleScriptUrl,
                        booking = booking,
                        action = "create"
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(id: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = bookingDao.getBookingById(id)
            if (existing != null) {
                val updated = existing.copy(status = newStatus)
                bookingDao.updateBooking(updated)
                CoroutineScope(Dispatchers.IO).launch {
                    syncService.syncBookingToSheet(
                        scriptUrl = preferencesManager.googleScriptUrl,
                        booking = updated,
                        action = "update_status"
                    )
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("الحجز غير موجود"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBooking(booking: Booking): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            bookingDao.updateBooking(booking)
            CoroutineScope(Dispatchers.IO).launch {
                syncService.syncBookingToSheet(
                    scriptUrl = preferencesManager.googleScriptUrl,
                    booking = booking,
                    action = "update"
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBooking(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = bookingDao.getBookingById(id)
            bookingDao.deleteBookingById(id)
            if (existing != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    syncService.syncBookingToSheet(
                        scriptUrl = preferencesManager.googleScriptUrl,
                        booking = existing,
                        action = "delete"
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAndMergeRemoteBookings(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val remoteList = syncService.fetchLiveBookings(
                scriptUrl = preferencesManager.googleScriptUrl,
                csvUrl = PreferencesManager.GOOGLE_SHEET_PUB_CSV
            )

            if (remoteList.isNotEmpty()) {
                bookingDao.insertAll(remoteList)
                preferencesManager.lastSyncTime = System.currentTimeMillis()
                Result.success(remoteList.size)
            } else {
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportBookingsCsv(bookings: List<Booking>): String {
        val sb = StringBuilder()
        sb.append('\uFEFF') // UTF-8 BOM for Arabic support in Excel
        sb.append("رمز الحجز,القاعة,التاريخ,الوقت,القسم,عنوان الفعالية,المحاضر,مقدم الطلب,التحويلة,الحالة,ملاحظات\n")
        for (b in bookings) {
            val hallName = if (b.hallName.isNotBlank()) b.hallName else HallConstants.getHallById(b.hallId).name
            val statusLabel = when {
                b.isApproved -> "محجوز (مؤكد)"
                b.isPending -> "في الانتظار"
                b.isRejected -> "مرفوض"
                else -> b.status
            }
            sb.append("\"${b.id}\",\"${hallName}\",\"${b.date}\",\"${b.timeSlot}\",\"${b.dept}\",\"${b.title}\",\"${b.lecturer}\",\"${b.applicant}\",\"${b.phone}\",\"${statusLabel}\",\"${b.notes}\"\n")
        }
        return sb.toString()
    }

    companion object {
        fun create(context: Context): BookingRepository {
            val db = AppDatabase.getDatabase(context)
            val prefs = PreferencesManager(context)
            return BookingRepository(db.bookingDao(), prefs)
        }
    }
}
