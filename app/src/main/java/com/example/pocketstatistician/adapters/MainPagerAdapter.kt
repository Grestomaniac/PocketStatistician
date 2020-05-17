package com.example.pocketstatistician.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.activities.MainMenuFragment
import com.example.pocketstatistician.activities.NewStatisticsFragment
import com.example.pocketstatistician.activities.NewVariableFragment
import com.example.pocketstatistician.convenience.FragmentWithId

class MainPagerAdapter(val fragmentActivity: MainActivity): FragmentStateAdapter(fragmentActivity) {

    private var nextId = 1L

    val fragmentList = ArrayList<FragmentWithId>()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragmentList.map { it.id }.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].id
    }

    fun addFragment(string: String) {
        val fragment = resolveFragmentByName(string)
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.lastIndex)
    }

    fun replaceFragment(newFragmentName: String, oldFragmentId: Long) {
        val position = getPositionById(oldFragmentId)
        fragmentList.removeAt(position)
        fragmentList.add(position, resolveFragmentByName(newFragmentName))
        notifyItemChanged(position)
    }

    fun removeFragment(fragmentId: Long) {
        val position = getPositionById(fragmentId)
        fragmentList.removeAt(position)
    }

    private fun getPositionById(id: Long): Int {
        return fragmentList.map { it.id }.indexOf(id)
    }

    private fun resolveFragmentByName(name: String): FragmentWithId {
        return when(name) {
            "New statistic" -> NewStatisticsFragment(nextId++, fragmentActivity.variableList, fragmentActivity.statisticsList)
            "New variable" -> NewVariableFragment(nextId++)
            else -> MainMenuFragment(nextId++)
        }
    }
}