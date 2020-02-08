package com.example.themoviesdb.allShows.model


import com.google.gson.annotations.SerializedName

data class AllShowDataModel(
    val page: Int,
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)