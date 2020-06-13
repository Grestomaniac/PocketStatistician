package com.example.pocketstatistician

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.lang.reflect.Array

open class Type(
    @PrimaryKey @Required
    var name: String = "",
    var type: String  = "",
    var variants: RealmList<String> = RealmList()
): RealmObject()

open class Variable(
    var name: String = "",
    var type: Type? = Type(),
    var question: String = ""
): RealmObject()

open class Statistic(
    @PrimaryKey @Required
    var name: String = "",
    var variables: RealmList<Variable> = RealmList(),
    var data: RealmList<Note> = RealmList()
): RealmObject() {
    fun removeVariableDataAt(pos: Int) {
        for (note in data) {
            note.note.removeAt(pos)
        }
    }
    fun addDataFieldForVariable(count: Int = 1) {
        for (note in data) {
            note.note.addAll(Array(count) { "" })
        }
    }
}

open class Note(
    var note: RealmList<String> = RealmList()
): RealmObject()