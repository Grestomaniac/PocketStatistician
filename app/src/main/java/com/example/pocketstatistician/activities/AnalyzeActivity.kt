package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.Analyzer
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic

class AnalyzeActivity: AppCompatActivity() {

    lateinit var statistic: Statistic
    private var statPosition: Int = -1
    lateinit var analyzer: Analyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analyze_layout)
        statPosition = intent.getIntExtra("statistic_position", -1)

        val app = application as Application
        statistic = app.statistics[statPosition]!!

        analyzer = Analyzer(statistic)
        analyzer.initialize()

    }

    fun onManualEditButtonClick(view: View) {}
}