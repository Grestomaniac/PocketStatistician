package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.adapters.menu.StatisticItemAdapter
import io.realm.RealmResults

class MainMenuActivity: AppCompatActivity() {

    lateinit var typesList: RealmResults<Type>
    lateinit var statisticsList: RealmResults<Statistic>
    lateinit var label: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: StatisticItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)

        statisticsList = (application as Application).statistics
        typesList = (application as Application).types

        label = findViewById(R.id.label)

        recyclerView = findViewById(R.id.list_of_items)

        adapter = StatisticItemAdapter(statisticsList)
        adapter.onEntryClickListener = object : StatisticItemAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val intent = Intent(this@MainMenuActivity, StatisticsMenuActivity::class.java)
                intent.putExtra("statistic_number", position)
                startActivity(intent)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        label.text = getString(R.string.statistics_count, statisticsList.size)
    }

    fun onFloatingButtonClick(v: View) {
        val intent = Intent(this, StatisticEditorActivity::class.java)
        startActivity(intent)
    }
}