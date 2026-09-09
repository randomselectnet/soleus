package com.soleus.office
import com.soleus.office.domain.isQuietTime
import com.soleus.office.ui.settings.ALLOWED_INTERVALS
import com.soleus.office.ui.settings.CUSTOM_INTERVALS
import com.soleus.office.ui.settings.isWorkRangeValid
import org.junit.Test
import org.junit.Assert.*
class SettingsValidationTest {
    @Test fun interval_allowedValues() {
        // Saatlik / 2 saatte bir / özel ritim değerleri.
        assertTrue(60 in ALLOWED_INTERVALS)
        assertTrue(120 in ALLOWED_INTERVALS)
        assertTrue(30 in ALLOWED_INTERVALS)
        assertTrue(45 in ALLOWED_INTERVALS)
        assertTrue(90 in ALLOWED_INTERVALS)
        assertFalse(20 in ALLOWED_INTERVALS)
        assertFalse(15 in ALLOWED_INTERVALS)
    }
    @Test fun customIntervals_subsetOfAllowed() {
        assertTrue(ALLOWED_INTERVALS.containsAll(CUSTOM_INTERVALS))
        assertEquals(listOf(30, 45, 60, 90, 120), CUSTOM_INTERVALS)
    }
    @Test fun workRange_valid() {
        assertTrue(isWorkRangeValid(540,1080)); assertFalse(isWorkRangeValid(1080,540))
    }
    @Test fun quietTime_overnightWindow() {
        // 21:00 -> 08:00 saran aralık.
        assertTrue(isQuietTime(22 * 60, 21 * 60, 8 * 60, true))
        assertTrue(isQuietTime(3 * 60, 21 * 60, 8 * 60, true))
        assertFalse(isQuietTime(12 * 60, 21 * 60, 8 * 60, true))
        // Kapalıysa asla sessiz değil.
        assertFalse(isQuietTime(22 * 60, 21 * 60, 8 * 60, false))
    }
    @Test fun quietTime_dayWindow() {
        assertTrue(isQuietTime(12 * 60, 9 * 60, 18 * 60, true))
        assertFalse(isQuietTime(20 * 60, 9 * 60, 18 * 60, true))
    }
}
