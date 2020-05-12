package com.example.pocketstatistician

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class NewStatisticsActivity: AppCompatActivity() {

    private lateinit var variablesCount: EditText
    private var variableList: RealmResults<Variable>? = null

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

        variableList = variablesFromRealm

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

        val listOfVariables = findViewById<RecyclerView>(R.id.list_of_variables)

        listOfVariables.adapter = adapter
        listOfVariables.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        listOfVariables.layoutManager = LinearLayoutManager(this)

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
        val statisticName = findViewById<EditText>(R.id.variable_name).text.toString()
        val variableRecView = findViewById<RecyclerView>(R.id.list_of_variables)
        val variableNames = RealmList<String>()
        val variableTypes = RealmList<String>()
        for (i in 0 until variableRecView.size) {
            val viewHolder = variableRecView.findViewHolderForAdapterPosition(i) as ListOfValuesAdapter.ViewHolder
            val chosenVariable = findElementByName(variableList, )
            variableTypes.add(getEl)
        }

        if (dataIsNotCorrect(variableName, variableType, variantsList)) {
            Toast.makeText(this, R.string.data_is_not_correct, Toast.LENGTH_SHORT).show()
            return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Variable(
                variableName,
                variableType,
                variantsList
            ))
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}