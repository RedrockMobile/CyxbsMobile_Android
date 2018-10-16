package com.mredrock.cyxbs.main.utils

import android.content.res.ColorStateList
import android.support.annotation.DrawableRes
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
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

    private var isAnimationEnable = true
    private var isShiftModeEnable = itemViews?.size ?: 0 > 3
    private var isItemShiftModeEnable = itemViews?.size ?: 0 > 3

    private var shiftAmount = 0
    private var scaleUpFactor = 0f
    private var scaleDownFactor = 0f

    private lateinit var viewPager: ViewPager

    private fun reflectBottomNavigationMenuView() = getField(bottomNavigationView, "mMenuView") as? BottomNavigationMenuView

    private fun reflectBottomNavigationItemViews(): Array<BottomNavigationItemView>? {
        val array = getField(menuView, "mButtons") as? Array<BottomNavigationItemView>
        shiftAmount = getField(array?.get(0), "mShiftAmount") as? Int ?: 0
        scaleUpFactor = getField(array?.get(0), "mScaleUpFactor") as? Float ?: 0f
        scaleDownFactor = getField(array?.get(0), "mScaleDownFactor") as? Float ?: 0f
        return array
    }

    private fun reflectLargeLabels(): Array<TextView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "mLargeLabel") as TextView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"mLargeLabel\" failed", e)
        }
        return null
    }

    private fun reflectSmallLabels(): Array<TextView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "mSmallLabel") as TextView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"mSmallLabel\" failed", e)
        }
        return null
    }

    private fun reflectIcons(): Array<ImageView>? {
        try {
            itemViews?.let { itemViews ->
                return Array(itemViews.size) {
                    getField(itemViews[it], "mIcon") as ImageView
                }
            }
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"mIcon\" failed", e)
        }
        return null
    }

    fun enableAnimation(enable: Boolean) {
        if (isAnimationEnable == enable) return
        isAnimationEnable = enable
        itemViews?.forEach {
            setField(it, "mShiftAmount", if (isAnimationEnable) shiftAmount else 0)
            setField(it, "mScaleUpFactor", if (isAnimationEnable) scaleUpFactor else 1f)
            setField(it, "mScaleDownFactor", if (isAnimationEnable) scaleDownFactor else 1f)
        }
    }

    fun enableShiftMode(enable: Boolean) {
        if (isShiftModeEnable == enable) return
        isShiftModeEnable = enable
        setField(menuView, "mShiftingMode", isShiftModeEnable)
    }

    fun enableItemShiftMode(enable: Boolean) {
        if (isItemShiftModeEnable == enable) return
        isItemShiftModeEnable = enable
        itemViews?.forEach { setField(it, "mShiftingMode", enable) }
    }

    fun setTextSize(size: Float) {
        largeLabels?.forEach { it.textSize = size }
        if (!isItemShiftModeEnable) {
            smallLabels?.forEach { it.textSize = size }
        }
    }

    fun setIcon(index: Int, @DrawableRes drawable: Int) {
        icons?.let {
            it[index].setImageResource(drawable)
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

    fun bindViewPager(viewPager: ViewPager, listener: ((position: Int, menuItem: MenuItem) -> Unit)?) {
        this.viewPager = viewPager
        val pageChangedListener = OnPageChangedListener(bottomNavigationView, viewPager, listener)
        viewPager.addOnPageChangeListener(pageChangedListener)
        bottomNavigationView.setOnNavigationItemSelectedListener(pageChangedListener)
    }

    private fun setField(obj: Any?, fieldName: String, value: Any) {
        obj ?: return
        try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(obj, value)
        } catch (e: Throwable) {
            LogUtils.e(javaClass.name, "reflect field \"$fieldName\" failed", e)
        }
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

    /*fun updateMenuView() {
        if (menuView == null) throw RuntimeException("get mMenuView by reflect failed")
        try {
            val clazz = mMenuView!!.javaClass
            val method = clazz.getDeclaredMethod("updateMenuView")
            method.invoke(mMenuView)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

    }*/

    private class OnPageChangedListener(private val nav: BottomNavigationView,
                                        private val vp: ViewPager,
                                        private val listener: ((position: Int, menuItem: MenuItem) -> Unit)?) : BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
        private val idToPosition: SparseArray<Int>
        private val positionToItem: SparseArray<MenuItem>

        init {
            val menu = nav.menu
            val size = menu.size()
            idToPosition = SparseArray()
            positionToItem = SparseArray()
            var j = 0
            for (i in 0 until size) {
                val item = menu.getItem(i)
                if (item.isEnabled) {
                    positionToItem.put(j, item)
                    idToPosition.put(item.itemId, j++)
                }
            }
        }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val position = idToPosition[item.itemId]
            vp.currentItem = position
            listener?.invoke(position, item)
            return true
        }

        override fun onPageScrollStateChanged(state: Int) = Unit

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

        override fun onPageSelected(position: Int) {
            nav.selectedItemId = positionToItem[position].itemId
            listener?.invoke(position, positionToItem[position])
        }
    }
}