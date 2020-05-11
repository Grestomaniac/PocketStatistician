package com.example.pocketstatistician

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.new_statistics_layout.*

class NewStatisticsActivity: AppCompatActivity() {

    private lateinit var variablesCount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_statistics_layout)

        variablesCount = findViewById(R.id.variables_count)
    }

    fun onCountButtonClick(view: View) {
        val variablesFromRealm = loadVariables()

        if (variablesFromRealm == null) {
            NoVariablesAlertDialog(this).show(supportFragmentManager, "No Variables Alert Dialog")
            return
        }

        val count = variablesCount.text.toString()
        if (!isInteger(count, this)) return

        val variableNames = variablesFromRealm.mapTo(RealmList(), { variable ->
            variable.name
        })

        val adapter = ListOfValuesAdapter(count.toInt(), this)

        adapter.onEntryClickListener = object: ListOfValuesAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                SearchDialog(variableNames, this@NewStatisticsActivity, view as TextView).show()
            }
        }

        list_of_variables.adapter = adapter
        list_of_variables.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        list_of_variables.layoutManager = LinearLayoutManager(this)

    }

    private fun loadVariables(): RealmResults<Variable>? {
        var variables: RealmResults<Variable>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Variable::class.java).findAll()
            if (varsFromRealm.size > 1) {
                variables = varsFromRealm
            }
        }
        return variables
    }

    private fun onSaveButtonClick() {

    }
}