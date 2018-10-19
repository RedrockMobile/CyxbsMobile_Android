package com.mredrock.cyxbs.discover.othercourse

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View
import org.jetbrains.anko.contentView

/**
 * Created by zxzhu
 *   2018/10/20.
 */

fun Activity.snackbar(str: String, LENGTH : Int = -1) {
    Snackbar.make(contentView!!, str, LENGTH).show()
}

fun Fragment.snackbar(str: String, LENGTH : Int = -1) {
    Snackbar.make(view!!, str, LENGTH).show()
}

fun View.snackbar(str: String, LENGTH : Int = -1) {
    Snackbar.make(this, str, LENGTH).show()
}