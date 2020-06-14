package com.example.pocketstatistician.adapters.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import io.realm.RealmResults

class TypeItemAdapter(val typeList:RealmResults<Type>): RecyclerView.Adapter<TypeItemAdapter.TypeViewHolder>() {

    var onEntryClickListener: OnEntryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_statitstic_item_layout, parent, false)
        return TypeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return typeList.size
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.name.text = typeList[position]!!.name
        holder.count.text = holder.name.context.getString(R.string.variant_quantity, typeList[position]!!.variants.size)
    }

    inner class TypeViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }
        val name: TextView = view.findViewById(R.id.name)
        val count: TextView = view.findViewById(R.id.item_count)

        override fun onClick(v: View?) {
            onEntryClickListener?.onEntryClick(v!!, layoutPosition)
        }
    }

    interface OnEntryClickListener {
        fun onEntryClick(view: View, position: Int)
    }

}