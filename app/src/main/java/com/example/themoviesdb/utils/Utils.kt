package com.example.themoviesdb.utils

import com.example.themoviesdb.showDesc.model.Episode

object Utils {
    var episodList = listOf<Episode>()
    lateinit var currentShowTitle: String
    lateinit var currentShowSeason: String
    lateinit var currentShowEpisode: String
}