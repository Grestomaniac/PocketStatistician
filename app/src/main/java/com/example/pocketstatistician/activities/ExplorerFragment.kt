package com.example.pocketstatistician.activities

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
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.adapters.SearchAdapter
import com.example.pocketstatistician.convenience.FragmentWithId
import io.realm.RealmList
import io.realm.RealmResults

class ExplorerFragment(id: Long, private val variableList: RealmResults<Variable>,
                       private val statisticList: RealmResults<Statistic>, var typeId: Int): FragmentWithId(id) {

    lateinit var searchBox: EditText
    lateinit var listOfNames: RealmList<String>

    private var selected: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.explorer_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        val recyclerView = view!!.findViewById<RecyclerView>(R.id.statistic_data)
        searchBox = view!!.findViewById(R.id.searchBox)

        if (typeId == 0) {
            listOfNames = variableList.mapTo(RealmList(), { it.name })
        }
        else listOfNames = statisticList.mapTo(RealmList(), { it.name })

        val adapter = SearchAdapter(listOfNames)

        adapter.onEntryClickListener = object : SearchAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val selectedViewText = view.findViewById<TextView>(R.id.recycler_text_view).text

                selected?.setBackgroundColor(Color.WHITE)

                selected = view
                selected?.setBackgroundColor(Color.LTGRAY)
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