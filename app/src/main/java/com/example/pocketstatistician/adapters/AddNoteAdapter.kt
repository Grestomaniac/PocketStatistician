package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic

class AddNoteAdapter(statistic: Statistic, val context: Context): RecyclerView.Adapter<AddNoteAdapter.ViewHolder>() {
    private val variables = statistic.variable_types
    private val variableNames = statistic.variable_names
    private var index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val variableType = variables[index++]!!.type

        return when (variableType) {
            "classified" -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.text_view_and_spinner, parent, false)
                ClassifiedViewHolder(view, variableType)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.text_view_and_edit_text, parent, false)
                UsualViewHolder(view, variableType)
            }
        }
    }

    override fun getItemCount(): Int {
        return variables.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.variableName.text = variableNames[position]
        if (holder.type == "classified") {
            val spinner = (holder as ClassifiedViewHolder).spinnerWithVariants
            val variants = variables[position]!!.variants

            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, variants)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = spinnerAdapter

            return
        }
    }

    abstract class ViewHolder(view: View, val type: String): RecyclerView.ViewHolder(view) {
        val variableName: TextView = view.findViewById(R.id.var_name)
        abstract fun getValue(): String
    }

    class ClassifiedViewHolder(view: View, type: String): ViewHolder(view, type) {
        val spinnerWithVariants: Spinner = view.findViewById(R.id.classified_spinner)

        override fun getValue(): String {
            return (spinnerWithVariants.selectedItem) as String
        }
    }

    class UsualViewHolder(view: View, type: String): ViewHolder(view, type) {
        val value: EditText = view.findViewById(R.id.value)

        override fun getValue(): String {
            return value.text.toString()
        }
    }

}