package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.ListOfValuesAdapter
import com.example.pocketstatistician.convenience.VariantChooserDialog
import com.example.pocketstatistician.convenience.isInteger
import com.example.pocketstatistician.convenience.show
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import kotlin.math.abs

class StatisticEditor(): AppCompatActivity() {

    private lateinit var variablesCount: EditText
    private lateinit var statisticList: RealmResults<Statistic>
    private lateinit var typeList: RealmResults<Type>
    lateinit var listOfVariables: RecyclerView
    lateinit var variablesAdapter: ListOfValuesAdapter
    lateinit var statisticName: EditText
    private val variables = ArrayList<VariableData>()
    private var statisticPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_editor_layout)

        variablesCount = findViewById(R.id.variables_count)
        statisticName = findViewById(R.id.statistic_name)
        listOfVariables = findViewById(R.id.list_of_variables)

        statisticList = (application as Application).statistics
        typeList = (application as Application).types
        statisticPosition = intent.getIntExtra("", -1)
        if (statisticPosition != -1) {
            val statistic = statisticList[statisticPosition]
            statisticName.setText(statistic!!.name)
            variablesCount.setText(statistic.variables.size.toString())

            for (i in 0 until statistic.variables.size) {
                val variable = statistic.variables[i]!!
                variables.add(VariableData(variable, true))
            }
        }
        val types = typeList.mapTo(RealmList(), { it.name })

        variablesAdapter = ListOfValuesAdapter(variables, this)

        variablesAdapter.onEntryClickListener = object: ListOfValuesAdapter.OnEntryClickListener {
            override fun onEntryClick(view: TextView, position: Int) {
                VariantChooserDialog(types, variables[position].variable.name,this@StatisticEditor, view).show()
                variables[position].variable.type!!.name = view.text.toString()
            }
        }

        listOfVariables.adapter = variablesAdapter
        listOfVariables.layoutManager = LinearLayoutManager(this)
    }

    fun onCountButtonClick(v: View) {

        val count = variablesCount.text.toString()
        if (!isInteger(count, this)) {
            show(this, getString(R.string.not_int))
            return
        }
        val countInt = count.toInt()
        if (countInt < 2) {
            show(this, getString(R.string.not_enough_variables))
            return
        }

        val difference = countInt - variablesAdapter.itemCount

        if (difference > 0) {
            for (i in 0 until difference) variables.add(VariableData())
            variablesAdapter.notifyItemRangeInserted(variablesAdapter.itemCount, difference)
        }

        else if (difference < 0) {
            for (i in 0 until abs(difference)){
                variables.removeAt(variables.lastIndex)
            }
            variablesAdapter.notifyItemRangeRemoved(variablesAdapter.itemCount + difference, abs(difference))
        }
    }

    fun onSaveButtonClick(v: View) {
        val name = statisticName.text.toString()
        if (name.isBlank()) {
            show(this, "Введите название статистики")
            return
        }

        if (variables.size < 2) {
            show(this, "Нужны как минимум 2 переменные")
            return
        }

        if (statisticPosition != -1) updateStatistic(name)
        else createStatistic(name)
    }

    private fun updateStatistic(name: String) {
        val statistic = statisticList[statisticPosition]!!
        val statOldVars = statistic.variables
        val blackList = IntArray(statOldVars.size).toMutableList()

        for (i in 0 until variables.size) {
            if (!checkIfDataIsCorrect(i)) return
            val variable = variables[i]
            if (variable.isDefault) {
                val defaultPos = statOldVars.indexOf(variable.variable)
                if (defaultPos != -1) blackList.remove(defaultPos)
            }
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            for (i in blackList) statistic.removeVariableDataAt(i)
            statistic.name = name
            statistic.variables = variables.mapTo(RealmList(), { it.variable })
        }
        show(this, "$name обновлён")
    }

    private fun createStatistic(name: String) {

        for (i in 0 until variables.size) {
            if (!checkIfDataIsCorrect(i)) return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Statistic(name, variables.mapTo(RealmList(), { it.variable })))
        }
        show(this, "$name обновлён")
    }

    private fun checkIfDataIsCorrect(position: Int): Boolean {
        val variableName = variables[position].name
        val variableType = variables[position].type

        if (variableName.isBlank()) {
            show(this, getString(R.string.empty_string))
            return false
        }

        if (!variablesHaveUniqueNames()) {
            show(this, getString(R.string.name_is_not_unique))
            return false
        }

        if (variableType.name == "") {
            show(this, getString(R.string.not_chosen))
            return false
        }
        return true
    }

    private fun variablesHaveUniqueNames(): Boolean {
        val variableNames = variables.mapTo(ArrayList(), { it.name })
        for (variable in variableNames) {
            if (containsTwice(variable, variableNames)) return false
            else variableNames.remove(variable)
        }
        return true
    }

    private fun containsTwice(name: String, names: ArrayList<String>): Boolean {
        var counter = 0
        for (i in names) {
            if (i == name) counter++
            if (counter > 1) return true
        }
        return false
    }

    class VariableData(var variable: Variable = Variable(), var isDefault: Boolean = false) {
        var name: String = variable.name
        var type: Type = variable.type!!
        var question: String = variable.question
    }
}