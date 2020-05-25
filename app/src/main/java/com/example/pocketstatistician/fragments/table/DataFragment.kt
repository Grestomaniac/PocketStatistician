package com.example.pocketstatistician.fragments.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.MainActivity
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.adapters.TableAdapter
import com.example.pocketstatistician.convenience.NoteDialog
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class DataFragment(val variableTypes: RealmList<Type>, val data: RealmList<Note>, val variableNames: RealmList<String>, val dataOffset: Int, val dataSize: Int): Fragment() {

    val dataId = dataOffset.toLong()

    private lateinit var mainActivity: MainActivity
    lateinit var notes: GridView
    lateinit var dataSet: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.data_and_note_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity
        notes = view!!.findViewById(R.id.note_container)
        dataSet = view!!.findViewById(R.id.data_container)

        notes.numColumns = mainActivity.columnCount + 1

        val noteArray = ArrayList<String>()
        noteArray.add("Номер записи")
        noteArray.addAll((dataOffset+1..dataOffset+dataSize).map { it.toString() })
        notes.adapter = ArrayAdapter(context!!, R.layout.just_text_view, R.id.recycler_text_view, noteArray)
        notes.setOnItemClickListener { parent, view, position, id ->
            val dialog = NoteDialog(this, position)
            dialog.show(fragmentManager!!, "0")
        }

        allocateData()

    }

    private fun getNote(parent: ViewGroup, position: Int): View {
        val noteView = LayoutInflater.from(parent.context).inflate(R.layout.just_text_view, parent, false) as TextView
        noteView.text = (dataOffset + position).toString()
        return noteView
    }

    private fun allocateData() {
        val slicedData = RealmList<String>()

        for (i in 0 until variableTypes.size)
            for (j in dataOffset until dataOffset + dataSize) {
                slicedData.add(data[j]!!.note[i])
            }

        dataSet.adapter = TableAdapter(variableTypes, slicedData, variableNames, context!!, mainActivity.columnCount)
        dataSet.layoutManager = LinearLayoutManager(context!!)
    }

    fun deleteNote() {

    }

}