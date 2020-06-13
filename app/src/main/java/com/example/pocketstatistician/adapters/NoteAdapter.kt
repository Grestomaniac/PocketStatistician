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
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Variable
import io.realm.RealmList

class NoteAdapter(val variables: RealmList<Variable>, val note: Note, val context: Context): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (variables[index++]!!.type!!.type) {
            "classified" -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_with_spinner, parent, false)
                ClassifiedViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_with_edit_text, parent, false)
                UsualViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return variables.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val varType = variables[position]!!.type
        holder.variableName.text = variables[position]!!.name

        if (varType!!.type == "classified") {
            val spinner = (holder as ClassifiedViewHolder).spinnerWithVariants
            val variants = RealmList("")
            variants.addAll(varType.variants)

            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, variants)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = spinnerAdapter

            val selected = variants.indexOfFirst { it == note.note[position] }
            if (selected != -1)
                spinner.setSelection(selected)

            return
        }

        (holder as UsualViewHolder).value.setText(note.note[position])
    }

    abstract class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val variableName: TextView = view.findViewById(R.id.variable_name)
        abstract fun getValue(): String
        abstract fun clear()
    }

    class ClassifiedViewHolder(view: View): ViewHolder(view) {
        val spinnerWithVariants: Spinner = view.findViewById(R.id.classified_spinner)

        override fun getValue(): String {
            return (spinnerWithVariants.selectedItem) as String
        }

        override fun clear() {
            spinnerWithVariants.setSelection(0)
        }
    }

    class UsualViewHolder(view: View): ViewHolder(view) {
        val value: EditText = view.findViewById(R.id.edit_text)

        override fun getValue(): String {
            return value.text.toString()
        }

        override fun clear() {
            value.text.clear()
        }
    }

}