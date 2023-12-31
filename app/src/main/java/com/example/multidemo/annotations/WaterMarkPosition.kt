package com.example.multidemo.annotations

@Retention(AnnotationRetention.SOURCE)
annotation class WaterMarkPosition {
    companion object {
        /**
         * 左上
         */
        const val LEFT_TOP = 1

        /**
         * 右上
         */
        const val RIGHT_TOP = 2

        /**
         * 左下
         */
        const val LEFT_BOTTOM = 3

        /**
         * 右下
         */
        const val RIGHT_BOTTOM = 4

        /**
         * 中间
         */
        const val CENTER = 0
    }
}