package com.example.pocketstatistician

import io.realm.RealmList
import kotlin.math.log10
import kotlin.math.pow

class Analyzer(private val statistic: Statistic) {

    lateinit var analyzeAbleData: ArrayList<DataForVariable>

    fun analyzeAt(position: Int) {
        analyzeAbleData[position].analyze()
    }

    fun initialize() {
        analyzeAbleData = transpose(statistic.data)
    }

    private fun transpose(data: RealmList<Note>): ArrayList<DataForVariable> {
        val dataSet = statistic.variables.mapTo(ArrayList(), { resolveType(it) })
        for (i in 0 until data.size) {
            for (j in 0 until dataSet.size) {
                dataSet[j].addValue(data[i]!!.note[j]!!)
            }
        }
        dataSet.forEach { it.putInOrder() }
        return dataSet
    }

    private fun resolveType(variable: Variable): DataForVariable {
        return when (variable.type!!.type) {
            "int" -> IntDataForVariable(variable)
            "double" -> DoubleDataForVariable(variable)
            else -> ClassifiedDataForVariable(variable)
        }
    }

    abstract class DataForVariable(val variable: Variable) {
        val varType: Type = variable.type!!

        var minimum: Double? = null
        var maximum: Double? = null
        var modes: ArrayList<String> = ArrayList()
        var averageArithmetic: Double? = null
        var median: Double? = null
        var selectionSize = 0

        var center: Double? = null
        var dispersion: Double? = null
        var deviation: Double? = null

        var isDiscrete: Boolean = false
        var intervals: Int = 0

        abstract fun addValue(value: String)
        open fun putInOrder() {}
        abstract fun discreteAnalysis()
        open fun continuousAnalysis() {}
        open fun defineRange() {}

        fun analyze() {
            if (isDiscrete) discreteAnalysis()
            else continuousAnalysis()
        }

        fun makeDiscrete() {
            isDiscrete = true
            intervals = optimalIntervalCount()
        }

        fun makeContinuous() {
            isDiscrete = false
            intervals = 0
        }

        fun defineCenter(type: Int = 0) { //0 - average, 1 - median, 2 - mode
            center = when (type) {
                0 -> averageArithmetic
                else -> median
            }
        }

        fun optimalIntervalCount(): Int {
            return (1 + 3.322*log10(selectionSize.toDouble())).toInt()
        }

    }

    class ClassifiedDataForVariable(variable: Variable, val objectValues: ArrayList<String> = ArrayList()): DataForVariable(variable) {
        var frequencyTable: ArrayList<Frequency> = varType.variants.mapTo( ArrayList(), { Frequency(it) })

        init {
            isDiscrete = true
        }

        override fun addValue(value: String) {
            objectValues.add(value)
            selectionSize++
        }

        override fun discreteAnalysis() {
            for (value in objectValues) {
                val variantIndex = varType.variants.indexOf(value)
                frequencyTable[variantIndex].frequency++
            }
            frequencyTable.sortBy { it.frequency }

            if (frequencyTable[0].frequency != frequencyTable.last().frequency) {
                var k = frequencyTable.lastIndex
                modes.add(frequencyTable[k].variant)

                while (frequencyTable[k].frequency == frequencyTable[k-1].frequency) {
                    modes.add(frequencyTable[k-1].variant)
                    k--
                }
            }
        }

    }

    class IntDataForVariable(variable: Variable, val selection: ArrayList<Int> = ArrayList()): DataForVariable(variable) {
        var frequencyTable: ArrayList<Slice> = ArrayList()

        override fun addValue(value: String) {
            selection.add(value.toInt())
            selectionSize++
        }

        override fun putInOrder() {
            selection.sort()
        }

        override fun discreteAnalysis() {
            val min = minimum!!

            val range = min - maximum!!
            val step: Double = range.div(intervals)

            var localMin = min
            var localMax = min + step
            val slice: ArrayList<Int> = ArrayList()
            for (i in 0 until selection.size) {
                if (selection[i] < localMax) slice.add(selection[i])
                else {
                    val newSlice = IntSlice(localMin, localMax)
                    newSlice.addValues(slice, selectionSize)

                    frequencyTable.add(newSlice)
                    localMin = localMax
                    localMax += step
                }
            }
        }

        override fun defineRange() {
            minimum = selection[0].toDouble()
            maximum = selection.last().toDouble()
        }

        override fun continuousAnalysis() {
            val medium: Int = selectionSize / 2
            median = if (selectionSize % 2 == 0) {
                (selection[medium] + selection[medium+1]).toDouble().div(selectionSize)
            }
            else selection[medium].toDouble()

            averageArithmetic = selection.average()

            var summary = .0
            for (i in 0 until selectionSize) {
                summary += (center!! - selection[i]).pow(2)
            }
            dispersion = summary
            deviation = summary.pow(0.5)
        }
    }

    class DoubleDataForVariable(variable: Variable, val selection: ArrayList<Double> = ArrayList()): DataForVariable(variable) {
        var frequencyTable: ArrayList<Slice> = ArrayList()

        override fun addValue(value: String) {
            selection.add(value.toDouble())
            selectionSize++
        }

        override fun putInOrder() {
            selection.sort()
        }

        override fun discreteAnalysis() {
            val min = minimum!!

            val range = min - maximum!!
            val step: Double = range.div(intervals)

            var localMin = min
            var localMax = min + step
            val slice: ArrayList<Double> = ArrayList()
            for (i in 0 until selection.size) {
                if (selection[i] < localMax) slice.add(selection[i])
                else {
                    val newSlice = DoubleSlice(localMin, localMax)
                    newSlice.addValues(slice, selectionSize)

                    frequencyTable.add(newSlice)
                    localMin = localMax
                    localMax += step
                }
            }
        }

        override fun defineRange() {
            minimum = selection[0]
            maximum = selection.last().toDouble()
        }

        override fun continuousAnalysis() {
            minimum = selection[0]
            maximum = selection.last().toDouble()

            val size = selection.size
            val medium: Int = size / 2
            median = if (size % 2 == 0) {
                (selection[medium] + selection[medium+1]).div(size)
            } else selection[medium]

            averageArithmetic = selection.average()
        }
    }

    abstract class Slice(val min: Double, val max: Double) {
        val medium: Double = (min + max) / 2
        var relativeFreq: Double = .0

        abstract fun getFreq(): Int
    }

    class IntSlice(min: Double, max: Double, val values: ArrayList<Int> = ArrayList()): Slice(min, max) {
        fun addValues(input: ArrayList<Int>, selectionSize: Int) {
            values.addAll(input)
            relativeFreq = values.size.toDouble().div(selectionSize)
        }

        override fun getFreq(): Int {
            return values.size
        }
    }
    class DoubleSlice(min: Double, max: Double, val values: ArrayList<Double> = ArrayList()): Slice(min, max) {
        fun addValues(input: ArrayList<Double>, selectionSize: Int) {
            values.addAll(input)
            relativeFreq = values.size.toDouble().div(selectionSize)
        }

        override fun getFreq(): Int {
            return values.size
        }
    }


    class Frequency(val variant: String, var frequency: Int = 0)

}