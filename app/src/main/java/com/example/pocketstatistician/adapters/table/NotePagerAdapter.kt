package com.example.pocketstatistician.adapters.table

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.fragments.NoteFragment
import com.example.pocketstatistician.fragments.table.DataFragment
import io.realm.RealmList

class NotePagerAdapter(fragmentActivity: FragmentActivity, statistic: Statistic): FragmentStateAdapter(fragmentActivity) {

    private val variableTypes: RealmList<Type> = statistic.variable_types
    private val data: RealmList<Note> = statistic.data
    private val variableNames: RealmList<String> = statistic.variable_names
    private val fragmentList = ArrayList<NoteFragment>()
    init {
        for (note in data) addFragment(note)
        addEmptyFragment()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): NoteFragment {
        return fragmentList[position]
    }

    fun removeFragment(position: Int) {
        fragmentList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun addFragment(note: Note) {
        val noteFragment = NoteFragment(variableTypes, note, variableNames)
        fragmentList.add(noteFragment)
        notifyItemInserted(fragmentList.lastIndex)
    }

    fun addEmptyFragment() {
        val noteData = RealmList<String>()
        noteData.addAll(Array(variableTypes.size) {""})

        addFragment(Note(noteData))
    }

    fun getNoteData(position: Int): Note {
        return fragmentList[position].getData()
    }

    fun clearData(position: Int) {
        fragmentList[position].clearData()
    }
}