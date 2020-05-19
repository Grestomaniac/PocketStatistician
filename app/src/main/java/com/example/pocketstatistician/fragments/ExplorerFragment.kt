package com.example.pocketstatistician.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.MainActivity
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.adapters.SearchAdapter
import com.example.pocketstatistician.convenience.FragmentWithId
import io.realm.RealmList
import io.realm.RealmResults

class ExplorerFragment(id: Long, var typeId: Int): FragmentWithId(id) {

    lateinit var searchBox: EditText
    lateinit var listOfNames: RealmList<String>
    private lateinit var variableList: RealmResults<Type>
    private lateinit var statisticList: RealmResults<Statistic>

    private var selected: View? = null
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.explorer_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        variableList = mainActivity.variableList
        statisticList = mainActivity.statisticsList

        val recyclerView = view!!.findViewById<RecyclerView>(R.id.statistic_data)
        searchBox = view!!.findViewById(R.id.searchBox)

        listOfNames = if (typeId == 0) {
            variableList.mapTo(RealmList(), { it.name })
        } else statisticList.mapTo(RealmList(), { it.name })

        val adapter = SearchAdapter(listOfNames)

        adapter.onEntryClickListener = object : SearchAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val text = (view as TextView).text.toString()
                when (typeId) {
                    0 ->  {
                        val index = variableList.indexOfFirst { it.name == text }
                        val menuFragment = TypeMenuFragment(mainActivity.getNextId(), variableList[index]!!)
                        mainActivity.changeFragment(menuFragment, id)
                    }
                    else -> {
                        val index = statisticList.indexOfFirst { it.name == text }
                        val menuFragment = StatisticMenuFragment(mainActivity.getNextId(), statisticList[index]!!)
                        mainActivity.changeFragment(menuFragment, id)
                    }
                }

            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchBox.addTextChangedListener {
            adapter.filterDataBy(searchBox.text.toString())

            selected?.setBackgroundColor(Color.WHITE)
            selected = null
        }

    }
}