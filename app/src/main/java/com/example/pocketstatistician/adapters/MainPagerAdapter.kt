package com.example.pocketstatistician.adapters

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.convenience.FragmentWithId

class MainPagerAdapter(fragmentActivity: MainActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = ArrayList<FragmentWithId>()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return fragmentList[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragmentList.map { it.id }.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].id
    }

    fun addFragment(fragment: FragmentWithId) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.lastIndex)
    }

    fun replaceFragment(newFragment: FragmentWithId, oldFragmentId: Long) {
        val position = getPositionById(oldFragmentId)
        fragmentList.removeAt(position)
        fragmentList.add(position, newFragment)
        notifyItemChanged(position)
    }

    fun removeFragment(fragmentId: Long) {
        val position = getPositionById(fragmentId)
        fragmentList.removeAt(position)
    }

    private fun getPositionById(id: Long): Int {
        return fragmentList.map { it.id }.indexOf(id)
    }
}