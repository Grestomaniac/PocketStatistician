package com.example.pocketstatistician

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListOfValuesAdapter(private val data: ArrayList<String>): RecyclerView.Adapter<ListOfValuesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.hint = data[position]
        val textViewAdapter = ArrayAdapter<String>(holder.parent.context, android.R.layout.simple_dropdown_item_1line, data)
        holder.textView.setAdapter(textViewAdapter)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parent = view
        val textView = (view.findViewById(R.id.textView) as AutoCompleteTextView)
    }
}
