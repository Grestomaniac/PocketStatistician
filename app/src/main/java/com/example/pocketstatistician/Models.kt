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
): RealmObject()

open class Statistic(
    @PrimaryKey @Required
    var name: String = "",
    var variablesCount: Int = 0,
    var variables: RealmList<Variable> = RealmList()
): RealmObject()