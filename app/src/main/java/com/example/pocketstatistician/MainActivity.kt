package com.example.pocketstatistician

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity_layout)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun addStatisticsButtonOnClick(view: View) {
        val intent = Intent(this, NewStatisticsActivity::class.java)
        startActivity(intent)
    }

    fun addVariableButtonOnClick(view: View) {
        val intent = Intent(this, NewVariableActivity::class.java)
        startActivity(intent)
        finish()
    }

}
