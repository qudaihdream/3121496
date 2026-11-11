package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY date DESC, timeSlot ASC")
    fun getAllBookingsFlow(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE date = :date ORDER BY timeSlot ASC")
    fun getBookingsByDateFlow(date: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE status = 'pending' ORDER BY date ASC, timeSlot ASC")
    fun getPendingBookingsFlow(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: String): Booking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<Booking>)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: String)

    @Query("DELETE FROM bookings")
    suspend fun deleteAll()
}
