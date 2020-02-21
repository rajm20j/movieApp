package com.example.themoviesdb.readEpisodeStories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife
import com.example.themoviesdb.MyApp
import com.example.themoviesdb.R
import com.example.themoviesdb.epDesc.EpisodeDescViewModel
import javax.inject.Inject

class ReadEpisodeStories : AppCompatActivity() {

    @Inject
    lateinit var readEpisodeStoriesVMFactory: ReadEpisodeStoriesVMFactory

    lateinit var readEpisodeStoriesVM: ReadEpisodeStoriesVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_episode_stories)
        ButterKnife.bind(this)
        (application as MyApp).myComponent.doInjection(this)
        readEpisodeStoriesVM = ViewModelProvider(this, readEpisodeStoriesVMFactory).get(
            ReadEpisodeStoriesVM::class.java)

    }
}
