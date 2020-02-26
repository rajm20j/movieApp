package com.example.themoviesdb.readEpisodeStories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.themoviesdb.data.Repository
import com.example.themoviesdb.data.model.ApiResponse
import com.example.themoviesdb.readEpisodeStories.model.ReadEpisodeGridItemModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ReadEpisodeStoriesVM(private val repository: Repository) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val getResponseLiveData = MutableLiveData<ApiResponse>()

    internal val listGetResponse: LiveData<ApiResponse>
        get() = getResponseLiveData

    internal fun hitReadFurther(showName: String, seasonNo: String, uid: String) {
        Log.v("TEST","hitReadFurther")
        disposable.add(repository.executeReadFurther(showName, seasonNo, uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { getResponseLiveData.value = ApiResponse.loading() }
            .subscribe(
                { result -> getResponseLiveData.value = ApiResponse.success(result) },
                { error -> getResponseLiveData.value = ApiResponse.error(error) }
            )
        )
    }

    override fun onCleared() {
        disposable.clear()
    }
}