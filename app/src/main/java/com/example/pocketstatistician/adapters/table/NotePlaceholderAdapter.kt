package com.example.pocketstatistician.adapters.table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import io.realm.RealmList

abstract class NotePlaceholderAdapter(private val notes: RealmList<Note>, private val layoutSize: Int): RecyclerView.Adapter<NotePlaceholderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = LinearLayout.LayoutParams(layoutSize, layoutSize)
        holder.note.layoutParams = layoutParams

        if (position == itemCount - 1) {
            holder.note.text = "+"
            holder.note.setOnClickListener { onAddNoteButtonClick() }
            return
        }
        holder.note.text = (position+1).toString()
        holder.note.setOnClickListener { onNoteClick() }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val note: TextView = view.findViewById(R.id.recycler_text_view)
    }

    open fun onAddNoteButtonClick() {

    }

    open fun onNoteClick() {

    }
}