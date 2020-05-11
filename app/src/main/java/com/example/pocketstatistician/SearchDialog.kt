package com.example.pocketstatistician

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList

class SearchDialog(items: RealmList<String>, private val context: Context,
                   private val chosenVariable: TextView){

    private val alertDialog: AlertDialog
    private var selected: View? = null
    private val searchBox: EditText
    private val chosenView: TextView

    init {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)
        val recView = view.findViewById<RecyclerView>(R.id.dialog_list)
        searchBox = view.findViewById(R.id.searchBox)
        chosenView = view.findViewById(R.id.chosen)

        recView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val adapter = SearchDialogAdapter(items)
        adapter.onEntryClickListener = object : SearchDialogAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val selectedViewText = view.findViewById<TextView>(R.id.recycler_text_view).text
                chosenView.text = selectedViewText

                selected?.setBackgroundColor(Color.WHITE)

                selected = view
                selected?.setBackgroundColor(Color.LTGRAY)
            }
        }
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(context)

        dialogBuilder.setView(view)
        alertDialog = dialogBuilder.create()

        searchBox.addTextChangedListener {
            adapter.filterDataBy(searchBox.text.toString())

            selected?.setBackgroundColor(Color.WHITE)
            selected = null
            chosenView.text = ""
        }

        view.findViewById<Button>(R.id.closeButton).setOnClickListener { closeDialog() }
        view.findViewById<Button>(R.id.add_new_variable).setOnClickListener { openVariableEditor() }
        view.findViewById<Button>(R.id.okButton).setOnClickListener { okButtonPressed() }
    }

    fun show() {
        alertDialog.show()
    }

    private fun closeDialog() {
        alertDialog.dismiss()
    }

    private fun openVariableEditor() {
        alertDialog.dismiss()
        val intent = Intent(context, NewVariableActivity::class.java)
        context.startActivity(intent)
        (context as AppCompatActivity).finish()
    }

    private fun okButtonPressed() {
        if (chosenView.text.isBlank()) {
            show(context, context.getString(R.string.nothing_is_chosen))
            return
        }
        chosenVariable.text = chosenView.text
        alertDialog.dismiss()
    }
}