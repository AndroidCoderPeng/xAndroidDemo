package com.example.android.model

data class TimeDomainData(
    val timeAxis: DoubleArray,
    val amplitude: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeDomainData

        if (!timeAxis.contentEquals(other.timeAxis)) return false
        if (!amplitude.contentEquals(other.amplitude)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeAxis.contentHashCode()
        result = 31 * result + amplitude.contentHashCode()
        return result
    }
}