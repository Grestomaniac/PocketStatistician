package com.example.pocketstatistician.adapters.table

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.MainActivity
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.fragments.table.DataFragment
import io.realm.RealmList

class TablePagerAdapter(fragmentActivity: MainActivity, val variableTypes: RealmList<Type>, val data: RealmList<Note>, val variableNames: RealmList<String>): FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = ArrayList<DataFragment>()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return fragmentList[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragmentList.map { it.dataId }.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].dataId
    }

    fun removeFragment(fragmentId: Long) {
        val position = getPositionById(fragmentId)
        fragmentList.removeAt(position)
    }

    private fun getPositionById(id: Long): Int {
        return fragmentList.map { it.dataId }.indexOf(id)
    }

    fun createAndAddFragment(offSet: Int, dataSize: Int) {
        val dataFragment = DataFragment(variableTypes, data, variableNames, offSet, dataSize)
        fragmentList.add(dataFragment)
        notifyItemInserted(fragmentList.lastIndex)
    }
}