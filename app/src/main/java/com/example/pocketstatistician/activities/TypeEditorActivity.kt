package com.example.pocketstatistician.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.VariantAdapter
import com.example.pocketstatistician.convenience.YouChooseDialog
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
        setContentView(R.layout.type_editor_layout)

        variantsCount = findViewById(R.id.variant_count)
        typeName = findViewById(R.id.type_name)
        listOfVariants = findViewById(R.id.list_of_variants)
        variantPlaceholder = findViewById(R.id.variant_placeholder)
        quantityChooser = findViewById(R.id.quantity_chooser)

        typeList = (application as Application).types
        typePosition = intent.getIntExtra("type_position", -1)

        if (typePosition != -1) {
            val type = typeList[typePosition]
            typeName.setText(type!!.name)
            title = type.name

            val oldVariants = type.variants
            variantsCount.setText(oldVariants.size.toString())

            variants.addAll(oldVariants.mapTo(ArrayList(), { VariantData(it, true) }))
            variantPlaceholder.visibility = View.VISIBLE
            quantityChooser.visibility = View.GONE
        }

        variantAdapter = VariantAdapter(variants, this)

        variantAdapter.onEntryClickListener = object: VariantAdapter.OnEntryClickListener {
            override fun onEntryClick(view: View, position: Int) {
                variants.removeAt(position)
                variantAdapter.notifyItemRemoved(position)
                variantAdapter.notifyItemRangeChanged(position, variantAdapter.itemCount - position)
            }
        }

        listOfVariants.adapter = variantAdapter
        listOfVariants.layoutManager = LinearLayoutManager(this)

        variantsCount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                currentFocus?.clearFocus()
                onCountButtonClick(v)
            }
            false
        }
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

        for (i in 0 until countInt) variants.add(VariantData())
        variantAdapter.notifyItemRangeInserted(variantAdapter.itemCount, countInt)

        quantityChooser.visibility = View.GONE
        variantPlaceholder.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    fun onSaveButtonClick() {
        val name = typeName.text.toString()
        if (name.isBlank()) {
            show(this, getString(R.string.no_name))
            return
        }

        if (variants.size < 2) {
            show(this, getString(R.string.not_enough_variants))
            return
        }
        if (!ifVariantsNotBlank()) return
        if (!variantsHaveUniqueNames()) return

        if (typePosition != -1) updateType(name)
        else createType(name)

        title = name
        variants.forEach { variant -> variant.isDefault = true }
        variantAdapter.notifyDataSetChanged()
    }

    fun onAddVariantButtonClick(v: View) {
        variants.add(VariantData())
        variantAdapter.notifyItemInserted(variantAdapter.itemCount)
    }

    private fun updateType(name: String) {
        val type = typeList[typePosition]!!
        val statOldVars = type.variants
        val blackList = IntArray(statOldVars.size).toMutableList()

        for (i in 0 until variants.size) {
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
        show(this, getString(R.string.updated, name))
    }

    private fun createType(name: String) {

        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.copyToRealm(Type(name, "classified", variants.mapTo(RealmList(), { it.variant })))
        }
        show(this, getString(R.string.created, name))
        typePosition = typeList.size-1
    }

    private fun ifVariantsNotBlank(): Boolean {
        for (variant in variants) {
            if (variant.variant.isBlank()) {
                show(this, getString(R.string.empty_string))
                return false
            }
        }
        return true
    }

    private fun variantsHaveUniqueNames(): Boolean {
        for (i in 0 until variants.size-1) {
            if (containsTwice(variants[i].variant, i+1)) {
                show(this, getString(R.string.name_is_not_unique))
                return false
            }
        }
        return true
    }

    private fun containsTwice(name: String, pointer: Int): Boolean {
        for (i in pointer until variants.size) {
            if (variants[i].variant == name) return true
        }
        return false
    }

    class VariantData(var variant: String = "", var isDefault: Boolean = false, var haveUniqueName: Boolean = false)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            true
        }

        R.id.action_save -> {
            currentFocus?.clearFocus()
            onSaveButtonClick()
            true
        }

        R.id.action_new -> {
            val dialog = YouChooseDialog(getString(R.string.not_saved_data_will_be_deleted), getString(R.string.yes), getString(R.string.no))
            dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
                override fun onPositiveButtonClick() {
                    val newIntent = Intent(this@TypeEditorActivity, TypeEditorActivity::class.java)
                    finish()
                    startActivity(newIntent)
                }

                override fun onNegativeButtonClick() {
                }
            }
            dialog.show(supportFragmentManager, "create_new")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}