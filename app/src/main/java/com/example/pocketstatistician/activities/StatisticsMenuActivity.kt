package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic

class StatisticsMenuActivity: AppCompatActivity() {

    lateinit var statistic: Statistic
    private var statPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_menu_layout)
        statPosition = intent.getIntExtra("statistic_number", 0)

        val app = application as Application
        statistic = app.statistics[statPosition]!!

        val statName = findViewById<TextView>(R.id.statistic_name)
        statName.text = statistic.name
    }

    fun onAddButtonClick(v: View) {
        val newIntent = Intent(this, AddNoteActivity::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onTableButtonClick(v: View) {
        val newIntent = Intent(this, TableActivity::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onAnalyzeButtonClick(v: View) {
        val newIntent = Intent(this, Statistic::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onEditButtonClick(v: View) {

    }

    fun onDeleteButtonClick(v: View) {

    }

    private fun sendIntentToActivity(newIntent: Intent) {
        newIntent.putExtra("statistic_number", statPosition)
        startActivity(newIntent)
    }
}