package com.mredrock.cyxbs.discover.map.util

import android.view.View
import androidx.viewpager.widget.ViewPager

class BannerPageTransformer : ViewPager.PageTransformer {
    companion object {
        const val DEFAULT_CENTER: Float = 0.5f
    }

    private val mMinScale = 0.85f
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        view.pivotY = (pageHeight shr 1.toFloat().toInt()).toFloat()
        view.pivotX = (pageWidth shr 1.toFloat().toInt()).toFloat()
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.scaleX = mMinScale
            view.scaleY = mMinScale
            view.pivotX = pageWidth.toFloat()
        } else if (position <= 1) {
            // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) {
                //1-2:1[0,-1] ;2-1:1[-1,0]
                val scaleFactor: Float = (1 + position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * (DEFAULT_CENTER + DEFAULT_CENTER * -position)
            } else {
                //1-2:2[1,0] ;2-1:2[0,1]
                val scaleFactor: Float = (1 - position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * ((1 - position) * DEFAULT_CENTER)
            }
        } else {
            // (1,+Infinity]
            view.pivotX = 0f
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        }

    }
}