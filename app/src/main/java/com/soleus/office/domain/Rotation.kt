package com.soleus.office.domain

fun dueExercise(exercises: List<String>, completed: List<String>): String {
    require(exercises.isNotEmpty()) { "dueExercise: egzersiz listesi boş olamaz" }
    val done = completed.toSet()
    return exercises.firstOrNull { it !in done } ?: exercises.first()
}
