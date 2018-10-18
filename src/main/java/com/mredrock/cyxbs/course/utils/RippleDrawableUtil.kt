package com.mredrock.cyxbs.course.utils

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build

/**
 * This class will generate the [RippleDrawable] when SDK version >= 21, else it will generate
 * the [ColorDrawable]
 *
 * Created by anriku on 2018/10/13.
 */
object RippleDrawableUtil {

    /**
     * This method is used to generate the Drawable.
     * @param rippleColor When SDK version >= 21, it will use to generate the [RippleDrawable]
     * @param background The base background color.
     */
    fun getRippleDrawable(rippleColor: Int, background: Int): Drawable =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable(getColorStateList(rippleColor), getColorDrawable(background), null)
            } else {
                ColorDrawable(background)
            }

    /**
     * This method is used to generate the [ColorStateList] needed by the [RippleDrawable]
     *
     * @param rippleColor When SDK version >= 21, it will use to generate the [RippleDrawable]
     */
    private fun getColorStateList(rippleColor: Int): ColorStateList =
            ColorStateList(arrayOf(intArrayOf()), intArrayOf(rippleColor))

    /**
     * This method is used to generate the [ColorDrawable]
     *
     * @param background The base background color.
     */
    private fun getColorDrawable(background: Int): ColorDrawable =
            ColorDrawable(background)
}