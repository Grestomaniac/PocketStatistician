package com.example.pocketstatistician

import android.content.Context
import android.widget.Toast
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