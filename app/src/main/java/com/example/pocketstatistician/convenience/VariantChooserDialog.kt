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
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.activities.StatisticEditorActivity
import com.example.pocketstatistician.adapters.SearchAdapter
import com.example.pocketstatistician.adapters.TypeSearchAdapter
import com.example.pocketstatistician.adapters.VariantAdapter
import io.realm.RealmList
import io.realm.RealmResults
import java.text.ParsePosition

class VariantChooserDialog(variants: RealmList<String>? = null, header: String, context: Context,
                           types: RealmResults<Type>? = null){

    private val alertDialog: AlertDialog
    private val searchBox: EditText
    private val questionBox: TextView
    private val recView: RecyclerView
    var onVariantChosenListener: OnVariantChosenListener? = null

    init {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = View.inflate(context, R.layout.picker_dialog_layout, null)
        recView = view.findViewById(R.id.dialog_list)
        searchBox = view.findViewById(R.id.searchBox)
        questionBox = view.findViewById(R.id.question)

        val searchButton = view.findViewById<ImageView>(R.id.searchIcon)
        searchButton.setOnClickListener { onSearchButtonClick() }

        dialogBuilder.setView(view)
        alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawableResource(R.drawable.table_picker)


        if (variants != null) {
            val adapter = SearchAdapter(variants)
            adapter.onEntryClickListener = object : SearchAdapter.OnEntryClickListener {
                override fun onEntryClick(view: View, position: Int) {
                    onVariantChosenListener?.onVariantChosen(position)

                    alertDialog.dismiss()
                }
            }
            recView.adapter = adapter
        }
        else {
            val adapter = TypeSearchAdapter(types!!)
            adapter.onEntryClickListener = object : TypeSearchAdapter.OnEntryClickListener {
                override fun onEntryClick(view: View, position: Int) {
                    onVariantChosenListener?.onVariantChosen(position)

                    alertDialog.dismiss()
                }
            }
            recView.adapter = adapter
        }

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
        (recView.adapter as Searcher).filterDataBy(searchBox.text.toString())
        searchBox.clearFocus()
    }

    fun show() {
        alertDialog.show()
    }

    interface Searcher {
        fun filterDataBy(predicate: String)
    }

    interface OnVariantChosenListener {
        fun onVariantChosen(itemPos: Int)
    }
}