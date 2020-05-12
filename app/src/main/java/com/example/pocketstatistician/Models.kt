package com.example.pocketstatistician

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Variable(
    @PrimaryKey @Required
    var name: String = "",
    var type: Int = 0,
    var variants: RealmList<String> = RealmList()
): RealmObject() {
    override fun equals(other: Any?): Boolean {
        return (this.name == (other as String))
    }
}

open class Statistic(
    @PrimaryKey @Required
    var name: String = "",
    var variable_names: RealmList<String> = RealmList(),
    var variable_types: RealmList<Variable> = RealmList()
): RealmObject()