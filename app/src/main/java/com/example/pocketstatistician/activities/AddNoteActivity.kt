package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.adapters.table.NotePagerAdapter
import com.example.pocketstatistician.convenience.log
import com.example.pocketstatistician.convenience.show
import io.realm.Realm

class AddNoteActivity: FragmentActivity() {

    lateinit var statistic: Statistic
    lateinit var viewPager: ViewPager2
    lateinit var viewPagerAdapter: NotePagerAdapter
    lateinit var header2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_note_layout)

        val statPosition = intent.getIntExtra("statistic_position", -1)
        statistic = (application as Application).statistics[statPosition]!!

        val header1 = findViewById<TextView>(R.id.header_1)
        header2 = findViewById(R.id.header_2)
        header1.text = statistic.name

        viewPager = findViewById(R.id.note_container)

        viewPagerAdapter = NotePagerAdapter(this, statistic)
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                header2.text = getString(R.string.add_note_header2, position+1, statistic.data.size)
            }
        })
        viewPager.currentItem = viewPagerAdapter.itemCount - 1
    }

    fun onDeleteButtonClick(v: View) {
        val currentItemPos = viewPager.currentItem
        if (currentItemPos == statistic.data.size) {
            show(this, getString(R.string.cannot_delete))
            return
        }

        viewPagerAdapter.removeFragment(currentItemPos)
        Realm.getDefaultInstance().executeTransaction {
            statistic.data.removeAt(currentItemPos)
        }
        header2.text = getString(R.string.add_note_header2, currentItemPos+1, statistic.data.size)
        show(this, getString(R.string.finished))
    }

    fun onSaveButtonClick(v: View) {
        val currentItemPos = viewPager.currentItem
        if (currentItemPos == statistic.data.size) {
            viewPagerAdapter.addEmptyFragment()

            Realm.getDefaultInstance().executeTransaction {
                statistic.data.add(viewPagerAdapter.getNoteData(currentItemPos))
            }
            header2.text = getString(R.string.add_note_header2, currentItemPos+1, statistic.data.size)
            show(this, getString(R.string.finished))
            return
        }
        Realm.getDefaultInstance().executeTransaction {
            statistic.data[currentItemPos] = viewPagerAdapter.getNoteData(currentItemPos)
        }
        show(this, getString(R.string.finished))
    }

    fun onClearButtonClick(v: View) {
        val currentItemPos = viewPager.currentItem
        viewPagerAdapter.clearData(currentItemPos)
        show(this, getString(R.string.finished))
    }
}