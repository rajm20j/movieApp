package com.example.themoviesdb.readEpisodeStories

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.R
import com.example.themoviesdb.data.model.ApiResponse
import com.example.themoviesdb.data.model.Status
import com.example.themoviesdb.readEpisodeStories.model.ListOfRVModelAdapter
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModelClass
import com.example.themoviesdb.readEpisodeStories.model.ReadGridItemAdapter
import com.example.themoviesdb.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonElement
import javax.inject.Inject

class ReadEpisodeStories : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    @BindView(R.id.pb_read_episode_stories)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.forRecyclerView)
    lateinit var linearLayoutForRV: LinearLayout

    @BindView(R.id.read_series_name)
    lateinit var seriesNameTv: TextView

    @BindView(R.id.read_episode_back_nav)
    lateinit var backNav: MaterialButton

    @BindView(R.id.read_episode_front_nav)
    lateinit var frontNav: MaterialButton

    @BindView(R.id.read_episode_page_no)
    lateinit var pageNoTv: TextView

    @BindView(R.id.navigationBtn)
    lateinit var navigationLinearLayout: LinearLayout

    lateinit var papaRv: RecyclerView
    lateinit var papaManager: LinearLayoutManager
    val listOfRV = mutableListOf<RecyclerView>()

    var pageNo = 1

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var readEpisodeStoriesVMFactory: ReadEpisodeStoriesVMFactory

    private lateinit var readEpisodeStoriesVM: ReadEpisodeStoriesVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_episode_stories)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        readEpisodeStoriesVM = ViewModelProvider(this, readEpisodeStoriesVMFactory).get(
            ReadEpisodeStoriesVM::class.java
        )

        readEpisodeStoriesVM.listGetResponse.observe(
            this,
            Observer<ApiResponse?> { this.consumeGetFurtherResponse(it) })

        papaRv = RecyclerView(this)
        linearLayoutForRV.addView(papaRv)
        papaRv.adapter = ListOfRVModelAdapter(this, listOfRV, frontNav, backNav, pageNoTv)
        papaRv.layoutManager = GridLayoutManager(this, 1)
        papaManager = (papaRv.layoutManager as LinearLayoutManager)

        seriesNameTv.text = Utils.currentShowTitle
        pageNoTv.text = pageNo.toString()

        Utils.titleForApi = Utils.currentShowTitle.replace("\\s".toRegex(), "").trim()
        Utils.seasonForApi = Utils.currentShowSeason.trim()
        Utils.uidForApi = (Utils.titleForApi+Utils.seasonForApi+Utils.currentShowEpisode.trim())
        Log.v("TEST", "${Utils.titleForApi}/${Utils.seasonForApi}/${Utils.uidForApi}")
        readEpisodeStoriesVM.hitReadFurther(
            Utils.titleForApi,
            Utils.seasonForApi,
            Utils.uidForApi
        )
    }

    private fun consumeGetFurtherResponse(apiResponse: ApiResponse?) {
        when (apiResponse!!.status) {
            Status.LOADING -> {
                navigationLinearLayout.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                progressBar.visibility = View.GONE
                renderErrorGetFurtherResponse(apiResponse.error)
            }

            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                navigationLinearLayout.visibility = View.VISIBLE
                renderSuccessGetFurtherResponse(apiResponse.data)
            }
            else -> Log.e(TAG, "Ye kya hua? :O")
        }
    }

    private fun renderSuccessGetFurtherResponse(data: JsonElement?) {
        val jsonArray = data!!.asJsonArray
        Utils.logInPrettyFormat("TEST", jsonArray.toString())


        val list = mutableListOf<ReadEpisodeGridItemModel>()

        val plusElement = ReadEpisodeGridItemModel()
        list.add(plusElement)

        val episodesAdapter = ReadGridItemAdapter(this, list)

        val allData =
            gson.fromJson(jsonArray.toString(), Array<ReadEpisodeGridItemModelClass>::class.java)

        val recyclerView = RecyclerView(this)
        for (item in allData) {
            val tempItem = ReadEpisodeGridItemModel()
            tempItem.uid = item.uid
            tempItem.title = item.title
            tempItem.parentUid = item.parentUid
            tempItem.likes = item.likes
            tempItem.descr = item.descr
            list.add(tempItem)
        }
        recyclerView.adapter = episodesAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        listOfRV.add(recyclerView)
        papaRv.adapter?.notifyDataSetChanged()

        if (list.size > 1) {
            recyclerView.scrollToPosition(1)
        }
        else {
            pageNo = 0
            pageNoTv.text = "0"
            navigationLinearLayout.visibility = View.INVISIBLE
        }
    }

    private fun renderErrorGetFurtherResponse(error: Throwable?) {
        Toast.makeText(this, "Could not fetch data", Toast.LENGTH_LONG).show()
        Log.v("TEST", "Could not fetch data: ${error.toString()}")
    }
}
