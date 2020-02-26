package com.example.themoviesdb.readEpisodeStories

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModelClass
import com.example.themoviesdb.readEpisodeStories.model.ReadGridItemAdapter
import com.example.themoviesdb.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonElement
import javax.inject.Inject

class ReadEpisodeStories : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    @BindView(R.id.pb_read_episode_stories)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.forRecyclerView)
    lateinit var linearLayout: LinearLayout

    @BindView(R.id.read_series_name)
    lateinit var seriesNameTv: TextView

    lateinit var globalRecyclerView: RecyclerView
    lateinit var globalList: MutableList<ReadEpisodeGridItemModel>

    var isScrolling = false

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var readEpisodeStoriesVMFactory: ReadEpisodeStoriesVMFactory

    private lateinit var readEpisodeStoriesVM: ReadEpisodeStoriesVM
    private lateinit var titleForApi: String
    private lateinit var seasonForApi: String
    private lateinit var episodeForApi: String

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

        seriesNameTv.text = Utils.currentShowTitle

        titleForApi = Utils.currentShowTitle.replace("\\s".toRegex(), "").trim()
        seasonForApi = Utils.currentShowSeason.trim()
        episodeForApi = Utils.currentShowEpisode.trim()
        Log.v("TEST", "$titleForApi/$seasonForApi/${titleForApi + seasonForApi + episodeForApi}")
        readEpisodeStoriesVM.hitReadFurther(titleForApi, seasonForApi, titleForApi + seasonForApi + episodeForApi)
    }

    private fun consumeGetFurtherResponse(apiResponse: ApiResponse?) {
        Log.v("TEST", "consumeGetFurtherResponse")
        when (apiResponse!!.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                progressBar.visibility = View.GONE
                renderErrorGetFurtherResponse(apiResponse.error)
            }

            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                renderSuccessGetFurtherResponse(apiResponse.data)
            }
            else -> Log.e(TAG, "Ye kya hua? :O")
        }
    }

    private fun renderSuccessGetFurtherResponse(data: JsonElement?) {
        val jsonArray = data!!.asJsonArray
        Log.v("TEST", jsonArray.toString())

        val list = mutableListOf<ReadEpisodeGridItemModel>()

        val plusElement = ReadEpisodeGridItemModel()
        list.add(plusElement)

        val episodesAdapter = ReadGridItemAdapter(readEpisodeStoriesVM, this, list)

        val allData =
            gson.fromJson(jsonArray.toString(), Array<ReadEpisodeGridItemModelClass>::class.java)
//        Utils.logInPrettyFormat("TEST", jsonArray.toString())
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
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout.addView(recyclerView)
        if (list.size > 1) {
            recyclerView.scrollToPosition(1)
        }
        this.globalRecyclerView = recyclerView
        loadMore()
        this.globalList = list
    }

    private fun loadMore() {
        globalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                if (isScrolling) {
                    if (dx > 15) {
                        recyclerView.smoothScrollToPosition(manager.findLastVisibleItemPosition())
                    } else if (dx < 15) {
                            recyclerView.smoothScrollToPosition(manager.findFirstVisibleItemPosition())
                    }
                    isScrolling = false
                }
            }
        })
    }

    private fun renderErrorGetFurtherResponse(error: Throwable?) {
        Toast.makeText(this, "Could not fetch data", Toast.LENGTH_LONG).show()
        Log.v("TEST", "Could not fetch data: ${error.toString()}")
    }
}
