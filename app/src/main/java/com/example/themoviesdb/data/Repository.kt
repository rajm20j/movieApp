package com.example.themoviesdb.data

import android.util.Log
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.data.ApiCallInterface
import com.example.themoviesdb.extras.Constants
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Single

class Repository(private val apiCallInterface: ApiCallInterface) {

    init {
        (MyApp.context as MyApp).myComponent.doInjection(this)
    }

    fun executeGetTvListApi(pageNo: Int): Single<JsonElement> {
        return apiCallInterface.tvList(Constants.API_KEY, pageNo)
    }

    fun executeGetTvShowSeasons(tvId: Int, season: Int): Single<JsonElement> {
        return apiCallInterface.tvShowSeasons(tvId, season, Constants.API_KEY)
    }
}