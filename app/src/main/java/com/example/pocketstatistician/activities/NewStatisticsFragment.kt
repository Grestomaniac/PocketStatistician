package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.ListOfValuesAdapter
import com.example.pocketstatistician.convenience.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class NewStatisticsFragment: Fragment() {

    private lateinit var variablesCount: EditText
    private val variableList: RealmResults<Variable>
    private val statisticsList: RealmResults<Statistic>

    init {
        variableList = loadVariables()
        statisticsList = loadStatistics()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_statistics_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        variablesCount = view!!.findViewById(R.id.variables_count)


        view!!.findViewById<Button>(R.id.createStatistics).setOnClickListener { onSaveButtonClick() }
        view!!.findViewById<Button>(R.id.ok_count_button).setOnClickListener { onCountButtonClick() }
    }

    private fun onCountButtonClick() {
        if (variableList.size == 0) {
            NoVariablesAlertDialog().show(activity!!.supportFragmentManager, "No Variables Alert Dialog")
            return
        }

        val count = variablesCount.text.toString()
        if (!isInteger(count, activity!!)) return

        val variableNames = variableList.mapTo(RealmList(), { variable ->
            variable.name
        })

        val adapter = ListOfValuesAdapter(count.toInt(), activity!!)

        adapter.onEntryClickListener = object:
            ListOfValuesAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                SearchDialog(variableNames, activity!!, view as TextView).show()
            }
        }

        val listOfVariables = view!!.findViewById<RecyclerView>(R.id.list_of_variables)

        listOfVariables.adapter = adapter
        listOfVariables.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))
        listOfVariables.layoutManager = LinearLayoutManager(activity!!)

    }

    private fun loadVariables(): RealmResults<Variable> {
        var variables: RealmResults<Variable>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Variable::class.java).findAll()
            variables = varsFromRealm
        }
        return variables!!
    }

    private fun loadStatistics(): RealmResults<Statistic> {
        var statistics: RealmResults<Statistic>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val dataFromRealm = realm.where(Statistic::class.java).findAll()
            statistics = dataFromRealm
        }
        return statistics!!
    }

    private fun onSaveButtonClick() {
        val statisticName = view!!.findViewById<EditText>(R.id.name_statistics).text.toString()
        if (statisticName.isBlank()) {
            show(activity!!, "Введите название статистики")
            return
        }
        if (!hasUniqueName(statisticName, statisticsList.mapTo(RealmList(),
            { statistic -> statistic.name }))) {
            show(activity!!, "Такое название уже есть")
            return
        }

        val variableRecView = view!!.findViewById<RecyclerView>(R.id.list_of_variables)

        if (variableRecView.childCount == 0) {
            show(activity!!, "Переменные ещё не созданы")
        }
        val variableNames = RealmList<String>()
        val variableTypes = RealmList<Variable>()

        for (i in 0 until variableRecView.childCount) {
            val viewHolder = variableRecView.findViewHolderForAdapterPosition(i) as ListOfValuesAdapter.ViewHolder
            val variableName = viewHolder.variableName.text.toString()
            val variableType = viewHolder.variableType.text.toString()

            if (hasUniqueName(variableName, variableNames))
                variableNames.add(variableName)
            else {
                show(activity!!, getString(R.string.name_is_not_unique))
                return
            }

            if (variableType.equals(getString(R.string.nothing_is_chosen))) {
                show(activity!!, getString(R.string.not_chosen))
                return
            }
            else
                variableTypes.add(findElementByName(variableList, viewHolder.variableType.text.toString()))
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(
                Statistic(
                    statisticName,
                    variableNames,
                    variableTypes
                )
            )
        }
        show(activity!!, "$statisticName создан")
    }

    private fun hasUniqueName(name: String, listOfNames: RealmList<String>): Boolean {
        for (item in listOfNames) {
            if (item == name) return false
        }

        return true
    }
}