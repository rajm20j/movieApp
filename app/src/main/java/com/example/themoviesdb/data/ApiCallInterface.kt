package com.example.themoviesdb.data

import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import com.example.themoviesdb.writeAStory.WriteAStoryViewModel
import com.example.themoviesdb.writeAStory.model.WriteAStoryModelClass
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.*

interface ApiCallInterface {

    @GET
    fun tvList(@Url url: String): Single<JsonElement>

    @GET
    fun tvShowSeasons(@Url url: String): Single<JsonElement>

    @GET("{showName}/{seasonNo}/{uid}")
    fun readFurther(@Path("showName") showName: String, @Path("seasonNo") seasonNo: String, @Path("uid") uid: String): Single<JsonElement>

    @Headers("Content-Type: application/json")
    @POST("{showName}/{seasonNo}/{parentuid}")
    fun postFurther(@Path("showName") showName: String, @Path("seasonNo") seasonNo: String, @Path("parentuid") uid: String, @Body writeAStoryModelClass: WriteAStoryModelClass): Single<JsonElement>
}