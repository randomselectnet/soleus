package com.soleus.office

import com.soleus.office.domain.durationLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationLabelTest {
    @Test fun sixtySec_isOneDk() {
        assertEquals("1 dk", durationLabel(60))
    }

    @Test fun ninetySec_roundsToTwoDk() {
        assertEquals("2 dk", durationLabel(90))
    }

    @Test fun oneTwentySec_isTwoDk() {
        assertEquals("2 dk", durationLabel(120))
    }
}
