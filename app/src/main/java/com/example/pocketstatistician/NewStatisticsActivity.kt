package com.example.pocketstatistician

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.new_statistics_layout.*

class NewStatisticsActivity: AppCompatActivity() {

    private lateinit var variablesCount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_statistics_layout)

        val listOfVariables = findViewById<RecyclerView>(R.id.list_of_variables)

        val okCountButton = findViewById<Button>(R.id.ok_count_button)

        variablesCount = findViewById(R.id.variables_count)
    }

    fun onCountButtonClick(view: View) {
        lateinit var variables: RealmResults<Variables>
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Variables::class.java).findAll()
            if (varsFromRealm.size < 2) {
                NoVariablesAlertDialog(this).show(supportFragmentManager, "No Variables Alert Dialog")
            }
            else {
                variables = varsFromRealm
            }
        }

        val count = variablesCount.text.toString()
        if (!isInteger(count, this)) return

        val variableNames = variables.mapTo(RealmList(), { variable ->
            variable.name
        })
        val variableNamesAdapter = ArrayAdapter<String>(this, R.layout.dropdown_menu_item, variableNames)

        val adapter = ListOfValuesAdapter(count.toInt(), variableNamesAdapter, this)

        list_of_variables.adapter = adapter
        list_of_variables.layoutManager = LinearLayoutManager(this)

    }
}