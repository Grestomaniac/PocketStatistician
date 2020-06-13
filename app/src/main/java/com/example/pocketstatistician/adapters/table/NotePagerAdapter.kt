package com.example.pocketstatistician.adapters.table

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.fragments.NoteFragment
import io.realm.RealmList

class NotePagerAdapter(fragmentActivity: FragmentActivity, statistic: Statistic): FragmentStateAdapter(fragmentActivity) {

    private val variables: RealmList<Variable> = statistic.variables
    private val data: RealmList<Note> = statistic.data
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
        val noteFragment = NoteFragment(variables, note)
        fragmentList.add(noteFragment)
        notifyItemInserted(fragmentList.lastIndex)
    }

    fun addEmptyFragment() {
        val noteData = RealmList<String>()
        noteData.addAll(Array(variables.size) {""})

        addFragment(Note(noteData))
    }

    fun getNoteData(position: Int): Note {
        return fragmentList[position].getData()
    }

    fun clearData(position: Int) {
        fragmentList[position].clearData()
    }
}