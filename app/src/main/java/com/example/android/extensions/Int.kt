package com.example.android.extensions

fun Int.isPowerOfTwo(): Boolean {
    return this > 0 && (this and (this - 1)) == 0
}

fun Int.calculateWeights(): DoubleArray {
    val len = 1 + this * 2
    val end = len - 1
    val radiusF = this.toDouble()
    val weights = DoubleArray(len)

    // 先把右边的权重算出来
    for (i in 0..this) {
        weights[this + i] = (i / radiusF).gaussian()
    }

    // 把右边的权重拷贝到左边
    for (i in 0 until this) {
        weights[i] = weights[end - i]
    }

    val total = weights.sum()
    for (i in 0 until len) {
        weights[i] /= total
    }

    return weights
}