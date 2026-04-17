package com.example.android.model

data class FrequencyDomainData(
    val frequencies: DoubleArray,
    val magnitudes: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FrequencyDomainData

        if (!frequencies.contentEquals(other.frequencies)) return false
        if (!magnitudes.contentEquals(other.magnitudes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = frequencies.contentHashCode()
        result = 31 * result + magnitudes.contentHashCode()
        return result
    }
}
