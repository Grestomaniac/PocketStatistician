package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.adapters.NoteAdapter
import com.example.pocketstatistician.convenience.NoteDialog
import io.realm.RealmList

class NoteFragment(val variableTypes: RealmList<Type>, val note: Note, val variableNames: RealmList<String>): Fragment() {

    lateinit var items: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.note_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        items = view!!.findViewById(R.id.items)
        val adapter = NoteAdapter(variableTypes, variableNames, note, context!!)

        items.adapter = adapter
        items.layoutManager = LinearLayoutManager(context)

    }

    fun getData(): Note {
        val data = RealmList<String>()
        for (i in 0 until items.childCount) {
            val holder = items.findViewHolderForAdapterPosition(i) as NoteAdapter.ViewHolder
            data.add(holder.getValue())
        }
        return Note(data)
    }

    fun clearData() {
        for (i in 0 until items.childCount) {
            val holder = items.findViewHolderForAdapterPosition(i) as NoteAdapter.ViewHolder
            holder.clear()
        }
    }
}