package com.example.pocketstatistician.convenience

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pocketstatistician.Note
import com.example.pocketstatistician.R

fun isInteger(str: String?, context: Context): Boolean {
    if (str == null) {
        Toast.makeText(context,
            R.string.empty_string, Toast.LENGTH_SHORT).show()
        return false
    }
    val length = str.length
    if (length == 0) {
        return false
    }
    var i = 0
    while (i < length) {
        val c = str[i]
        if (c < '0' || c > '9') {
            Toast.makeText(context,
                R.string.not_int, Toast.LENGTH_SHORT).show()
            return false
        }
        i++
    }
    return true
}

fun show(where: Context, what: String) {
    Toast.makeText(where, what, Toast.LENGTH_SHORT).show()
}

fun log(input: String) {
    Log.d("Abrakadabra", input)
}

fun getEmptyNote(size: Int): Note {
    val emptyNote = Note()
    emptyNote.note.addAll(Array(size) { "" })
    return emptyNote
}

fun convertToPx(dps: Int, context: Context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}

/*class NoVariablesAlertDialog(): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.not_enough_variables)
            .setPositiveButton(
                R.string.to_variables_editor
            ) { dialog, which ->
                val intent = Intent(context, NewVariableFragment::class.java)
                startActivity(intent)
            }
            .setNegativeButton(
                R.string.to_main_activity
            ) { dialog, which ->
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }

        return builder.create()
    }
}*/

abstract class FragmentWithId(val id: Long): Fragment()