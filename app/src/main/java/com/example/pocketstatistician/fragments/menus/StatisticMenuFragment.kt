package com.example.pocketstatistician.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import androidx.fragment.app.Fragment
import com.example.pocketstatistician.fragments.table.StatisticAsTableFragment

class StatisticMenuFragment(private val statistic: Statistic): Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistic_menu_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity

        view!!.findViewById<Button>(R.id.button_add_note).setOnClickListener { onAddButtonClick() }
        view!!.findViewById<Button>(R.id.button_table).setOnClickListener { onTableButtonClick() }
        view!!.findViewById<Button>(R.id.button_analyze).setOnClickListener { onAnalyzeButtonClick() }
        view!!.findViewById<Button>(R.id.button_edit).setOnClickListener { onEditButtonClick() }
        view!!.findViewById<Button>(R.id.button_delete).setOnClickListener { onDeleteButtonClick() }
    }

    private fun onAddButtonClick() {
    }

    private fun onTableButtonClick() {
        val statisticAsTableFragment = StatisticAsTableFragment(statistic)
        mainActivity.addFragmentToTab(statisticAsTableFragment)
    }

    private fun onAnalyzeButtonClick() {

    }

    private fun onEditButtonClick() {

    }

    private fun onDeleteButtonClick() {

    }


}