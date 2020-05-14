package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R

class ListOfValuesAdapter(private val count: Int, private val context: Context): RecyclerView.Adapter<ListOfValuesAdapter.ViewHolder>() {

    private val TAG = this.javaClass.simpleName
    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.variableName.hint = context.getString(R.string.variable, position)
        holder.variableType.text = context.getString(R.string.nothing_is_chosen)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val variableName = (view.findViewById(R.id.variable_name) as EditText)
        val variableType = (view.findViewById(R.id.variable_type) as TextView)
        init {
            variableType.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }
}
