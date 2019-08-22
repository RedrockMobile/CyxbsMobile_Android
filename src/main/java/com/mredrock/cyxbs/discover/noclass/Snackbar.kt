package com.mredrock.cyxbs.discover.noclass

import android.app.Activity
import android.view.View
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar.*
import org.jetbrains.anko.contentView

/**
 * Created by zxzhu
 *   2018/10/20.
 */

@IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
private annotation class Duration

fun Activity.snackbar(str: String, @Duration LENGTH: Int = LENGTH_SHORT) {
    make(contentView!!, str, LENGTH).show()
}

fun Fragment.snackbar(str: String, @Duration LENGTH: Int = LENGTH_SHORT) {
    make(view!!, str, LENGTH).show()
}

fun View.snackbar(str: String, @Duration LENGTH: Int = LENGTH_SHORT) {
    make(this, str, LENGTH).show()
}