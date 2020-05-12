package com.example.pocketstatistician

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity_layout)
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
