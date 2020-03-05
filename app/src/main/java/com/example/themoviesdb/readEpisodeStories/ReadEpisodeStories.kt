package com.example.themoviesdb.readEpisodeStories

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
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
    lateinit var papaRv: RecyclerView

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

    var isScrolling = false
    private var initialHit = true
    private var pageNo = 1

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

        Utils.largeListPosition = 0
        Utils.smallListPosition = 1
        Utils.listOfRV.clear()
        Utils.currentPapaRV = papaRv
        papaRv.adapter = ListOfRVModelAdapter(
            this,
            frontNav,
            backNav,
            pageNoTv,
            readEpisodeStoriesVM
        )
        papaRv.layoutManager = LinearLayoutManager(this)

        seriesNameTv.text = Utils.currentShowTitle
        pageNoTv.text = pageNo.toString()

        Utils.titleForApi = Utils.currentShowTitle.replace("\\s".toRegex(), "").trim()
        Utils.seasonForApi = Utils.currentShowSeason.trim()
        Utils.uidForApi = (Utils.titleForApi + Utils.seasonForApi + Utils.currentShowEpisode.trim())
        Log.v("TEST", "${Utils.titleForApi}/${Utils.seasonForApi}/${Utils.uidForApi}")
        readEpisodeStoriesVM.hitReadFurther(
            Utils.titleForApi,
            Utils.seasonForApi,
            Utils.uidForApi
        )
        loadMore()
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
//        Utils.logInPrettyFormat("TEST", jsonArray.toString())


        val list = mutableListOf<ReadEpisodeGridItemModel>()

        val plusElement = ReadEpisodeGridItemModel()
        list.add(plusElement)

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
        val episodesAdapter = ReadGridItemAdapter(this, list)
        recyclerView.adapter = episodesAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        Utils.listOfRV.add(recyclerView)
        papaRv.adapter?.notifyItemInserted(Utils.largeListPosition+1)
        Utils.globalListOfList.add(list)

        if(initialHit)
        {
            if (Utils.globalListOfList[0].size > 1){
                readEpisodeStoriesVM.hitReadFurther(
                    Utils.titleForApi,
                    Utils.seasonForApi,
                    Utils.globalListOfList[0][1].uid!!
                )
            }
            initialHit = false
        }
    }


    private fun loadMore() {
        Utils.currentPapaRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                if (isScrolling) {
                    if (dy > 15) {
                        Utils.largeListPosition = manager.findLastVisibleItemPosition()
                        recyclerView.smoothScrollToPosition(Utils.largeListPosition)

                        if(Utils.globalListOfList[Utils.largeListPosition].size > 1 && Utils.largeListPosition == papaRv.layoutManager!!.itemCount-1)
                            readEpisodeStoriesVM.hitReadFurther(Utils.titleForApi, Utils.seasonForApi, Utils.globalListOfList[Utils.largeListPosition][1].uid!!)

                    } else if (dy < 15) {
                        Utils.largeListPosition = manager.findFirstVisibleItemPosition()
                        recyclerView.smoothScrollToPosition(Utils.largeListPosition)
                        Log.v("TEST", "Utils.listOfRV.size: ${Utils.listOfRV.size.toString()} & manager.itemCount: ${manager.itemCount}")
                    }
                    Log.v("TEST", "LargeList: ${Utils.largeListPosition}, SmallList: ${Utils.smallListPosition}")
                    isScrolling = false
                }
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Utils.largeListPosition = 0
            Utils.smallListPosition = 1
            this.finish()
            Toast.makeText(this, "Back pressed", Toast.LENGTH_SHORT).show()
            return true;
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun renderErrorGetFurtherResponse(error: Throwable?) {
        Toast.makeText(this, "Could not fetch data", Toast.LENGTH_LONG).show()
        Log.v("TEST", "Could not fetch data: ${error.toString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.smallListPosition = 1
        Utils.largeListPosition = 0
    }
}
