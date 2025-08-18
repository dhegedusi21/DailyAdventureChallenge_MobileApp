package com.example.dailyadventurechallenge

import android.app.Application
import com.example.dailyadventurechallenge.data.api.RetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(applicationContext)
    }
}