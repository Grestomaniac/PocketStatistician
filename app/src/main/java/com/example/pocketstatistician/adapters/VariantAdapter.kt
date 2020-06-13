package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.TypeEditorActivity

class VariantAdapter(private val variants: ArrayList<TypeEditorActivity.VariantData>, private val context: Context): RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return variants.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (variants[position].isDefault) {
            holder.itemView.background = context.getDrawable(R.drawable.default_rounded)
        }
        holder.variantName.setText(variants[position].variant)

        holder.variantName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                variants[position].variant = holder.variantName.text.toString()
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val number = view.findViewById(R.id.number) as TextView
        val variantName = view.findViewById(R.id.variant_name) as EditText
        init {
            number.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }
}
