package com.example.themoviesdb.epDesc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.R
import com.example.themoviesdb.epDesc.model.EpisodeGridItemAdapter
import com.example.themoviesdb.epDesc.model.EpisodeGridItemModel
import com.example.themoviesdb.utils.Utils
import com.google.gson.Gson
import javax.inject.Inject

class EpisodeDesc : AppCompatActivity() {

    @BindView(R.id.tv_seasons_episode_desc)
    lateinit var textViewSeasons: TextView

    @BindView(R.id.rv_episode_desc)
    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var episodeDescViewModelFactory: EpisodeDescViewModelFactory

    var episodeDetails = mutableListOf<EpisodeGridItemModel>()
    var episodeItemAdapter = EpisodeGridItemAdapter(this, episodeDetails)
    lateinit var episodeDescViewModel: EpisodeDescViewModel

    @Inject
    lateinit var gson: Gson

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_desc)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        episodeDescViewModel = ViewModelProvider(this, episodeDescViewModelFactory).get(EpisodeDescViewModel::class.java)

        recyclerView.adapter = episodeItemAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val intent = intent
        val seasonNumber = intent.getStringExtra("seasonNo")
//        textViewSeasons.text = "Season ${Utils.episodList[0].seasonNumber}"
        textViewSeasons.text = "Season ${Utils.currentShowSeason}"

        initializeRV()
    }

    private fun initializeRV() {
        for (items in Utils.episodList)
        {
            val itemDetails = EpisodeGridItemModel()
            itemDetails.airDate = items.airDate
            itemDetails.episodeNo = items.episodeNumber
            itemDetails.overview = items.overview
            itemDetails.episodeName = items.name
            episodeDetails.add(itemDetails)
            recyclerView.adapter?.notifyDataSetChanged()

        }
    }
}
