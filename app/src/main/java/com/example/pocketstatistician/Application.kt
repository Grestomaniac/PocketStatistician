package com.example.pocketstatistician

import android.app.Application
import io.realm.Realm

class Application: Application() {

    override fun onCreate() {
        Realm.init(this)
        super.onCreate()
    }

}