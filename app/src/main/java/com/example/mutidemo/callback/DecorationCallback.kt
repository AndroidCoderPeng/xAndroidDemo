package com.example.mutidemo.callback

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/27 23:00
 */
interface DecorationCallback {
    fun getGroupTag(position: Int): Long
    fun getGroupFirstLetter(position: Int): String
}