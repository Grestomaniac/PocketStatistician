package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.ListOfValuesAdapter
import com.example.pocketstatistician.convenience.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import kotlin.math.abs

class StatisticEditorActivity: AppCompatActivity() {

    private lateinit var variablesCount: EditText
    private lateinit var statisticList: RealmResults<Statistic>
    private lateinit var typeList: RealmResults<Type>
    lateinit var listOfVariables: RecyclerView
    lateinit var variablesAdapter: ListOfValuesAdapter
    lateinit var statisticName: EditText
    lateinit var variablePlaceholder: LinearLayout
    lateinit var quantityChooser: LinearLayout

    private val variables = ArrayList<VariableData>()
    private var statisticPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_editor_layout)

        variablesCount = findViewById(R.id.variant_count)
        statisticName = findViewById(R.id.type_name)
        listOfVariables = findViewById(R.id.list_of_variants)
        variablePlaceholder = findViewById(R.id.variant_placeholder)
        quantityChooser = findViewById(R.id.quantity_chooser)

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
            variablePlaceholder.visibility = View.VISIBLE
            quantityChooser.visibility = View.GONE
        }
        val types = typeList.mapTo(RealmList(), { it.name })

        variablesAdapter = ListOfValuesAdapter(variables, this)

        variablesAdapter.onEntryClickListener = object: ListOfValuesAdapter.OnEntryClickListener {
            override fun onEntryClick(view: TextView, position: Int) {
                val variableName = variables[position].variable.name
                val header = if (variableName.isBlank()) getString(R.string.choose_type) else variableName
                VariantChooserDialog(types, header,this@StatisticEditorActivity, view).show()
                variables[position].variable.type!!.name = view.text.toString()
            }
        }

        listOfVariables.adapter = variablesAdapter
        listOfVariables.layoutManager = LinearLayoutManager(this)

        listOfVariables.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {

            }

            override fun onChildViewAttachedToWindow(view: View) {

            }

        })

        variablesCount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onCountButtonClick(v)
            }
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    fun onCountButtonClick(v: View) {
        currentFocus?.clearFocus()
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

        for (i in 0 until countInt) variables.add(VariableData())
        variablesAdapter.notifyItemRangeInserted(variablesAdapter.itemCount, countInt)

        quantityChooser.visibility = View.GONE
        variablePlaceholder.visibility = View.VISIBLE
    }

    fun onSaveButtonClick() {
        val name = statisticName.text.toString()
        if (name.isBlank()) {
            show(this, getString(R.string.no_name))
            return
        }

        if (variables.size < 2) {
            show(this, getString(R.string.not_enough_variants))
            return
        }

        if (!ifVariableDataIsCorrect()) return
        if (!variablesHaveUniqueNames()) return

        if (statisticPosition != -1) updateStatistic(name)
        else createStatistic(name)

        title = name
        variables.forEach { variant -> variant.isDefault = true }
        variablesAdapter.notifyDataSetChanged()
    }

    private fun updateStatistic(name: String) {
        val statistic = statisticList[statisticPosition]!!
        val statOldVars = statistic.variables
        val blackList = IntArray(statOldVars.size).toMutableList()

        for (i in 0 until variables.size) {
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
        show(this, getString(R.string.updated, name))
    }

    private fun createStatistic(name: String) {

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Statistic(name, variables.mapTo(RealmList(), { it.variable })))
        }
        show(this, getString(R.string.created, name))
        statisticPosition = statisticList.size-1
    }

    private fun ifVariableDataIsCorrect(): Boolean {
        for (variable in variables) {
            if (variable.variable.name.isBlank()) {
                show(this, getString(R.string.empty_string))
                return false
            }
            if (variable.type.name == "") {
                show(this, getString(R.string.not_chosen))
                return false
            }
        }
        return true
    }

    private fun variablesHaveUniqueNames(): Boolean {
        for (i in 0 until variables.size-1) {
            if (containsTwice(variables[i].variable.name, i+1)) {
                show(this, getString(R.string.name_is_not_unique))
                return false
            }
        }
        return true
    }

    private fun containsTwice(name: String, pointer: Int): Boolean {
        for (i in pointer until variables.size) {
            if (variables[i].name == name) return true
        }
        return false
    }

    fun onAddVariableButtonClick(v: View) {
        variables.add(VariableData())
        variablesAdapter.notifyItemInserted(variablesAdapter.itemCount)
    }

    class VariableData(var variable: Variable = Variable(), var isDefault: Boolean = false) {
        var name: String = variable.name
        var type: Type = variable.type!!
        var question: String = variable.question
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            true
        }

        R.id.action_save -> {
            currentFocus?.clearFocus()
            onSaveButtonClick()
            true
        }

        R.id.action_new -> {
            val dialog = YouChooseDialog(getString(R.string.not_saved_data_will_be_deleted), getString(R.string.yes), getString(R.string.no))
            dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
                override fun onPositiveButtonClick() {
                    val newIntent = Intent(this@StatisticEditorActivity, StatisticEditorActivity::class.java)
                    finish()
                    startActivity(newIntent)
                }

                override fun onNegativeButtonClick() {
                }
            }
            dialog.show(supportFragmentManager, "create_new")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}