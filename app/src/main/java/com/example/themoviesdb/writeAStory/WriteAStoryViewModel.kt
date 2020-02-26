package com.example.themoviesdb.writeAStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.themoviesdb.data.Repository
import com.example.themoviesdb.data.model.ApiResponse
import com.example.themoviesdb.writeAStory.model.WriteAStoryModelClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WriteAStoryViewModel(private val repository: Repository) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val postResponseLiveData = MutableLiveData<ApiResponse>()
    internal val listPostResponse: LiveData<ApiResponse>
        get() = postResponseLiveData

    internal fun hitPostFurther(showName: String, seasonNo: String, uid: String, writeAStoryModelClass: WriteAStoryModelClass) {
        disposable.add(repository.executePostFurther(showName, seasonNo, uid, writeAStoryModelClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { postResponseLiveData.value = ApiResponse.loading() }
            .subscribe(
                { result -> postResponseLiveData.value = ApiResponse.success(result) },
                { error -> postResponseLiveData.value = ApiResponse.error(error) }
            )
        )
    }

    override fun onCleared() {
        disposable.clear()
    }
}