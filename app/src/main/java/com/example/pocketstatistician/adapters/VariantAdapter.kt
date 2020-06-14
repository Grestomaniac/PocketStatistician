package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.TypeEditorActivity
import com.example.pocketstatistician.convenience.log

class VariantAdapter(private val variants: ArrayList<TypeEditorActivity.VariantData>, private val context: Context): RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.variant_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return variants.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (variants[position].isDefault) {
            holder.itemView.background = context.getDrawable(R.drawable.default_rounded)
        }
        else holder.itemView.background = context.getDrawable(R.drawable.item_name)

        holder.variantName.setText(variants[position].variant)
        holder.number.text = (position+1).toString()

        holder.variantName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                variants[position].variant = holder.variantName.text.toString()
            }
        }

        holder.variantName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                holder.variantName.clearFocus()
            }
            false
        }
        if (position == itemCount-1) holder.variantName.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val number = view.findViewById(R.id.number) as TextView
        val variantName = view.findViewById(R.id.variant_name) as EditText
        private val deleteButton = view.findViewById(R.id.delete_button) as ImageButton
        init {
            deleteButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }
}
