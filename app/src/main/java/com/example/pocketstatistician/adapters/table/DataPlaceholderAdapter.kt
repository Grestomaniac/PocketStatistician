package com.example.pocketstatistician.adapters.table

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.TableActivity
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class DataPlaceholderAdapter(private val data: RealmList<Note>, private val activity: TableActivity, private val columnSizes: ArrayList<Int>, private val scrollListener: RecyclerView.OnScrollListener): RecyclerView.Adapter<DataPlaceholderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapter = DataPlaceHolderItemAdapter(data[position]!!, columnSizes, activity)
        adapter.onEntryClickListener = object: DataPlaceHolderItemAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, positionOfItem: Int) {
                activity.processDataOnClick(view, position, positionOfItem)
            }
        }
        holder.row.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.row.layoutManager = layoutManager
        holder.row.addOnScrollListener(scrollListener)

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val row: RecyclerView = view.findViewById(R.id.rec_view)
    }

}