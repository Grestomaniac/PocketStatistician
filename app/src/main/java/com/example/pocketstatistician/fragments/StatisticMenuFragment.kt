package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pocketstatistician.MainActivity
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.convenience.FragmentWithId
import com.example.pocketstatistician.convenience.log

class StatisticMenuFragment(id: Long, private val statistic: Statistic): FragmentWithId(id) {

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
        log("0")
        val addNoteFragment = AddNoteFragment(mainActivity.getNextId(), statistic)
        mainActivity.addFragmentToTab(addNoteFragment)
    }

    private fun onTableButtonClick() {

    }

    private fun onAnalyzeButtonClick() {

    }

    private fun onEditButtonClick() {

    }

    private fun onDeleteButtonClick() {

    }


}