package com.example.themoviesdb.utils

import android.util.Log
import com.example.themoviesdb.showDesc.model.Episode
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

object Utils {
    var episodList = listOf<Episode>()
    lateinit var currentShowTitle: String
    lateinit var currentShowSeason: String
    lateinit var currentShowEpisode: String

    fun toPrettyFormat(jsonString: String): String {
        val parser = JsonParser()
        val json = parser.parse(jsonString)
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }

    fun logInPrettyFormat(tag: String?, string: String) {
        val prettyJson = toPrettyFormat(string)
        Log.v(tag, prettyJson)
    }
}