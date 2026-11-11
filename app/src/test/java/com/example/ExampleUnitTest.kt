package com.example

import com.example.data.model.Booking
import com.example.data.model.HallConstants
import com.example.data.model.TimeSlotConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testHallsConfiguration() {
        assertEquals(3, HallConstants.HALLS.size)
        assertEquals("h1", HallConstants.getHallById("h1").id)
        assertEquals(100, HallConstants.getHallById("h1").capacity)
        assertEquals("القاعة الكبيرة", HallConstants.getHallById("h1").name)

        assertEquals("h2", HallConstants.getHallIdByName("القاعة الزجاجية"))
        assertEquals("h3", HallConstants.getHallIdByName("القاعة الداخلية"))
    }

    @Test
    fun testTimeSlots() {
        assertEquals(8, TimeSlotConstants.SLOTS.size)
        assertTrue(TimeSlotConstants.SLOTS.contains("08:00 - 09:00"))
        assertTrue(TimeSlotConstants.SLOTS.contains("15:00 - 16:00"))
    }

    @Test
    fun testBookingStatusProperties() {
        val approvedBooking = Booking(
            id = "BK-1001",
            date = "2026-11-11",
            timeSlot = "09:00 - 10:00",
            dept = "قسم التمريض",
            title = "الممارسة السريرية",
            status = "approved"
        )
        assertTrue(approvedBooking.isApproved)
        assertFalse(approvedBooking.isPending)
        assertFalse(approvedBooking.isRejected)

        val pendingBooking = approvedBooking.copy(status = "pending")
        assertTrue(pendingBooking.isPending)
        assertFalse(pendingBooking.isApproved)

        val rejectedBooking = approvedBooking.copy(status = "rejected")
        assertTrue(rejectedBooking.isRejected)
        assertFalse(rejectedBooking.isApproved)
    }
}
