package com.example.themoviesdb.allShows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.themoviesdb.data.Repository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ListAllShowsViewModelFactory @Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListAllShowsViewModel::class.java)) {
            return ListAllShowsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
