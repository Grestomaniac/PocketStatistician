package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.Application
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.convenience.YouChooseDialog
import io.realm.Realm

class StatisticsMenuActivity: AppCompatActivity() {

    lateinit var statistic: Statistic
    private var statPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_menu_layout)
        statPosition = intent.getIntExtra("statistic_number", -1)

        val app = application as Application
        statistic = app.statistics[statPosition]!!

        title = statistic.name
    }

    fun onAddButtonClick(v: View) {
        val newIntent = Intent(this, AddNoteActivity::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onTableButtonClick(v: View) {
        val newIntent = Intent(this, TableActivity::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onAnalyzeButtonClick(v: View) {
        val newIntent = Intent(this, Statistic::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onEditButtonClick(v: View) {

    }

    fun onDeleteButtonClick(v: View) {
        val dialog = YouChooseDialog(getString(R.string.delete_anyway), getString(R.string.yes), getString(R.string.no))
        dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
            override fun onPositiveButtonClick() {
                Realm.getDefaultInstance().executeTransaction { realm ->
                    statistic.deleteFromRealm()
                }
                finish()
            }

            override fun onNegativeButtonClick() {
            }

        }
        dialog.show(supportFragmentManager, "delete_statistic")
    }

    private fun sendIntentToActivity(newIntent: Intent) {
        newIntent.putExtra("statistic_position", statPosition)
        startActivity(newIntent)
    }
}