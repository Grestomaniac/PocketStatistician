package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.pocketstatistician.R
import com.example.pocketstatistician.convenience.FragmentWithId

class MainMenuFragment(id: Long): FragmentWithId(id) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        val newStatButton = view!!.findViewById<Button>(R.id.stat_button)
        val newVarButton = view!!.findViewById<Button>(R.id.variable_button)

        newStatButton.setOnClickListener { changeFragment("New statistic") }
        newVarButton.setOnClickListener { changeFragment("New variable") }
    }

    private fun changeFragment(string: String) {
        (activity as MainActivity).changeFragment(string, id)
    }



}