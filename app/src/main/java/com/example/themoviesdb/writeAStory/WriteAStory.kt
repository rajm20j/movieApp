package com.example.themoviesdb.writeAStory

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.R
import com.example.themoviesdb.data.model.ApiResponse
import com.example.themoviesdb.data.model.Status
import com.example.themoviesdb.readEpisodeStories.ReadEpisodeStories
import com.example.themoviesdb.utils.Utils
import com.example.themoviesdb.writeAStory.model.WriteAStoryModelClass
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonElement
import javax.inject.Inject

class WriteAStory : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    @BindView(R.id.write_a_story_series_name)
    lateinit var seriesName: TextView

    @BindView(R.id.write_a_story_title)
    lateinit var seriesTitle: EditText

    @BindView(R.id.write_a_story_overview)
    lateinit var seriesOverview: EditText

    @BindView(R.id.write_a_story_button)
    lateinit var submitBtn: MaterialButton

    @BindView(R.id.pb_write_a_story)
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var writeAStoryViewModelFactory: WriteAStoryViewModelFactory

    private lateinit var writeAStoryViewModel: WriteAStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_astory)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        writeAStoryViewModel = ViewModelProvider(this, writeAStoryViewModelFactory).get(
            WriteAStoryViewModel::class.java
        )

        writeAStoryViewModel.listPostResponse.observe(
            this,
            Observer<ApiResponse?> { this.consumePostFurtherResponse(it) })
        seriesName.text = Utils.currentShowTitle

        submitBtn.setOnClickListener{
            val title = Utils.currentShowTitle.replace("\\s".toRegex(), "").trim()
            val season = Utils.currentShowSeason.trim()
            val episode = Utils.currentShowEpisode.trim()
            if(!TextUtils.isEmpty(seriesTitle.text) && !TextUtils.isEmpty(seriesOverview.text))
            {
                val writeAStoryModelClass = WriteAStoryModelClass()
                writeAStoryModelClass.title = seriesTitle.text.toString()
                writeAStoryModelClass.descr = seriesOverview.text.toString()
                writeAStoryViewModel.hitPostFurther(title, season, title+season+episode, writeAStoryModelClass)
            }
        }
    }

    private fun consumePostFurtherResponse(apiResponse: ApiResponse?) {
        Log.v("TEST", "consumeGetFurtherResponse")
        when (apiResponse!!.status) {
            Status.LOADING -> {
                submitBtn.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }

            Status.ERROR -> {
                submitBtn.visibility = View.VISIBLE
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

    private fun renderErrorPostFurtherResponse(error: Throwable?) {
        Toast.makeText(this, "Could not fetch data", Toast.LENGTH_LONG).show()
        Log.v("TEST", "Could not fetch data: ${error.toString()}")
        startActivity(Intent(this, ReadEpisodeStories::class.java))
        finish()
    }

    private fun renderSuccessPostFurtherResponse(data: JsonElement?) {
        Toast.makeText(this, "Ho gya post", Toast.LENGTH_LONG).show()
        val jsonArray = data!!.asJsonArray

        Utils.logInPrettyFormat("TEST", jsonArray.toString())
        startActivity(Intent(this, ReadEpisodeStories::class.java))
        finish()
    }
}
