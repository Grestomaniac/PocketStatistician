package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketstatistician.*
import com.example.pocketstatistician.convenience.YouChooseDialog
import io.realm.Realm

class TypeMenuActivity: AppCompatActivity() {

    lateinit var type: Type
    private var typePosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_menu_layout)
        typePosition = intent.getIntExtra("type_position", -1)

        val app = application as Application
        type = app.types[typePosition]!!
    }

    override fun onResume() {
        super.onResume()
        title = type.name
    }

    fun onEditButtonClick(v: View) {
        val newIntent = Intent(this, TypeEditorActivity::class.java)
        sendIntentToActivity(newIntent)
    }

    fun onDeleteButtonClick(v: View) {
        val dialog = YouChooseDialog(getString(R.string.delete_anyway), getString(R.string.yes), getString(R.string.no))
        dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
            override fun onPositiveButtonClick() {
                Realm.getDefaultInstance().executeTransaction { realm ->
                    type.delete()
                }
                finish()
            }

            override fun onNegativeButtonClick() {
            }

        }
        dialog.show(supportFragmentManager, "delete_type")
    }

    private fun sendIntentToActivity(newIntent: Intent) {
        newIntent.putExtra("type_position", typePosition)
        startActivity(newIntent)
    }
}