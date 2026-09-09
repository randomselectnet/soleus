package com.soleus.office.domain

fun shouldRemind(nowMin: Int, start: Int, end: Int) = nowMin in start until end

/**
 * Sessiz saat kontrolü. Gece yarısını saran aralık desteklenir (örn. 21:00 -> 08:00).
 * Kapalıysa her zaman false.
 */
fun isQuietTime(nowMin: Int, quietStart: Int, quietEnd: Int, quietEnabled: Boolean): Boolean {
    if (!quietEnabled) return false
    return if (quietStart <= quietEnd) {
        nowMin in quietStart until quietEnd
    } else {
        nowMin >= quietStart || nowMin < quietEnd
    }
}
