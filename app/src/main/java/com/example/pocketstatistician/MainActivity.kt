package com.example.pocketstatistician

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.realm.RealmList
import io.realm.RealmObject

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity_layout)

        val newStatisticsButton = findViewById<Button>(R.id.addStatisticsButton)

        newStatisticsButton.setOnClickListener {
            val intent = Intent(this, NewStatisticsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

open class Variables(
    var name: String = "",
    var type: Int = 0,
    var value: Double = .0,
    var variants: RealmList<String> = RealmList()
): RealmObject()

open class Statistic(
    var name: String = "",
    var variablesCount: Int = 0,
    var variables: RealmList<Variables> = RealmList()
): RealmObject()
