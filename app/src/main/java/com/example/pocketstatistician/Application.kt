package com.example.pocketstatistician

import android.app.Application
import android.content.Context
import com.example.pocketstatistician.convenience.log
import io.realm.Realm
import io.realm.RealmConfiguration

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        checkFirstRun()
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
            Realm.getDefaultInstance().executeTransaction { realm ->
                realm.copyToRealm(Type(getString(R.string.integer_type), "int"))
                realm.copyToRealm(Type(getString(R.string.double_type), "double"))
                realm.copyToRealm(Type(getString(R.string.date_type), "date"))
            }
            pref.edit().putInt(PREF_VERSION_CODE, currentVersionCode).apply()
        }

        //App is upgraded
        if (savedPrefVersion < currentVersionCode) {

        }

    }

    private fun deleteRealm() {
        val realmDefaultConfig = Realm.getDefaultConfiguration()
        if (realmDefaultConfig != null)
            Realm.deleteRealm(realmDefaultConfig)
    }

}