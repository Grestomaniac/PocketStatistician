package com.example.pocketstatistician.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Type
import com.example.pocketstatistician.adapters.VariantsAdapter
import com.example.pocketstatistician.convenience.FragmentWithId
import com.example.pocketstatistician.convenience.isInteger
import com.example.pocketstatistician.convenience.show
import io.realm.Realm
import io.realm.RealmList

class NewTypeFragment(id: Long): FragmentWithId(id) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_type_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        view!!.findViewById<Button>(R.id.ok_count_button).setOnClickListener { onCountButtonClick() }
        view!!.findViewById<Button>(R.id.createVariable).setOnClickListener { onSaveButtonClick() }
    }


    private fun onCountButtonClick() {
        val variablesCount = view!!.findViewById<EditText>(R.id.variants_count)

        val count = variablesCount.text.toString()
        if (!isInteger(count, activity!!)) return
        val adapter =
            VariantsAdapter(count.toInt())

        val variantsBox = view!!.findViewById<LinearLayout>(R.id.var_rec_view)
        val variantsRecyclerView = view!!.findViewById<RecyclerView>(R.id.list_of_variants)

        variantsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        variantsRecyclerView.adapter = adapter
        variantsRecyclerView.layoutManager = LinearLayoutManager(activity)
        variantsBox.visibility = View.VISIBLE
    }

    private fun onSaveButtonClick() {
        val variableName = view!!.findViewById<EditText>(R.id.variable_name).text.toString()
        val variantsRecView = view!!.findViewById<RecyclerView>(R.id.list_of_variants)
        val variantsList = RealmList<String>()
        for (i in 0 until variantsRecView.size) {
            variantsList.add((variantsRecView.findViewHolderForAdapterPosition(i) as VariantsAdapter.VariantsViewHolder).variantValue.text.toString())
        }

        if (dataIsNotCorrect(variableName, variantsList)) {
            Toast.makeText(activity,
                R.string.data_is_not_correct, Toast.LENGTH_SHORT).show()
            return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(
                Type(
                    variableName,
                    "classified",
                    variantsList
                )
            )
        }
        show(activity!!, "$variableName создан")
    }

    private fun dataIsNotCorrect(name: String, variantsList: RealmList<String>): Boolean {
        if (name.isEmpty()) {
            return true
        }
        if (variantsList.size == 0) {
            return true
        }

        for (item in variantsList) {
            if (item.isEmpty()) return true
        }
        return false
    }
}