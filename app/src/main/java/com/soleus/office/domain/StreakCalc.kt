package com.soleus.office.domain

fun calcStreak(days: Set<String>, today: String): Int {
    var count = 0
    var date = java.time.LocalDate.parse(today)
    while (date.toString() in days) {
        count++
        date = date.minusDays(1)
    }
    return count
}
