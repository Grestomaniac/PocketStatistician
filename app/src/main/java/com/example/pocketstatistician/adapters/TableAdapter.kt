package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import io.realm.RealmList
import java.util.*

class TableAdapter(val variableTypes: RealmList<Type>, val dataSlice: RealmList<String>, val variableNames: RealmList<String>, val context: Context, val columnCount: Int): RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    var index = 0
    val noteCount = dataSlice.size / variableTypes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_view, parent, false)
        return ViewHolder(view, variableTypes[index]!!, variableNames[index]!!, index++)
    }

    override fun getItemCount(): Int {
        return variableTypes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = holder.item
        val sliceStart = holder.offSet * noteCount
        val sliceEnd = sliceStart + noteCount - 1
        item.adapter = TableItemAdapter(holder.variableType, holder.variableName, dataSlice.slice(sliceStart..sliceEnd), context)
        item.layoutManager = GridLayoutManager(context, columnCount + 1)
    }

    class ViewHolder(view: View, val variableType: Type, val variableName: String, val offSet: Int): RecyclerView.ViewHolder(view) {
        val item: RecyclerView = view.findViewById(R.id.rec_view)
    }
}