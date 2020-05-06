package com.example.pocketstatistician

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.security.AccessControlContext

fun isInteger(str: String?, context: Context): Boolean {
    if (str == null) {
        Toast.makeText(context, R.string.empty_string, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, R.string.not_int, Toast.LENGTH_SHORT).show()
            return false
        }
        i++
    }
    return true
}

class NoVariablesAlertDialog(context: Context): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.not_enough_variables)
            .setPositiveButton(R.string.to_variables_editor
            ) { dialog, which ->
                val intent = Intent(context, NewVariableActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton(R.string.to_variables_editor
            ) { dialog, which ->
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }

        return builder.create()
    }
}