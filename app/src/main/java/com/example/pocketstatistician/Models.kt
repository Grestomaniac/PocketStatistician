package com.example.pocketstatistician

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Type(
    @PrimaryKey @Required
    var name: String = "",
    var type: String  = "",
    var variants: RealmList<String> = RealmList()
): RealmObject()

open class Statistic(
    @PrimaryKey @Required
    var name: String = "",
    var variable_names: RealmList<String> = RealmList(),
    var variable_types: RealmList<Type> = RealmList(),
    var data: RealmList<Note> = RealmList()
): RealmObject()

open class Note(
    var note: RealmList<String> = RealmList()
): RealmObject()