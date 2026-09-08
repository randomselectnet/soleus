package com.soleus.office.data

import android.content.Context
import com.soleus.office.data.db.Exercise
import org.json.JSONArray

object ContentLoader {
    fun load(context: Context): List<Exercise> {
        val json = context.assets.open("content/exercises_tr.json")
            .bufferedReader().use { it.readText() }
        return parseJson(json)
    }

    fun parseJson(json: String): List<Exercise> {
        val arr = JSONArray(json)
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            val stepsJson = o.getJSONArray("steps")
            Exercise(
                id = o.getString("id"),
                trName = o.getString("trName"),
                steps = List(stepsJson.length()) { j -> stepsJson.getString(j) },
                durationSec = o.getInt("durationSec"),
                benefit = o.getString("benefit"),
                caution = o.getString("caution"),
                animationAsset = o.getString("animationAsset")
            )
        }
    }
}
