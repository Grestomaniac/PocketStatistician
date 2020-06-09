package com.example.pocketstatistician.fragments.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.R
import com.example.pocketstatistician.fragments.ExplorerFragment
import com.example.pocketstatistician.fragments.editors.StatisticsEditorFragment
import com.example.pocketstatistician.fragments.editors.TypeEditorFragment

class MainMenuFragment: Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity

        view!!.findViewById<Button>(R.id.stat_button).setOnClickListener { changeFragment(
            StatisticsEditorFragment()
        ) }
        view!!.findViewById<Button>(R.id.variable_button).setOnClickListener { changeFragment(
            TypeEditorFragment()
        ) }
        view!!.findViewById<Button>(R.id.explorer_button).setOnClickListener { changeFragment(
            ExplorerFragment(1)
        ) }
    }

    private fun changeFragment(fragment: Fragment) {
        mainActivity.addFragmentToTab(fragment)
    }



}