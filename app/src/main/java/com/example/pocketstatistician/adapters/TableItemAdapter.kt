package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type

class TableItemAdapter(val variableType: Type, val variableName: String, val dataSlice: List<String>, val context: Context): RecyclerView.Adapter<TableItemAdapter.ViewHolder>() {

    private var index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (index++ == 0) return TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false))
        return when (variableType.type) {
            "classified" -> {
                ClassifiedViewHolder(Spinner(context))
            }
            else -> {
                UsualViewHolder(EditText(context))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSlice.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            (holder as TextViewHolder).value.text = variableName
            return
        }

        if (variableType.type == "classified") {
            val spinner = (holder as ClassifiedViewHolder).spinnerWithVariants
            val variants = variableType.variants

            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, variants)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = spinnerAdapter

            val selected = variants.indexOfFirst { it == dataSlice[position-1] }
            if (selected != -1)
                spinner.setSelection(selected)

            return
        }

        (holder as UsualViewHolder).value.setText(dataSlice[position-1])
    }

    abstract class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun getValue(): String
    }

    class ClassifiedViewHolder(view: View): ViewHolder(view) {
        val spinnerWithVariants: Spinner = view as Spinner

        override fun getValue(): String {
            return (spinnerWithVariants.selectedItem) as String
        }
    }

    class UsualViewHolder(view: View): ViewHolder(view) {
        val value: EditText = view as EditText

        override fun getValue(): String {
            return value.text.toString()
        }
    }

    class TextViewHolder(view: View): ViewHolder(view) {
        val value: TextView = view.findViewById(R.id.recycler_text_view)

        override fun getValue(): String {
            return value.text.toString()
        }
    }

    fun deleteNote(position: Int) {

    }

}