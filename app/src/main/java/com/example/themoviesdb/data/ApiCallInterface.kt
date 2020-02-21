package com.example.themoviesdb.data

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiCallInterface {

    @GET
    fun tvList(@Url url: String): Single<JsonElement>

    @GET
    fun tvShowSeasons(@Url url: String): Single<JsonElement>
}