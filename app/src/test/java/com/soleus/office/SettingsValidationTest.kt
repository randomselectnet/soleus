package com.soleus.office
import org.junit.Test
import org.junit.Assert.*
class SettingsValidationTest {
    @Test fun interval_allowedValues() {
        val allowed = setOf(30,45,60,90)
        assertTrue(60 in allowed); assertFalse(20 in allowed)
    }
    @Test fun workRange_valid() {
        fun valid(s:Int,e:Int) = s in 0..1439 && e in 0..1439 && s < e
        assertTrue(valid(540,1080)); assertFalse(valid(1080,540))
    }
}
