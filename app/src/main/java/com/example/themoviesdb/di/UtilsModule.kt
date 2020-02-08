package com.example.themoviesdb.di

import androidx.lifecycle.ViewModelProvider
import com.example.themoviesdb.allShows.ListAllShowsViewModelFactory
import com.example.themoviesdb.extras.Constants
import com.example.themoviesdb.data.ApiCallInterface
import com.example.themoviesdb.data.Repository
import com.example.themoviesdb.epDesc.EpisodeDescViewModelFactory
import com.example.themoviesdb.showDesc.ShowDescViewModelFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class UtilsModule {
    @Provides
    @Singleton
    internal fun getRequestHeader(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder().build()
            chain.proceed(request)
        }.readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS)
        return httpClient.build()
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val builder = GsonBuilder()
        return builder.setLenient().create()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    internal fun getApiCallInterface(retrofit: Retrofit): ApiCallInterface {
        return retrofit.create(ApiCallInterface::class.java)
    }

    @Provides
    @Singleton
    internal fun getRepository(apiCallInterface: ApiCallInterface): Repository {
        return Repository(apiCallInterface)
    }

    @Provides
    @Singleton
    internal fun getListAllShowsViewModelFactory(repository: Repository): ViewModelProvider.Factory {
        return ListAllShowsViewModelFactory(
            repository
        )
    }

    @Provides
    @Singleton
    internal fun getShowDescViewModelFactory(repository: Repository): ViewModelProvider.Factory {
        return ShowDescViewModelFactory(
            repository
        )
    }

    @Provides
    @Singleton
    internal fun getEpisodeDescViewModelFactory(repository: Repository): ViewModelProvider.Factory {
        return EpisodeDescViewModelFactory(
            repository
        )
    }
}