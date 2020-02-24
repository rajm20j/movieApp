package com.example.themoviesdb.readEpisodeStories.model

import com.google.gson.annotations.SerializedName

data class ReadEpisodeGridItemModelClass(
    val descr: String,
    val likes: Int,
    val parentUid: String,
    val title: String,
    val uid: String
)