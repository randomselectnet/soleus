package com.soleus.office.data

import android.content.Context
import com.soleus.office.data.model.Exercise
import com.soleus.office.data.model.InfoPage
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
                animationAsset = o.getString("animationAsset"),
                faydaKisa = o.getString("faydaKisa"),
                dikkatKisa = o.getString("dikkatKisa"),
                dozajEtiket = o.getString("dozajEtiket"),
                zorluk = o.getInt("zorluk")
            )
        }
    }

    fun loadInfo(context: Context): List<InfoPage> {
        val json = context.assets.open("content/info_tr.json")
            .bufferedReader().use { it.readText() }
        return parseInfoJson(json)
    }

    fun parseInfoJson(json: String): List<InfoPage> {
        val arr = JSONArray(json)
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            val parJson = o.getJSONArray("paragraflar")
            InfoPage(
                id = o.getString("id"),
                baslik = o.getString("baslik"),
                paragraflar = List(parJson.length()) { j -> parJson.getString(j) },
                kapanis = o.getString("kapanis"),
                kaynak = if (o.isNull("kaynak")) null else o.getString("kaynak")
            )
        }
    }
}
