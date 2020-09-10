package com.example.themoviesdb.temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.themoviesdb.R
import com.example.themoviesdb.data.model.ApiResponse
import javax.inject.Inject

class SampleActivity : AppCompatActivity() {

    lateinit var sampleViewModel: SampleViewModel

    @Inject
    lateinit var sampleVMFactory: SampleVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        sampleViewModel = ViewModelProvider(this, sampleVMFactory).get(SampleViewModel::class.java)

        sampleViewModel.liveDataResponse.observe(this, Observer<ApiResponse> { this.consumeResponse(it) })

        sampleViewModel.hitGetResponse()
    }

    private fun consumeResponse(apiResponse: ApiResponse?) {
    }
}