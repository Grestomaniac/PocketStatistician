package com.example.pocketstatistician.convenience

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.adapters.SearchAdapter
import io.realm.RealmList

class VariantChooserDialog(variants: RealmList<String>, header: String, context: Context,
                           private val picker: TextView){

    private val alertDialog: AlertDialog
    private val searchBox: EditText
    private val questionBox: TextView
    private val adapter: SearchAdapter

    init {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = View.inflate(context, R.layout.picker_dialog_layout, null)
        val recView = view.findViewById<RecyclerView>(R.id.dialog_list)
        searchBox = view.findViewById(R.id.searchBox)
        questionBox = view.findViewById(R.id.question)

        val searchButton = view.findViewById<ImageView>(R.id.searchIcon)
        searchButton.setOnClickListener { onSearchButtonClick() }

        dialogBuilder.setView(view)
        alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawableResource(R.drawable.table_picker)

        adapter = SearchAdapter(variants)
        adapter.onEntryClickListener = object : SearchAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val selectedViewText = view.findViewById<TextView>(R.id.picker_item).text

                picker.text = selectedViewText

                alertDialog.dismiss()
            }
        }
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(context)

        searchBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchButtonClick()
            }
            false
        }

        questionBox.text = header
    }

    fun onSearchButtonClick() {
        adapter.filterDataBy(searchBox.text.toString())
        searchBox.clearFocus()
    }

    fun show() {
        alertDialog.show()
    }
}