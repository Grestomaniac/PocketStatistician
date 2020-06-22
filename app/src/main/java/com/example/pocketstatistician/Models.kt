package com.example.pocketstatistician

import io.realm.RealmList
import io.realm.RealmObject

//TODO(add variants and data maxSizes for faster calculations of table size)
open class Type(
    var name: String = "",
    var type: String  = "",
    var variants: RealmList<String> = RealmList(),
    var usedVariables: RealmList<Variable> = RealmList()
): RealmObject() {
    fun delete() {
        for (variable in usedVariables) {
            variable.remove()
            variable.deleteFromRealm()
        }
        deleteFromRealm()
    }
}

open class Variable(
    var name: String = "",
    var type: Type? = Type(),
    var question: String = "",
    var statistic: Statistic? = null,
    var positionInStatistic: Int = -1
): RealmObject() {
    fun clearDataFromStatistic() {
        statistic!!.clearVariableDataAt(positionInStatistic)
    }
    fun remove() {
        statistic!!.removeVariableDataAt(positionInStatistic)
        type!!.usedVariables.remove(this)
    }
    fun deleteWithStatistic() {
        type!!.usedVariables.remove(this)
        deleteFromRealm()
    }
}

open class Statistic(
    var name: String = "",
    var variables: RealmList<Variable> = RealmList(),
    var data: RealmList<Note> = RealmList()
): RealmObject() {
    fun removeVariableDataAt(pos: Int) {
        for (note in data) {
            note.note.removeAt(pos)
        }
    }
    fun clearVariableDataAt(pos: Int) {
        for (note in data) {
            note.note[pos] = ""
        }
    }
    fun addDataFieldForVariable(count: Int = 1) {
        for (note in data) {
            note.note.addAll(Array(count) { "" })
        }
    }
    fun delete() {
        for (note in data) note.deleteFromRealm()
        for (variable in variables) variable.deleteWithStatistic()
        deleteFromRealm()
    }
}

open class Note(
    var note: RealmList<String> = RealmList()
): RealmObject()