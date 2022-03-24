package com.vmloft.develop.library.base

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

/**
 * Create by lzan13 on 2022/3/10
 * 描述：对 Android ActivityResultContract 进行简单封装
 */
open class BResultLauncher<I, O>(caller: ActivityResultCaller, contract: ActivityResultContract<I, O>) {

    private var launcher: ActivityResultLauncher<I>
    private var callback: ActivityResultCallback<O>? = null


    init {
        launcher = caller.registerForActivityResult(contract) {
            callback?.onActivityResult(it)
        }
    }

    fun launch(input: I, callback: ActivityResultCallback<O>) {
        this.callback = callback
        launcher.launch(input)
    }
}

