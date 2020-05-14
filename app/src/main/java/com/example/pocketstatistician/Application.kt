package com.example.pocketstatistician

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class Application: Application() {

    override fun onCreate() {
        Realm.init(this)
    }

    private fun checkFirstRun() {
        val PREF_FILE_NAME = "my_file"
        val PREF_VERSION_CODE = "version"
        val DOES_NOT_EXIST = -1

        val currentVersionCode = BuildConfig.VERSION_CODE

        val pref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val savedPrefVersion = pref.getInt(PREF_VERSION_CODE, DOES_NOT_EXIST)

        //Current and last ran version are equal, normal run
        if (savedPrefVersion == currentVersionCode) {
            return
        }

        //App is running for the first time
        if (savedPrefVersion == DOES_NOT_EXIST) {

        }

        //App is upgraded
        if (savedPrefVersion < currentVersionCode) {
            val realmDefaultConfig = Realm.getDefaultConfiguration()
            if (realmDefaultConfig != null)
                Realm.deleteRealm(realmDefaultConfig)
            super.onCreate()
        }

    }

}