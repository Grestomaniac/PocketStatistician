package com.example.pocketstatistician

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class Application: Application() {

    override fun onCreate() {
        Realm.init(this)
        /*val realmDefaultConfig = Realm.getDefaultConfiguration()
        if (realmDefaultConfig != null)
            Realm.deleteRealm(realmDefaultConfig)*/
        super.onCreate()
    }

}