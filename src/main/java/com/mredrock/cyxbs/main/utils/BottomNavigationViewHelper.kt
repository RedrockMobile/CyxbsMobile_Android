package com.mredrock.cyxbs.main.utils

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * Created By jay68 on 2018/8/16.
 */
open class BottomNavigationViewHelper(nav: BottomNavigationView) {
    private val bottomNavigationView: BottomNavigationView = nav

    private val menuView: BottomNavigationMenuView? by lazy { reflectBottomNavigationMenuView() }
    private val itemViews: Array<BottomNavigationItemView>? by lazy { reflectBottomNavigationItemViews() }
    private val largeLabels: Array<TextView>? by lazy { reflectLargeLabels() }
    private val smallLabels: Array<TextView>? by lazy { reflectSmallLabels() }
    private val icons: Array<ImageView>? by lazy { reflectIcons() }

    private var isItemShiftModeEnable = itemViews?.size ?: 0 > 3

    private var shiftAmount = 0
    private var scaleUpFactor = 0f
    private var scaleDownFactor = 0f


    private fun reflectBottomNavigationMenuView() = getField(bottomNavigationView, "menuView") as? BottomNavigationMenuView

    private fun reflectBottomNavigationItemViews(): Array<BottomNavigationItemView>? {
        val array = getField(menuView, "buttons") as? Array<BottomNavigationItemView>
        shiftAmount = getField(array?.get(0), "shiftAmount") as? Int ?: 0
        scaleUpFactor = getField(array?.get(0), "scaleUpFactor") as? Float ?: 0f
        scaleDownFactor = getField(array?.get(0), "scaleDownFactor") as? Float ?: 0f
        return array
    }

    private fun reflectLargeLabels(): Array<TextView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "largeLabel") as TextView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"largeLabel\" failed", e)
        }
        return null
    }

    private fun reflectSmallLabels(): Array<TextView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "smallLabel") as TextView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"smallLabel\" failed", e)
        }
        return null
    }

    private fun reflectIcons(): Array<ImageView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "icon") as ImageView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"icon\" failed", e)
        }
        return null
    }


    fun setTextSize(size: Float) {
        largeLabels?.forEach { it.textSize = size }
        if (!isItemShiftModeEnable) {
            smallLabels?.forEach { it.textSize = size }
        }
    }

    fun setIconSize(size: Int) {
        icons?.forEach {
            val layoutParams = it.layoutParams
            layoutParams.width = size
            layoutParams.height = size
            it.layoutParams = layoutParams
        }
    }

    fun setItemIconTintList(colorStateList: ColorStateList?) {
        bottomNavigationView.itemIconTintList = colorStateList
    }

    private fun getField(obj: Any?, fieldName: String): Any? {
        obj ?: return null
        try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            return field.get(obj)
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"$fieldName\" failed", e)
        }
        return null
    }
}