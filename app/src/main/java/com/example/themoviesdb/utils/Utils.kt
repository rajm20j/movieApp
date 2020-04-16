package com.example.themoviesdb.utils

import android.R
import android.content.Context
import android.graphics.BlurMaskFilter
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import com.example.themoviesdb.showDesc.model.Episode
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

object Utils {
    var episodList = listOf<Episode>()
    lateinit var currentShowTitle: String
    lateinit var currentShowSeason: String
    lateinit var currentShowEpisode: String

    lateinit var titleForApi: String
    lateinit var seasonForApi: String
    lateinit var uidForApi: String

    var globalListOfList: MutableList<MutableList<ReadEpisodeGridItemModel>> = mutableListOf()
    var smallListPosition = 1
    var largeListPosition = 0
    val listOfRV = mutableListOf<RecyclerView>()
    var currentInnerRv: RecyclerView? = null
    var currentPapaRV: RecyclerView? =null

    private fun toPrettyFormat(jsonString: String): String {
        val parser = JsonParser()
        val json = parser.parse(jsonString)
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }

    fun logInPrettyFormat(tag: String?, string: String) {
        val prettyJson = toPrettyFormat(string)
        Log.v(tag, prettyJson)
    }

    fun setScrollUpAnimation(
        context: Context?,
        view: View,
        duration: Int
    ) {
        val set = AnimationSet(true)

        val trAnimation: Animation = TranslateAnimation(0f, 0f, 20f, 0f)
        trAnimation.duration = duration.toLong()
        trAnimation.repeatMode =
            Animation.ABSOLUTE // This will make the view translate in the reverse direction
        set.addAnimation(trAnimation)

        val alphaAnim: Animation = AlphaAnimation(0.0f, 1.0f)
        alphaAnim.duration = duration.toLong()
        set.addAnimation(alphaAnim)

        /*val scaleAnim = ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = duration.toLong()
        set.addAnimation(scaleAnim)*/

        set.setInterpolator(context, R.anim.decelerate_interpolator)
        view.startAnimation(set)
    }
}