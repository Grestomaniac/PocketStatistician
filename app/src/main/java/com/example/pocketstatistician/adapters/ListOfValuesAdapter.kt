package com.example.pocketstatistician.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.StatisticEditorActivity
import com.example.pocketstatistician.convenience.YouChooseDialog

class ListOfValuesAdapter(private val variables: ArrayList<StatisticEditorActivity.VariableData>, private val context: Context): RecyclerView.Adapter<ListOfValuesAdapter.ViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return variables.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (variables[position].isDefault) {
            holder.itemView.background = context.getDrawable(R.drawable.default_rounded)

            holder.variableType.text = variables[position].type.name
            holder.variableType.setOnClickListener {
                val dialog = YouChooseDialog(context.getString(R.string.edit_or_not), context.getString(R.string.continue_anyway), context.getString(R.string.cancel))
                dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
                    override fun onPositiveButtonClick() {
                        holder.variableType.setOnClickListener(holder)
                        variables[position].isDefault = false
                        holder.itemView.background = context.getDrawable(R.drawable.item_name)
                    }

                    override fun onNegativeButtonClick() {
                    }
                }
            }

            holder.variableName.setText(variables[position].name)
        }

        else {
            holder.itemView.background = context.getDrawable(R.drawable.item_name)

            holder.variableType.text = if (variables[position].type.name.isBlank()) context.getString(R.string.nothing_is_chosen)
            else variables[position].type.name

            holder.variableName.setText(variables[position].name)
        }

        holder.variableName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                variables[position].name = holder.variableName.text.toString()
            }
        }

        holder.variableName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                holder.variableName.clearFocus()
            }
            false
        }
        if (position == itemCount-1) holder.variableName.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val variableName = view.findViewById(R.id.variable_name) as EditText
        val variableType = view.findViewById(R.id.variable_type) as TextView
        init {
            variableType.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(variableType, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: TextView, position: Int)
    }
}
