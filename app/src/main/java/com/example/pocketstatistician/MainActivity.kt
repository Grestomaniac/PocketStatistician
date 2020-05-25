package com.example.pocketstatistician

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.pocketstatistician.convenience.log
import com.example.pocketstatistician.fragments.menus.MainMenuFragment
import com.google.android.material.tabs.TabLayout
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: FragmentActivity(){

    val columnCount = 3
    private val tabsStack: ArrayList<Stack<Fragment>> = ArrayList()
    lateinit var fragmentContainer: FrameLayout
    lateinit var tabLayout: TabLayout
    lateinit var variableList: RealmResults<Type>
    lateinit var statisticsList: RealmResults<Statistic>
    private var nextFragmentId = 1L
    private var currentTabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        variableList = loadVariables()
        statisticsList = loadStatistics()

        fragmentContainer = findViewById(R.id.fragment_container)
        tabLayout = findViewById(R.id.tab_layout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPosition = tab!!.position

                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                transaction.replace(R.id.fragment_container, tabsStack[currentTabPosition].lastElement()).commit()
            }

        })

        findViewById<Button>(R.id.add_tab_button).setOnClickListener { addTab() }

        addTab()

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

    private fun addTab(fragment: Fragment = MainMenuFragment()) {
        val newStack = Stack<Fragment>()
        newStack.push(fragment)
        tabsStack.add(newStack)

        val newTab = tabLayout.newTab()
        tabLayout.addTab(newTab)
        newTab.text = newTab.position.toString()

        tabLayout.selectTab(tabLayout.getTabAt(newTab.position))
    }

    private fun removeTab(tabPos: Int = currentTabPosition) {
        var targetPos = tabPos - 1
        if (tabPos == 0) {
            if (tabPos < tabLayout.tabCount) {
                log("no more tabs")
                return
                //TODO(add something to handle one tab situation)
            }
            targetPos = tabPos + 1
        }

        tabLayout.selectTab(tabLayout.getTabAt(targetPos))
        tabLayout.removeTabAt(tabPos)
        tabsStack.removeAt(tabPos)
    }

    fun addFragmentToTab(fragment: Fragment, tabId: Int = currentTabPosition) {
        tabsStack[tabId].push(fragment)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        transaction.replace(R.id.fragment_container, fragment).commit()
    }

    private fun popFragmentFromTab(tabPos: Int = currentTabPosition) {
        tabsStack[tabPos].pop()
        val previousFragment = tabsStack[tabPos].lastElement()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        transaction.replace(R.id.fragment_container, previousFragment).commit()
    }

    override fun onBackPressed() {
        if (tabsStack[currentTabPosition].size == 1) {
            //TODO(add dialog or something)
            log("closing activity")
            finish()
            return
        }

        popFragmentFromTab()
    }

    fun getNextId(): Long {
        return nextFragmentId++
    }

}
