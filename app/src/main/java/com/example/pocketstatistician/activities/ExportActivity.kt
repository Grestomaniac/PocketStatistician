package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.convenience.YouChooseDialog
import io.realm.Realm

class ExportActivity: AppCompatActivity() {

    lateinit var statistic: Statistic
    private var statPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.export_layout)
        statPosition = intent.getIntExtra("statistic_position", -1)

        val app = application as Application
        statistic = app.statistics[statPosition]!!
    }

    override fun onResume() {
        super.onResume()
        title = statistic.name
    }

    fun onCsvButtonClick(v: View) {

    }

    fun onJsonButtonClick(v: View) {
    }

    private fun sendIntentToActivity(newIntent: Intent) {
        newIntent.putExtra("statistic_position", statPosition)
        startActivity(newIntent)
    }
}