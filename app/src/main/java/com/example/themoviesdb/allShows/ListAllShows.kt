package com.example.themoviesdb.allShows

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
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
import com.example.themoviesdb.allShows.model.AllShowDataModel
import com.example.themoviesdb.allShows.model.GridItemModel
import com.example.themoviesdb.allShows.model.ListAllShowsGridAdapter
import com.example.themoviesdb.data.model.Status
import com.example.themoviesdb.data.model.ApiResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import javax.inject.Inject

class ListAllShows : AppCompatActivity() {

    val TAG = this.javaClass.simpleName
    lateinit var listAllShowsViewModel: ListAllShowsViewModel
    var isScrolling = false
    var currentItem: Int = 0
    var totalItem: Int = 0
    var scrolledOutItem: Int = 0
    private var pageNo = 1
    private val list= mutableListOf<GridItemModel>()
    private var showListAdapter = ListAllShowsGridAdapter(this@ListAllShows, list)
    lateinit var manager: LinearLayoutManager

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.pb_list_all_shows)
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var listAllShowsViewModelFactory: ListAllShowsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_all_shows)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        recyclerView.adapter = showListAdapter

        if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        else
            recyclerView.layoutManager = GridLayoutManager(this, 4)

        listAllShowsViewModel =ViewModelProvider(this, listAllShowsViewModelFactory).get(ListAllShowsViewModel::class.java)
        listAllShowsViewModel.listResponse.observe(this, Observer<ApiResponse> { this.consumeTvList(it) })
        listAllShowsViewModel.hitGetAllTvListApi(pageNo)
        loadMore()
    }

    private fun loadMore() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                manager = recyclerView.layoutManager as LinearLayoutManager
                currentItem = manager.childCount
                totalItem = manager.itemCount
                scrolledOutItem = manager.findFirstVisibleItemPosition()

                if(isScrolling && currentItem+scrolledOutItem >= totalItem-2)
                {
                    isScrolling = false
                    pageNo++
                    listAllShowsViewModel.hitGetAllTvListApi(pageNo)
                }
            }
        })
    }


    private fun consumeTvList(apiResponse: ApiResponse) {
        when(apiResponse.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                progressBar.visibility = View.GONE
                renderErrorTvListResponse(apiResponse.error)
            }

            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                renderSuccessTvListResponse(apiResponse.data)
            }
            else -> Log.e(TAG, "Ye kya hua? :O")
        }
    }

    private fun renderSuccessTvListResponse(data: JsonElement?) {
        val jsonObject = data!!.asJsonObject
        Log.d(TAG, jsonObject.toString())

        val allData = gson.fromJson(jsonObject.toString(), AllShowDataModel::class.java)

        for(item in allData.results)
        {
            val grdItem = GridItemModel()
            grdItem.posterPath = item.posterPath
            grdItem.title = item.name
            grdItem.backdropPath = item.backdropPath
            grdItem.overview = item.overview
            grdItem.tvId = item.id
            list.add(grdItem)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun renderErrorTvListResponse(error: Throwable?) {
        Toast.makeText(this, "End of list", Toast.LENGTH_LONG).show()
    }
}
