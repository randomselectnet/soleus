package com.soleus.office.domain

/**
 * Süre etiketi: saniyeyi en yakın dakikaya yuvarlar (en az 1 dk).
 * 60 -> "1 dk", 90/120 -> "2 dk". Üç tüketici de (Home, Liste,
 * bildirim) aynı helper'ı kullanır; integer division hatasını önler.
 */
fun durationLabel(durationSec: Int): String {
    val s = durationSec.coerceAtLeast(0)
    val dk = ((s + 30) / 60).coerceAtLeast(1)
    return "$dk dk"
}
