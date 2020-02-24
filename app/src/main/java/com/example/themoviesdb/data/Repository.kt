package com.example.themoviesdb.data

import android.util.Log
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.extras.Constants
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import com.google.gson.JsonElement
import io.reactivex.Single

class Repository(private val apiCallInterface: ApiCallInterface) {

    init {
        (MyApp.context as MyApp).myComponent.doInjection(this)
    }

    fun executeGetTvListApi(pageNo: Int): Single<JsonElement> {
        return apiCallInterface.tvList("${Constants.BASE_URL_TO_GET}${Constants.TV_TOP_RATED}?api_key=${Constants.API_KEY}&page=${pageNo}")
    }

    fun executeGetTvShowSeasons(tvId: Int, season: Int): Single<JsonElement> {
        return apiCallInterface.tvShowSeasons("${Constants.BASE_URL_TO_GET}${Constants.TV}${tvId}/${Constants.SEASON}${season}?api_key=${Constants.API_KEY}")
    }

    fun executeReadFurther(showName: String, seasonNo: String, uid: String): Single<JsonElement> {
        Log.v("TEST", "${showName}/${seasonNo}/${uid}")
        return apiCallInterface.readFurther(showName, seasonNo, uid)
    }

    fun executePostFurther(showName: String, seasonNo: String, parentuid: String, readEpisodeGridItemModel: ReadEpisodeGridItemModel): Single<JsonElement> {
        return apiCallInterface.postFurther(showName, seasonNo, parentuid, readEpisodeGridItemModel)
    }
}