package com.mredrock.cyxbs.noclass.callback

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntDef

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.interface
 * @ClassName:      ISlideMenuAction
 * @Author:         Yan
 * @CreateDate:     2022年08月16日 20:00:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */

interface ISlideMenuAction {

    companion object{
        /**
         * 只支持左侧滑
         */
        const val SLIDE_MODE_LEFT = 0x1234

        /**
         * 只支持右侧滑
         */
        const val SLIDE_MODE_RIGHT = 0x2341

        /**
         * 支持左侧滑和右侧滑
         */
        const val SLIDE_MODE_LEFT_RIGHT = 0x3412

        /**
         * 左滑右滑均不支持
         */
        const val SLIDE_MODE_NONE = 0x4123
    }

    /**
     * 设置滑动模式
     *
     * @param slideMode
     *  [SLIDE_MODE_LEFT]、[SLIDE_MODE_RIGHT]、[SLIDE_MODE_LEFT_RIGHT]、[SLIDE_MODE_NONE]
     *
     */
    fun setSlideMode(@SlideMode slideMode: Int)

    /**
     * 设置左侧视图的宽度
     *
     * @param slideLeftWidth 单位px
     *
     */
    fun setSlideLeftWidth(slideLeftWidth : Int)

    /**
     * 设置右侧视图的宽度
     *
     * @param slideRightWidth 单位px
     *
     */
    fun setSlideRightWidth(slideRightWidth : Int)

    /**
     *  设置视图整体宽度
     */
    fun setSlideWidth(slideWidth : Int)

    /**
     * 设置打开开关的时间
     *
     * @param slideTime 单位ms
     *
     */
    fun setSlideTime(slideTime : Int)

    /**
     * 设置在侧滑菜单打开时候的ContentView的透明度，该值会在侧滑的时候不断变化，从1.0变化至设置的值.
     *
     * @param contentAlpha contentAlpha in [0,1]，值为1.0时表示侧滑时候ContentView无透明度变化.
     *
     * 默认值为0.5
     */
    fun setContentAlpha(@FloatRange(from = 0.0, to = 1.0) contentAlpha: Float)

    /**
     * 设置ContentView在滑动过程中的阴影颜色
     *
     * @param color 颜色，默认色值：#000000
     */
    fun setContentShadowColor(@ColorRes color: Int)

    /**
     * 设置是否运行拖动侧滑菜单
     *
     * @param allowTogging
     *
     * 默认为true
     */
    fun setAllowTogging(allowTogging: Boolean)

    /**
     * 返回左侧滑视图
     *
     * @return [View]
     */
    fun getSlideLeftView(): View?

    /**
     * 返回右侧滑视图
     *
     * @return [View]
     */
    fun getSlideRightView(): View?

    /**
     * 返回侧滑主体视图
     *
     * @return [View]
     */
    fun getSlideContentView(): View

    /**
     * 打开/关闭左侧滑菜单
     */
    fun toggleLeftSlide()

    /**
     * 打开左侧滑菜单
     */
    fun openLeftSlide()

    /**
     * 关闭左侧滑菜单
     */
    fun closeLeftSlide()

    /**
     * 左侧滑菜单是否打开
     */
    fun isLeftSlideOpen(): Boolean

    /**
     * 右侧滑菜单是否打开
     */
    fun isRightSlideOpen(): Boolean

    /**
     * 打开/关闭右侧滑菜单
     */
    fun toggleRightSlide()

    /**
     * 打开右滑菜单
     */
    fun openRightSlide()

    /**
     * 关闭右侧滑菜单
     */
    fun closeRightSlide()

    /**
     * 设置侧滑菜单变化的监听器
     *
     * @param listener [OnSlideChangedListener]
     */
    fun setOnSlideChangedListener(listener: OnSlideChangedListener)

    /**
     * 滑动模式
     *
     * @hide
     */
    @IntDef(SLIDE_MODE_LEFT, SLIDE_MODE_RIGHT, SLIDE_MODE_LEFT_RIGHT, SLIDE_MODE_NONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class SlideMode

}