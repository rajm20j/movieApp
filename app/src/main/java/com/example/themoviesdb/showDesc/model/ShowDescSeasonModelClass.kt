package com.example.themoviesdb.showDesc.model


import com.google.gson.annotations.SerializedName

data class ShowDescSeasonModelClass(
    @SerializedName("air_date")
    val airDate: String,
    val episodes: List<Episode>,
    @SerializedName("_id")
    val _id: String,
    val id: Int,
    val name: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: Any,
    @SerializedName("season_number")
    val seasonNumber: Int
)