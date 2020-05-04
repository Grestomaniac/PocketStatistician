package com.example.pocketstatistician

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ListOfValuesAdapter(private val data: ArrayList<String>, private val dropDownMenu: RecyclerView): RecyclerView.Adapter<ListOfValuesAdapter.ViewHolder>() {

    private lateinit var selected: EditText
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)
        arrayAdapter = ArrayAdapter(parent.context, R.layout.dropdown_menu_item, data)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dropDownEditText.setOnClickListener {
            if (!dropDownMenu.isVisible) {

            }

            if (selected == holder.dropDownEditText) {

            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parent = view
        val dropDownEditText = (view.findViewById(R.id.drop_down_editText) as EditText)

    }
}
