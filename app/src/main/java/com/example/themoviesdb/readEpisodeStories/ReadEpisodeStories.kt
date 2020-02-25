package com.example.themoviesdb.readEpisodeStories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var readEpisodeStoriesVMFactory: ReadEpisodeStoriesVMFactory

    private lateinit var readEpisodeStoriesVM: ReadEpisodeStoriesVM
    private val list = mutableListOf<ReadEpisodeGridItemModel>()
    var episodesAdapter = ReadGridItemAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_episode_stories)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        readEpisodeStoriesVM = ViewModelProvider(this, readEpisodeStoriesVMFactory).get(
            ReadEpisodeStoriesVM::class.java)

        readEpisodeStoriesVM.listGetResponse.observe(this, Observer<ApiResponse?>{ this.consumeGetFurtherResponse(it) })
        readEpisodeStoriesVM.listPostResponse.observe(this, Observer<ApiResponse?>{ this.consumePostFurtherResponse(it) })

        seriesNameTv.text = Utils.currentShowTitle

        val title = Utils.currentShowTitle.replace("\\s".toRegex(),"").trim()
        val season = Utils.currentShowSeason.trim()
        val episode = Utils.currentShowEpisode.trim()
        Log.v("TEST", "$title/$season/${title+season+episode}")
        readEpisodeStoriesVM.hitReadFurther(title, season, title+season+episode)
    }

    private fun consumePostFurtherResponse(apiResponse: ApiResponse?) {
        when(apiResponse!!.status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                progressBar.visibility = View.GONE
                renderErrorPostFurtherResponse(apiResponse.error)
            }

            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                renderSuccessPostFurtherResponse(apiResponse.data)
            }
            else -> Log.e(TAG, "Ye kya hua? :O")
        }
    }

    private fun renderSuccessPostFurtherResponse(data: JsonElement?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun renderErrorPostFurtherResponse(error: Throwable?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun consumeGetFurtherResponse(apiResponse: ApiResponse?) {
        Log.v("TEST", "consumeGetFurtherResponse")
        when(apiResponse!!.status) {
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
        Toast.makeText(this, "Success mein hain", Toast.LENGTH_SHORT).show()
        val jsonArray = data!!.asJsonArray
        Log.v("TEST", jsonArray.toString())

//        val allData = gson.fromJson(jsonArray.toString(), mutableListOf<ReadEpisodeGridItemModelClass>()::class.java)
        val allData = gson.fromJson(jsonArray.toString(), Array<ReadEpisodeGridItemModelClass>::class.java)
        Log.v("TEST", allData.toString())

        list.add(ReadEpisodeGridItemModel())
        for(item in allData)
        {
            val tempItem = ReadEpisodeGridItemModel()
            tempItem.uid = item.uid
            tempItem.title = item.title
            tempItem.parentUid = item.parentUid
            tempItem.likes = item.likes
            tempItem.descr = item.descr
            list.add(tempItem)
        }

        val recyclerView = RecyclerView(this)
        recyclerView.adapter = episodesAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout.addView(recyclerView)
    }

    private fun renderErrorGetFurtherResponse(error: Throwable?) {
        Toast.makeText(this, "Could not fetch data", Toast.LENGTH_LONG).show()
        Log.v("TEST", "Could not fetch data: ${error.toString()}")
    }
}
