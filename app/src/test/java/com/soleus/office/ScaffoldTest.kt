package com.soleus.office

import org.junit.Assert.assertEquals
import org.junit.Test

class ScaffoldTest {
    @Test
    fun packageName_isCorrect() {
        assertEquals("com.soleus.office", BuildConfig.APPLICATION_ID)
    }
}
