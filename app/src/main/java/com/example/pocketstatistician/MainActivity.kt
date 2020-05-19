package com.example.pocketstatistician

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketstatistician.adapters.MainPagerAdapter
import com.example.pocketstatistician.convenience.FragmentWithId
import com.example.pocketstatistician.fragments.MainMenuFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.Realm
import io.realm.RealmResults

class MainActivity: FragmentActivity(){

    lateinit var viewPager: ViewPager2
    lateinit var fragmentAdapter: MainPagerAdapter
    lateinit var tabLayout: TabLayout
    lateinit var variableList: RealmResults<Type>
    lateinit var statisticsList: RealmResults<Statistic>
    private var nextid = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        variableList = loadVariables()
        statisticsList = loadStatistics()

        viewPager = findViewById(R.id.fragment_container)
        tabLayout = findViewById(R.id.tab_layout)

        fragmentAdapter = MainPagerAdapter(this)
        viewPager.adapter = fragmentAdapter

        val newFragment =
            MainMenuFragment(getNextId())

        fragmentAdapter.addFragment(newFragment)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fragmentAdapter.fragmentList[position].id.toString()
        }.attach()

        findViewById<Button>(R.id.add_tab_button).setOnClickListener { addTab() }

    }

    private fun loadVariables(): RealmResults<Type> {
        var variables: RealmResults<Type>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Type::class.java).findAll()
            variables = varsFromRealm
        }
        return variables!!
    }

    private fun loadStatistics(): RealmResults<Statistic> {
        var statistics: RealmResults<Statistic>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val dataFromRealm = realm.where(Statistic::class.java).findAll()
            statistics = dataFromRealm
        }
        return statistics!!
    }

    private fun addTab() {
        fragmentAdapter.addFragment(
            MainMenuFragment(
                getNextId()
            )
        )
    }

    fun getNextId(): Long {
        return nextid++
    }

    fun changeFragment(newFragment: FragmentWithId, oldFragmentId: Long) {
        fragmentAdapter.replaceFragment(newFragment, oldFragmentId)
    }

}
