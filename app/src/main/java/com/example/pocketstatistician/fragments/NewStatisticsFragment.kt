package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.ListOfValuesAdapter
import com.example.pocketstatistician.convenience.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class NewStatisticsFragment(id: Long): FragmentWithId(id) {

    private lateinit var variablesCount: EditText
    private lateinit var mainActivity: MainActivity
    private lateinit var statisticList: RealmResults<Statistic>
    private lateinit var variableList: RealmResults<Type>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_statistic_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        statisticList = mainActivity.statisticsList
        variableList = mainActivity.variableList

        variablesCount = view!!.findViewById(R.id.variables_count)

        view!!.findViewById<Button>(R.id.createStatistics).setOnClickListener { onSaveButtonClick() }
        view!!.findViewById<Button>(R.id.ok_count_button).setOnClickListener { onCountButtonClick() }
    }

    private fun onCountButtonClick() {

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

    private fun onSaveButtonClick() {
        val statisticName = view!!.findViewById<EditText>(R.id.name_statistics).text.toString()
        if (statisticName.isBlank()) {
            show(activity!!, "Введите название статистики")
            return
        }
        if (!hasUniqueName(statisticName, statisticList.mapTo(RealmList(),
            { statistic -> statistic.name }))) {
            show(activity!!, "Такое название уже есть")
            return
        }

        val variableRecView = view!!.findViewById<RecyclerView>(R.id.list_of_variables)

        if (variableRecView.childCount == 0) {
            show(activity!!, "Переменные ещё не созданы")
        }
        val variableNames = RealmList<String>()
        val variableTypes = RealmList<Type>()

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

            if (variableType == getString(R.string.nothing_is_chosen)) {
                show(activity!!, getString(R.string.not_chosen))
                return
            }
            else {
                val variable = variableList.find { it.name == viewHolder.variableType.text.toString() }
                variableTypes.add(variable)
            }
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
        return !listOfNames.contains(name)
    }
}