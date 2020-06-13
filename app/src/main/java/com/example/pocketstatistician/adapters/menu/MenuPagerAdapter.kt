package com.example.pocketstatistician.adapters.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.fragments.NoteFragment
import com.example.pocketstatistician.fragments.menus.StatisticsFragment
import com.example.pocketstatistician.fragments.menus.TypesFragment
import io.realm.RealmList
import io.realm.RealmResults

class MenuPagerAdapter(fragmentActivity: FragmentActivity, val statistics: RealmResults<Statistic>, val types: RealmResults<Type>): FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) StatisticsFragment(statistics)
        else TypesFragment(types)
    }

}