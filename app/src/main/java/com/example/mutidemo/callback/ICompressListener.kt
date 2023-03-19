package com.example.mutidemo.callback

import java.io.File

interface ICompressListener {
    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    fun onSuccess(file: File)

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    fun onError(e: Throwable)
}