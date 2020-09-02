package com.mredrock.cyxbs.qa.utils

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat

/**
 * Created by yyfbe, Date on 2020/8/31.
 */
@SuppressLint("ClickableViewAccessibility")
internal fun TextView.setSelectableTextViewClick(onClick: (TextView) -> Unit) {
    val gestureDetector = GestureDetectorCompat(this.context, GestureDetector.SimpleOnGestureListener())
    gestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onClick(this@setSelectableTextViewClick)
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return false
        }
    })
    setOnTouchListener { v, event ->
        gestureDetector.onTouchEvent(event)
        false
    }

}