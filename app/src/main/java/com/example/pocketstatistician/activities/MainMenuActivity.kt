package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.adapters.menu.MenuPagerAdapter
import com.example.pocketstatistician.convenience.log
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.RealmResults

class MainMenuActivity: AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    lateinit var tab: TabLayout
    private var statisticTabSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)

        viewPager = findViewById(R.id.menu_container)
        tab = findViewById(R.id.menu_tab)

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabPosition = tab!!.position
                statisticTabSelected = tabPosition == 0
            }

        })

        viewPager.adapter = MenuPagerAdapter(this)

        TabLayoutMediator(tab, viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.statistics) else getString(R.string.types)
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onFloatingButtonClick(v: View) {
        val intent = if (statisticTabSelected) Intent(this, StatisticEditorActivity::class.java)
        else Intent(this, TypeEditorActivity::class.java)

        startActivity(intent)
    }
}