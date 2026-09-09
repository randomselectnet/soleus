package com.soleus.office.data.model

data class Exercise(
    val id: String,
    val trName: String,
    val steps: List<String>,
    val durationSec: Int,
    val benefit: String,
    val caution: String,
    val animationAsset: String
)
