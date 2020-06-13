package com.example.pocketstatistician.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.VariantAdapter
import com.example.pocketstatistician.convenience.isInteger
import com.example.pocketstatistician.convenience.show
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class TypeEditorActivity: AppCompatActivity() {
    private lateinit var variantsCount: EditText
    private lateinit var typeList: RealmResults<Type>
    lateinit var listOfVariants: RecyclerView
    lateinit var variantAdapter: VariantAdapter
    lateinit var typeName: EditText
    lateinit var variantPlaceholder: LinearLayout
    lateinit var quantityChooser: LinearLayout

    private val variants = ArrayList<VariantData>()
    private var typePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistic_editor_layout)

        variantsCount = findViewById(R.id.variant_count)
        typeName = findViewById(R.id.type_name)
        listOfVariants = findViewById(R.id.list_of_variants)
        variantPlaceholder = findViewById(R.id.variant_placeholder)
        quantityChooser = findViewById(R.id.quantity_chooser)

        typeList = (application as Application).types
        typePosition = intent.getIntExtra("", -1)

        if (typePosition != -1) {
            val type = typeList[typePosition]
            typeName.setText(type!!.name)

            val oldVariants = type.variants
            variantsCount.setText(oldVariants.size.toString())

            variants.addAll(oldVariants.mapTo(ArrayList(), { VariantData(it, true) }))
            variantPlaceholder.visibility = View.VISIBLE
        }

        variantAdapter = VariantAdapter(variants, this)

        variantAdapter.onEntryClickListener = object: VariantAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {

            }
        }

        listOfVariants.adapter = variantAdapter
        listOfVariants.layoutManager = LinearLayoutManager(this)

        listOfVariants.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
            }

            override fun onChildViewAttachedToWindow(view: View) {
            }
        })
    }

    fun onCountButtonClick(v: View) {

        val count = variantsCount.text.toString()
        if (!isInteger(count, this)) {
            show(this, getString(R.string.not_int))
            return
        }
        val countInt = count.toInt()
        if (countInt < 2) {
            show(this, getString(R.string.not_enough_variables))
            return
        }

        quantityChooser.visibility = View.GONE
        variantPlaceholder.visibility = View.VISIBLE
    }

    fun onSaveButtonClick(v: View) {
        val name = typeName.text.toString()
        if (name.isBlank()) {
            show(this, "Введите название статистики")
            return
        }

        if (variants.size < 2) {
            show(this, "Нужны как минимум 2 переменные")
            return
        }

        if (typePosition != -1) updateType(name)
        else createType(name)
    }

    private fun updateType(name: String) {
        val type = typeList[typePosition]!!
        val statOldVars = type.variants
        val blackList = IntArray(statOldVars.size).toMutableList()

        for (i in 0 until variants.size) {
            if (!checkIfDataIsCorrect(i)) return
            val variant = variants[i]
            if (variant.isDefault) {
                val defaultPos = statOldVars.indexOf(variant.variant)
                if (defaultPos != -1) blackList.remove(defaultPos)
            }
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            for (i in blackList) type.variants.removeAt(i)
            type.name = name
            type.variants = variants.mapTo(RealmList(), { it.variant })
        }
        show(this, "$name обновлён")
    }

    private fun createType(name: String) {

        for (i in 0 until variants.size) {
            if (!checkIfDataIsCorrect(i)) return
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Type(name, "classified", variants.mapTo(RealmList(), { it.variant })))
        }
        show(this, "$name обновлён")
    }

    private fun checkIfDataIsCorrect(position: Int): Boolean {
        val variableName = variants[position].variant

        if (variableName.isBlank()) {
            show(this, getString(R.string.empty_string))
            return false
        }

        if (!variantsHaveUniqueNames()) {
            show(this, getString(R.string.name_is_not_unique))
            return false
        }
        return true
    }

    private fun variantsHaveUniqueNames(): Boolean {
        val variantNames = variants.mapTo(ArrayList(), { it.variant })
        for (variant in variantNames) {
            if (containsTwice(variant, variantNames)) return false
            else variantNames.remove(variant)
        }
        return true
    }

    private fun containsTwice(name: String, names: ArrayList<String>): Boolean {
        var counter = 0
        for (i in names) {
            if (i == name) counter++
            if (counter > 1) return true
        }
        return false
    }

    class VariantData(var variant: String = "", var isDefault: Boolean = false)
}