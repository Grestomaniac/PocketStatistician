package com.example.pocketstatistician.adapters.table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.TableActivity
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class DataPlaceHolderItemAdapter(private val note: Note, private val columnSizes: ArrayList<Int>, private val activity: TableActivity): RecyclerView.Adapter<DataPlaceHolderItemAdapter.ViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return note.note.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = note.note[position]
        val layoutParams = LinearLayout.LayoutParams(columnSizes[position], columnSizes.last())
        holder.textView.layoutParams = layoutParams
        holder.textView.background = activity.getDrawable(R.drawable.table_data)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }
        val textView: TextView = view.findViewById(R.id.recycler_text_view)
        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(textView, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, positionOfItem: Int)
    }
}