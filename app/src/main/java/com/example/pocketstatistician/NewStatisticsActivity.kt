package com.example.pocketstatistician

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.new_statistics_layout.*
import kotlinx.android.synthetic.main.value_item.*

class NewStatisticsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_statistics_layout)

        val listOfVariables = findViewById<RecyclerView>(R.id.list_of_variables)

        val okCountButton = findViewById<Button>(R.id.ok_count_button)
    }

    fun onCountButtonClick(view: View) {
        val variablesCount = findViewById<EditText>(R.id.variables_count).text.toString()
        if (!isInteger(variablesCount, this)) return

        val variables = ArrayList<String>()

        for (i in 0 until variablesCount.toInt()) {
            variables.add("${getString(R.string.variable)} ${i+1}")
        }

        val adapter = ListOfValuesAdapter(variables)

        list_of_variables.adapter = adapter
        list_of_variables.layoutManager = LinearLayoutManager(this)

    }
}