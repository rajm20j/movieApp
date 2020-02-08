package com.example.themoviesdb.allShows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.themoviesdb.data.Repository
import com.example.themoviesdb.data.model.ApiResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAllShowsViewModel (private val repository: Repository): ViewModel() {
    private val disposable = CompositeDisposable()
    private val responseLiveData = MutableLiveData<ApiResponse>()

    internal val listResponse: LiveData<ApiResponse>
        get() = responseLiveData

    internal fun hitGetAllTvListApi(pageNo: Int) {
        disposable.add(repository.executeGetTvListApi(pageNo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{responseLiveData.value = ApiResponse.loading()}
            .subscribe(
                {result-> responseLiveData.value = ApiResponse.success(result)},
                {error-> responseLiveData.value = ApiResponse.error(error)}
            )
        )
    }

    override fun onCleared() {
        disposable.clear()
    }
}