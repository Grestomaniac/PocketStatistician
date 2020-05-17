package com.example.pocketstatistician.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import io.realm.RealmList
import java.util.*

class SearchAdapter(private val defaultDataList: RealmList<String>)
                          : RecyclerView.Adapter<SearchAdapter.SearchDialogViewHolder>() {

    private val dataList: RealmList<String> = RealmList()
    var onEntryClickListener: OnEntryClickListener? = null
    init {
        dataList.addAll(defaultDataList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchDialogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false)
        return SearchDialogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchDialogViewHolder, position: Int) {
        holder.textView.text = dataList[position]
    }

    fun filterDataBy(input: String) {
        updateDataList()
        if (input.isBlank()) {
            notifyDataSetChanged()
            return
        }
        val predicate = input.toLowerCase(Locale.getDefault())

        dataList.removeIf { p ->
            !p.toLowerCase(Locale.getDefault()).startsWith(predicate)
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

        val textView: TextView = view.findViewById(R.id.recycler_text_view)
        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }
}