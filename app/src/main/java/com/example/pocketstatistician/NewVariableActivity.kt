package com.example.pocketstatistician

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList

class NewVariableActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_variables_layout)

        val variableTypeSpinner = findViewById<Spinner>(R.id.variable_type_spinner)
        val classified = findViewById<LinearLayout>(R.id.classified)

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
    }


    fun onCountButtonClick(view: View) {
        val variablesCount = findViewById<EditText>(R.id.variants_count)

        val count = variablesCount.text.toString()
        if (!isInteger(count, this)) return
        val adapter = VariantsAdapter(count.toInt())

        val variantsBox = findViewById<LinearLayout>(R.id.var_rec_view)
        val variantsRecyclerView = findViewById<RecyclerView>(R.id.list_of_variants)

        variantsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        variantsRecyclerView.adapter = adapter
        variantsRecyclerView.layoutManager = LinearLayoutManager(this)
        variantsBox.visibility = View.VISIBLE
    }

    fun onSaveButtonClick(view: View) {
        val variableName = findViewById<EditText>(R.id.variable_name).text.toString()
        val variableType = findViewById<Spinner>(R.id.variable_type_spinner).selectedItemPosition
        val variantsRecView = findViewById<RecyclerView>(R.id.list_of_variants)
        val variantsList = RealmList<String>()
        for (i in 0 until variantsRecView.size) {
            variantsList.add((variantsRecView.findViewHolderForAdapterPosition(i) as VariantsAdapter.VariantsViewHolder).variantValue.text.toString())
        }

        if (dataIsNotCorrect(variableName, variableType, variantsList)) {
            Toast.makeText(this, R.string.data_is_not_correct, Toast.LENGTH_SHORT).show()
            return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Variable(
                variableName,
                variableType,
                variantsList
            ))
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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