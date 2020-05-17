package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.adapters.MainPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.Realm
import io.realm.RealmResults

class MainActivity: FragmentActivity(){

    lateinit var viewPager: ViewPager2
    lateinit var fragmentAdapter: MainPagerAdapter
    lateinit var tabLayout: TabLayout
    val variableList: RealmResults<Variable> = loadVariables()
    val statisticsList: RealmResults<Statistic> = loadStatistics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity_layout)

        viewPager = findViewById(R.id.fragment_container)
        tabLayout = findViewById(R.id.tab_layout)

        fragmentAdapter = MainPagerAdapter(this)
        viewPager.adapter = fragmentAdapter

        fragmentAdapter.addFragment("Main menu")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fragmentAdapter.fragmentList[position].id.toString()
        }.attach()

        findViewById<Button>(R.id.add_tab_button).setOnClickListener { addTab() }

    }

    private fun loadVariables(): RealmResults<Variable> {
        var variables: RealmResults<Variable>? = null
        Realm.getDefaultInstance().executeTransaction { realm ->
            val varsFromRealm = realm.where(Variable::class.java).findAll()
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
        fragmentAdapter.addFragment("Main menu")
    }

    fun addStatisticsButtonOnClick(view: View) {
    }

    fun addVariableButtonOnClick(view: View) {
    }

    fun changeFragment(newFragmentName: String, oldFragmentId: Long) {
        fragmentAdapter.replaceFragment(newFragmentName, oldFragmentId)
    }

}
