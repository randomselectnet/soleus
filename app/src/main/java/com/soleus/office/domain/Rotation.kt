package com.soleus.office.domain

fun dueExercise(exercises: List<String>, completed: List<String>): String {
    require(exercises.isNotEmpty()) { "dueExercise: egzersiz listesi boş olamaz" }
    val done = completed.toSet()
    return exercises.firstOrNull { it !in done } ?: exercises.first()
}

/**
 * Kapalı hareketleri rotasyon havuzundan çıkarır.
 * Satır yokluğu = açık kabul edilir; TÜMÜ kapalıysa fail-safe olarak tüm liste döner
 * (Worker + Home aynı filtreyi kullanır, boş havuz garanti edilir).
 */
fun enabledIds(all: List<String>, disabled: Collection<String>): List<String> {
    if (all.isEmpty()) return all
    if (disabled.isEmpty()) return all
    val kapali = disabled.toSet()
    val acik = all.filter { it !in kapali }
    return if (acik.isEmpty()) all else acik
}
