package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.R
import com.example.pocketstatistician.Variable
import com.example.pocketstatistician.adapters.VariantsAdapter
import com.example.pocketstatistician.convenience.isInteger
import io.realm.Realm
import io.realm.RealmList

class NewVariableFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_variables_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)  {
        super.onActivityCreated(savedInstanceState)

        val variableTypeSpinner = view!!.findViewById<Spinner>(R.id.variable_type_spinner)
        val classified = view!!.findViewById<LinearLayout>(R.id.classified)

        variableTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    classified.visibility = View.GONE
                    return
                }
                classified.visibility = View.VISIBLE
            }
        }

        view!!.findViewById<Button>(R.id.ok_count_button).setOnClickListener { onCountButtonClick() }
        view!!.findViewById<Button>(R.id.createStatistics).setOnClickListener { onSaveButtonClick() }
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
        val variableType = view!!.findViewById<Spinner>(R.id.variable_type_spinner).selectedItemPosition
        val variantsRecView = view!!.findViewById<RecyclerView>(R.id.list_of_variants)
        val variantsList = RealmList<String>()
        for (i in 0 until variantsRecView.size) {
            variantsList.add((variantsRecView.findViewHolderForAdapterPosition(i) as VariantsAdapter.VariantsViewHolder).variantValue.text.toString())
        }

        if (dataIsNotCorrect(variableName, variableType, variantsList)) {
            Toast.makeText(activity,
                R.string.data_is_not_correct, Toast.LENGTH_SHORT).show()
            return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(
                Variable(
                    variableName,
                    variableType,
                    variantsList
                )
            )
        }
    }

    private fun dataIsNotCorrect(name: String, type: Int, variantsList: RealmList<String>): Boolean {
        if (name.isEmpty()) {
            return true
        }
        if (type == 1 && variantsList.size == 0) {
            return true
        }

        for (item in variantsList) {
            if (item.isEmpty()) return true
        }
        return false
    }
}