package com.example.pocketstatistician.adapters.table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import io.realm.RealmList

class VariablePlaceholderAdapter(private val variables: RealmList<String>, private val columnSizes: ArrayList<Int>): RecyclerView.Adapter<VariablePlaceholderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return variables.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.variable.text = variables[position]
        val layoutParams = LinearLayout.LayoutParams(columnSizes[position], columnSizes.last())
        holder.variable.layoutParams = layoutParams
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val variable: TextView = view.findViewById(R.id.recycler_text_view)
    }
}