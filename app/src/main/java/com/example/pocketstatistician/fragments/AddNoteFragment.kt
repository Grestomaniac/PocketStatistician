/*
package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.activities.MainActivity
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Statistic
import com.example.pocketstatistician.adapters.AddNoteAdapter
import com.example.pocketstatistician.convenience.show
import io.realm.Realm
import io.realm.RealmList

class AddNoteFragment(private val statistic: Statistic): Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var recView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_note_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity

        view!!.findViewById<TextView>(R.id.statistic_name).text = statistic.name

        recView = view!!.findViewById(R.id.variables)
        val adapter = AddNoteAdapter(statistic, mainActivity)

        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(context)

        view!!.findViewById<Button>(R.id.save_button).setOnClickListener { save() }
    }

    private fun save() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction(Realm.Transaction { bgRealm ->
            val oldStatistic = bgRealm.where(Statistic::class.java).equalTo("name", statistic.name).findFirst()!!

            val values = getValues()
            if (values == null) {
                show(mainActivity, "Упс, вы что-то упустили")
                return@Transaction
            }
            oldStatistic.data.add(getValues())
        })
        show(activity!!, "Запись добавлена")
    }

    private fun getValues(): Note? {
        val note: RealmList<String> = RealmList()
        for (i in 0 until recView.childCount) {
            val value = (recView.findViewHolderForAdapterPosition(i) as AddNoteAdapter.ViewHolder).getValue()
            if (value.isBlank()) return null
            note.add(value)
        }
        return Note(note)
    }
}*/
