package com.example.pocketstatistician

import android.app.Application
import android.content.Context
import com.example.pocketstatistician.convenience.log
import io.realm.*
import io.realm.exceptions.RealmMigrationNeededException

class Application: Application() {

    lateinit var types: RealmResults<Type>
    lateinit var statistics: RealmResults<Statistic>

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder().name("default.realm").schemaVersion(1).migration(ApplicationRealmMigration()).build()
        Realm.setDefaultConfiguration(config)
        types = loadTypes()
        statistics = loadStatistics()

    }

    private fun loadTypes(): RealmResults<Type> {
        var variables: RealmResults<Type>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Type::class.java).findAll()
            variables = varsFromRealm
        }
        return variables!!
    }

    private fun loadStatistics(): RealmResults<Statistic> {
        var statistics: RealmResults<Statistic>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val dataFromRealm = realm.where(Statistic::class.java).findAll()
            statistics = dataFromRealm
        }
        return statistics!!
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

    class ApplicationRealmMigration: RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            val schema = realm.schema

            if (oldVersion == 0L) {
                schema.get("Statistic")!!.addRealmListField("questions", String::class.java)
            }
        }

    }

}