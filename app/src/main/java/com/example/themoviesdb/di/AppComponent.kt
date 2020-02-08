package com.example.themoviesdb.di

import com.example.themoviesdb.allShows.ListAllShows
import com.example.themoviesdb.data.Repository
import com.example.themoviesdb.epDesc.EpisodeDesc
import com.example.themoviesdb.showDesc.ShowDesc
import dagger.Component
import javax.inject.Singleton


@Component(modules = [AppModule::class, UtilsModule::class])
@Singleton
interface AppComponent {
    fun doInjection(listAllShows: ListAllShows)

    fun doInjection(repository: Repository)

    fun doInjection(showDesc: ShowDesc)

    fun doInjection(episodeDesc: EpisodeDesc)
}