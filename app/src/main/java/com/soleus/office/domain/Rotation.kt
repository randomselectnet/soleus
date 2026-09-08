package com.soleus.office.domain

fun dueExercise(exercises: List<String>, completed: List<String>): String {
    val done = completed.toSet()
    return exercises.firstOrNull { it !in done } ?: exercises.first()
}
