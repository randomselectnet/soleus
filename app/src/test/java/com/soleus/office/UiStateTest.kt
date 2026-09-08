package com.soleus.office

import com.soleus.office.ui.detail.clampProgress
import com.soleus.office.ui.detail.formatSure
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateTest {
    @Test fun progress_clamped() {
        assertEquals(1f, clampProgress(1.5f), 0f)
        assertEquals(0f, clampProgress(-0.2f), 0f)
        assertEquals(0.5f, clampProgress(0.5f), 0f)
    }

    @Test fun countdown_format_mmss() {
        assertEquals("01:00", formatSure(60))
        assertEquals("02:00", formatSure(120))
        assertEquals("00:05", formatSure(5))
    }
}
