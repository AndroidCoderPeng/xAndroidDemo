package com.example.android.model

data class TimeDomainData(
    val timeAxis: DoubleArray,
    val amplitudes: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeDomainData

        if (!timeAxis.contentEquals(other.timeAxis)) return false
        if (!amplitudes.contentEquals(other.amplitudes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeAxis.contentHashCode()
        result = 31 * result + amplitudes.contentHashCode()
        return result
    }
}