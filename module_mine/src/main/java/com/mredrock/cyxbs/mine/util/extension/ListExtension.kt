package com.mredrock.cyxbs.mine.util.extension

import io.reactivex.disposables.Disposable

/**
 * Created by roger on 2020/2/28
 */
fun disposeAll(disposables: MutableList<Disposable>) {
    disposables.asSequence()
            .filterNot { it.isDisposed }
            .forEach { it.dispose() }
    disposables.clear()
}