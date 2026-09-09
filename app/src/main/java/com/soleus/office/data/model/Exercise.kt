package com.soleus.office.data.model

data class Exercise(
    val id: String,
    val trName: String,
    val steps: List<String>,
    val durationSec: Int,
    val benefit: String,
    val caution: String,
    val animationAsset: String,
    val faydaKisa: String,
    val dikkatKisa: String,
    val dozajEtiket: String,
    val zorluk: Int
)

data class InfoPage(
    val id: String,
    val baslik: String,
    val paragraflar: List<String>,
    val kapanis: String,
    val kaynak: String? = null
)
