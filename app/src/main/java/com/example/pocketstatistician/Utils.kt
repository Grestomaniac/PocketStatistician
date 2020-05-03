package com.example.pocketstatistician

fun isInteger(str: String?): Boolean {
    if (str == null) {
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
            return false
        }
        i++
    }
    return true
}