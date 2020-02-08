package com.example.themoviesdb.data

import com.example.themoviesdb.extras.Constants
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCallInterface {

    @GET(Constants.TV_TOP_RATED)
    fun tvList(@Query("api_key")apiKey: String, @Query("page")pageNo: Int): Single<JsonElement>

    @GET("${Constants.TV}{tv_id}/${Constants.SEASON}{season}")
    fun tvShowSeasons(@Path("tv_id") tvId: Int, @Path("season") season: Int, @Query("api_key") apiKey: String): Single<JsonElement>
}