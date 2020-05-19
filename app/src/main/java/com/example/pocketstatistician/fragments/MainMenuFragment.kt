package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pocketstatistician.MainActivity
import com.example.pocketstatistician.R
import com.example.pocketstatistician.convenience.FragmentWithId

class MainMenuFragment(id: Long): FragmentWithId(id) {

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity

        view!!.findViewById<Button>(R.id.stat_button).setOnClickListener { changeFragment(NewStatisticsFragment(mainActivity.getNextId())) }
        view!!.findViewById<Button>(R.id.variable_button).setOnClickListener { changeFragment(NewTypeFragment(mainActivity.getNextId())) }
        view!!.findViewById<Button>(R.id.explorer_button).setOnClickListener { changeFragment(ExplorerFragment(mainActivity.getNextId(), 1)) }
    }

    private fun changeFragment(fragment: FragmentWithId) {
        mainActivity.addFragmentToTab(fragment)
    }



}