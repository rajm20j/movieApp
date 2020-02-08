package com.example.themoviesdb

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.themoviesdb.di.AppComponent
import com.example.themoviesdb.di.AppModule
import com.example.themoviesdb.di.DaggerAppComponent
import com.example.themoviesdb.di.UtilsModule

class MyApp : Application() {
    lateinit var myComponent: AppComponent

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        myComponent = createMyComponent()
        context = this.applicationContext
    }

    private fun createMyComponent(): AppComponent{
        return DaggerAppComponent
            .builder()
            .appModule(AppModule(this.applicationContext))
            .utilsModule(UtilsModule())
            .build()
    }
}
