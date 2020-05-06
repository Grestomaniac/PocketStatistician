package com.example.pocketstatistician

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList

class ListOfValuesAdapter(private val count: Int, private val adapter: ArrayAdapter<String>, private val context: Context): RecyclerView.Adapter<ListOfValuesAdapter.ViewHolder>() {

    private val TAG = this.javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.value_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.autoCompleteTextView.setAdapter(adapter)
        holder.autoCompleteTextView.hint = "${context.getString(R.string.variable)} ${position + 1}"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val autoCompleteTextView = (view.findViewById(R.id.drop_down_editText) as AutoCompleteTextView)
    }
}
