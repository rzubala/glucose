package com.zubala.rafal.glucose.domain

data class GlucoseData (var time: String = "", var result: Int = 0)

data class GlucoseDay (val onEmpty: GlucoseData, val breakfast: GlucoseData, val dinner: GlucoseData, val supper: GlucoseData) {
    companion object {
        fun empty() = GlucoseDay(GlucoseData(), GlucoseData(), GlucoseData(), GlucoseData())
    }
    fun setTime(part: Int, time: String) {
        when(part) {
            0 -> onEmpty.time = time
            1 -> breakfast.time = time
            2 -> dinner.time = time
            3 -> supper.time = time
        }
    }
    fun setResult(part: Int, resultStr: String) {
        val result = resultStr.toInt()
        when(part) {
            0 -> onEmpty.result = result
            1 -> breakfast.result = result
            2 -> dinner.result = result
            3 -> supper.result = result
        }
    }
}

fun List<List<Any>>.toGlucose(): GlucoseDay {
    val glucose = GlucoseDay.empty()
    if (isEmpty()) {
        return glucose
    }
    var isTime = true
    var cnt = 0
    for (row in this) {
        for (col in row) {
            when (isTime) {
                true -> glucose.setTime(cnt, col as String)
                else -> glucose.setResult(cnt, col as String)
            }
            isTime = !isTime
            if (isTime) {
                cnt++
            }
        }
        break
    }
    return glucose
}

