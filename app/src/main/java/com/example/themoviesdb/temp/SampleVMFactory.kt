package com.example.themoviesdb.temp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.themoviesdb.allShows.ListAllShowsViewModel
import com.example.themoviesdb.data.Repository
import javax.inject.Inject

class SampleVMFactory @Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SampleViewModel::class.java)) {
            return ListAllShowsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}