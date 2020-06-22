package com.example.pocketstatistician.adapters.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import io.realm.RealmResults

class StatisticItemAdapter(private val statisticList: RealmResults<Statistic>): RecyclerView.Adapter<StatisticItemAdapter.StatisticViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_statitstic_item_layout, parent, false)
        return StatisticViewHolder(view)

    }

    override fun getItemCount(): Int {
        return statisticList.size
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.name.text = statisticList[position]!!.name
        holder.noteCount.text = holder.name.context.getString(R.string.note_quantity, statisticList[position]!!.data.size)
        holder.variableCount.text = holder.name.context.getString(R.string.variable_quantity, statisticList[position]!!.variables.size)
    }

    inner class StatisticViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }
        val name: TextView = view.findViewById(R.id.name)
        val noteCount: TextView = view.findViewById(R.id.item_count)
        val variableCount: TextView = view.findViewById(R.id.variable_count)
        val date: TextView = view.findViewById(R.id.last_edited_date)

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }

}