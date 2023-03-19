package com.example.mutidemo.extensions

/**
 * ArrayList扩展方法
 */

fun addAll(vararg args: String): ArrayList<String> {
    val result = ArrayList<String>()
    args.forEach {
        result.add(it)
    }
    return result
}