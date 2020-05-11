package com.example.pocketstatistician

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class VariantsAdapter(private val count: Int): RecyclerView.Adapter<VariantsAdapter.VariantsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.variant_layout, parent, false)
        return VariantsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: VariantsViewHolder, position: Int) {
        holder.variantValue.hint = position.toString()
    }

    class VariantsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val variantValue: EditText = view.findViewById(R.id.variant_value)
    }

}