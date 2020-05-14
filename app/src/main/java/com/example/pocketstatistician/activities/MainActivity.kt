package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.R

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity_layout)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, NewStatisticsFragment()).commit()
    }

    fun addStatisticsButtonOnClick(view: View) {
    }

    fun addVariableButtonOnClick(view: View) {
    }

}
