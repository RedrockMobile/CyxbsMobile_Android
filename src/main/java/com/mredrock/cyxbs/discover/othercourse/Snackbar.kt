package com.mredrock.cyxbs.discover.othercourse

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.contentView

/**
 * Created by zxzhu
 *   2018/10/20.
 */

fun Activity.snackbar(str: String, LENGTH : Int = -1) {
    Snackbar.make(contentView!!, str, LENGTH).show()
}

fun Fragment.snackbar(str: String, LENGTH : Int = -1) {
    com.google.android.material.snackbar.Snackbar.make(view!!, str, LENGTH).show()
}

fun View.snackbar(str: String, LENGTH : Int = -1) {
    Snackbar.make(this, str, LENGTH).show()
}