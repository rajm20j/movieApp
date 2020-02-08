package com.example.themoviesdb.showDesc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.themoviesdb.data.Repository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ShowDescViewModelFactory @Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowDescViewModel::class.java)) {
            return ShowDescViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}