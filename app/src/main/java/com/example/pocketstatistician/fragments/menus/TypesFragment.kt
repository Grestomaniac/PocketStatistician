package com.example.pocketstatistician.fragments.menus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.activities.TypeMenuActivity
import com.example.pocketstatistician.adapters.menu.TypeItemAdapter
import io.realm.RealmResults

class TypesFragment(val types: RealmResults<Type>): Fragment() {

    lateinit var label: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: TypeItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.menu_fragment_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        label = view!!.findViewById(R.id.label)

        recyclerView = view!!.findViewById(R.id.list_of_items)

        adapter = TypeItemAdapter(types)
        adapter.onEntryClickListener = object : TypeItemAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                val intent = Intent(activity, TypeMenuActivity::class.java)
                intent.putExtra("type_number", position)
                startActivity(intent)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        label.text = getString(R.string.statistics_quantity, types.size)
    }
}