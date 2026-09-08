package com.soleus.office

import com.soleus.office.domain.shouldRemind
import org.junit.Assert.*
import org.junit.Test

class WorkerLogicTest {
    @Test fun skipsOutsideHours() { assertFalse(shouldRemind(23 * 60, 540, 1080)) }
    @Test fun firesInsideHours() { assertTrue(shouldRemind(10 * 60, 540, 1080)) }
}
