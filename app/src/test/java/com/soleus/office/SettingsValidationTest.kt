package com.soleus.office
import com.soleus.office.ui.settings.ALLOWED_INTERVALS
import com.soleus.office.ui.settings.isWorkRangeValid
import org.junit.Test
import org.junit.Assert.*
class SettingsValidationTest {
    @Test fun interval_allowedValues() {
        assertTrue(60 in ALLOWED_INTERVALS); assertFalse(20 in ALLOWED_INTERVALS)
    }
    @Test fun workRange_valid() {
        assertTrue(isWorkRangeValid(540,1080)); assertFalse(isWorkRangeValid(1080,540))
    }
}
