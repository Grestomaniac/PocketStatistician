package com.example.pocketstatistician.fragments.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketstatistician.*
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.adapters.table.TablePagerAdapter

class StatisticAsTableFragment(statistic: Statistic): Fragment() {

    lateinit var mainActivity: MainActivity

    val variableNames = statistic.variable_names
    val variableTypes = statistic.variable_types
    val data = statistic.data

    lateinit var viewPager: ViewPager2
    lateinit var variableNamesRecView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistic_as_table_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        viewPager = view!!.findViewById(R.id.data_and_note_container)

        val tableAdapter = TablePagerAdapter(mainActivity, variableTypes, data, variableNames)
        divideDataIntoFragments(tableAdapter)

        viewPager.adapter = tableAdapter

    }

    private fun divideDataIntoFragments(tableAdapter: TablePagerAdapter) {
        val columnSize = mainActivity.columnCount
        val numberOfExtraFragments = (data.size - 1) / columnSize

        for (i in 0 until numberOfExtraFragments)
            tableAdapter.createAndAddFragment(i * columnSize, columnSize)

        val last = numberOfExtraFragments * columnSize

        tableAdapter.createAndAddFragment(last, data.size - last)

    }

}