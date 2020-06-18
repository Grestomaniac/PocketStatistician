package com.example.pocketstatistician.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.convenience.VariantChooserDialog
import io.realm.RealmList
import io.realm.RealmResults
import java.util.*

class TypeSearchAdapter(private val defaultDataList: RealmResults<Type>)
                          : RecyclerView.Adapter<TypeSearchAdapter.SearchDialogViewHolder>(), VariantChooserDialog.Searcher {

    private val dataList: RealmList<Type> = RealmList()
    var onEntryClickListener: OnEntryClickListener? = null
    init {
        dataList.addAll(defaultDataList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchDialogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.picker_item_type, parent, false)
        return SearchDialogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchDialogViewHolder, position: Int) {
        holder.name.text = dataList[position]!!.name
        holder.type.text = dataList[position]!!.type
    }

    override fun filterDataBy(predicate: String) {
        updateDataList()
        if (predicate.isBlank()) {
            notifyDataSetChanged()
            return
        }
        val pred = predicate.toLowerCase(Locale.getDefault())

        dataList.removeIf { p ->
            !p.name.toLowerCase(Locale.getDefault()).startsWith(pred)
        }

        notifyDataSetChanged()
    }

    private fun updateDataList() {
        dataList.clear()
        dataList.addAll(defaultDataList)
    }

    inner class SearchDialogViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        val name: TextView = view.findViewById(R.id.picker_item_name)
        val type: TextView = view.findViewById(R.id.picker_item_type)

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }
}