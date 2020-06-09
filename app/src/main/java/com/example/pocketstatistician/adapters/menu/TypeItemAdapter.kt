package com.example.pocketstatistician.adapters.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import io.realm.RealmResults

class TypeItemAdapter(val typeList:RealmResults<Type>): RecyclerView.Adapter<TypeItemAdapter.TypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item_layout, parent, false)
        return TypeViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class TypeViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

}