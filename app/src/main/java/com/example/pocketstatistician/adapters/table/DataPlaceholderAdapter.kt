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
import com.example.pocketstatistician.convenience.getEmptyNote
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class DataPlaceholderAdapter(private val data: RealmList<Note>, private val activity: TableActivity, private val columnSizes: ArrayList<Int>): RecyclerView.Adapter<DataPlaceholderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.row.adapter = getAdapter(position)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.row.layoutManager = layoutManager

    }

    private fun getAdapter(position: Int): DataPlaceHolderItemAdapter {
        return if (position < itemCount-1) {
            val adapter = DataPlaceHolderItemAdapter(data[position]!!, columnSizes, activity)
            adapter.onEntryClickListener = object: DataPlaceHolderItemAdapter.OnEntryClickListener {
                override fun onEntryClick(view: View, positionOfItem: Int) {
                    activity.processDataOnClick(view, position, positionOfItem)
                }
            }
            adapter
        } else {
            DataPlaceHolderItemAdapter(getEmptyNote(columnSizes.size-1), columnSizes, activity)
        }

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val row: RecyclerView = view.findViewById(R.id.rec_view)
    }

}