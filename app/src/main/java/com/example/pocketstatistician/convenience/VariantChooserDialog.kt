package com.example.pocketstatistician.convenience

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.adapters.SearchAdapter
import io.realm.RealmList

class VariantChooserDialog(variants: RealmList<String>, context: Context,
                           private val picker: TextView){

    private val alertDialog: AlertDialog
    private val searchBox: EditText

    init {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = View.inflate(context, R.layout.picker_dialog_layout, null)
        val recView = view.findViewById<RecyclerView>(R.id.dialog_list)
        searchBox = view.findViewById(R.id.searchBox)

        dialogBuilder.setView(view)
        alertDialog = dialogBuilder.create()

        recView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val adapter = SearchAdapter(variants)
        adapter.onEntryClickListener = object : SearchAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val selectedViewText = view.findViewById<TextView>(R.id.recycler_text_view).text

                picker.text = selectedViewText

                alertDialog.dismiss()
            }
        }
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(context)

        searchBox.addTextChangedListener {
            adapter.filterDataBy(searchBox.text.toString())
        }
    }

    fun show() {
        alertDialog.show()
    }
}