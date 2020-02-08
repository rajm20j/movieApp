package com.example.themoviesdb.showDesc

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.R
import com.example.themoviesdb.data.Status
import com.example.themoviesdb.data.model.ApiResponse
import com.example.themoviesdb.extras.Constants
import com.example.themoviesdb.showDesc.model.ListAllSeasonGridAdapter
import com.example.themoviesdb.showDesc.model.SeasonsGridItemModel
import com.example.themoviesdb.showDesc.model.ShowDescSeasonModelClass
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.Error
import javax.inject.Inject

class ShowDesc : AppCompatActivity() {
    val TAG = this.javaClass.simpleName
    lateinit var showDescViewModel: ShowDescViewModel

    @BindView(R.id.tv_title_activity_show_desc)
    lateinit var title: TextView

    @BindView(R.id.tv_overview_activity_show_desc)
    lateinit var overview: TextView

    @BindView(R.id.banner)
    lateinit var banner: ImageView

    @BindView(R.id.rv_season_list_show_desc)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.pb_show_desc)
    lateinit var progressBar: ProgressBar

    private val list = mutableListOf<SeasonsGridItemModel>()
    var seasonsAdapter = ListAllSeasonGridAdapter(this, list)
    var seasonNo = 1

    @Inject
    lateinit var showDescViewModelFactory: ShowDescViewModelFactory

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_desc)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)

        showDescViewModel = ViewModelProvider(this, showDescViewModelFactory).get(ShowDescViewModel::class.java)
        showDescViewModel.listResponse.observe(this, Observer<ApiResponse?>{ this.consumeGetSeasonList(it) })

        val intent =intent
        title.text = intent.getStringExtra("title")
        overview.text = intent.getStringExtra("overview")
        Glide.with(this)
            .load(Constants.IMAGE_BASE_URL + intent.getStringExtra("backdrop"))
            .placeholder(R.drawable.movie_place_holder_landscape)
            .into(banner)

        recyclerView.adapter = seasonsAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        showDescViewModel.hitExecuteGetTvShowSeasons(intent.getIntExtra("tvId", 100), seasonNo)

    }

    private fun consumeGetSeasonList(apiResponse: ApiResponse?) {
        when(apiResponse!!.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                progressBar.visibility = View.GONE
                renderErrorSeasonResponse(apiResponse.error)
            }

            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                renderSuccessSeasonResponse(apiResponse.data)
            }
            else -> Log.e(TAG, "Ye kya hua? :O")
        }
    }

    private fun renderSuccessSeasonResponse(data: JsonElement?) {
        val jsonObject = data!!.asJsonObject
        Log.v("TEST", jsonObject.toString())

        val allData = gson.fromJson(jsonObject.toString(), ShowDescSeasonModelClass::class.java)

        val itemDetails = SeasonsGridItemModel()
        itemDetails.seasonNo = allData.seasonNumber
        itemDetails.airDate = allData.airDate
        itemDetails.episodes = allData.episodes

        list.add(itemDetails)
        recyclerView.adapter?.notifyDataSetChanged()

        this.seasonNo++
        showDescViewModel.hitExecuteGetTvShowSeasons(intent.getIntExtra("tvId", 100), seasonNo)
    }

    private fun renderErrorSeasonResponse(error: Throwable?) {
        Log.v("TEST", error.toString())
    }
}
