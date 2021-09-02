package com.mredrock.cyxbs.store.utils.widget.slideshow.myinterface

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2.ScrollState
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators

/**
 * 如果你想实现自己的指示器，建议继承于 AbstractIndicatorsView 抽象类，此类省略了绘制全部轨迹的过程，
 * 可快速通过一个区间的轨迹绘制而重复于全部轨迹
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/15
 */
interface IIndicator {

    /**
     * 得到指示器的 View 对象
     */
    fun getIndicatorView(): View

    /**
     * 初始化圆点数量
     */
    fun setAmount(amount: Int)

    /**
     * 用于在设置了 SlideShow 的 adapter 后动态改变 indicators 的圆点数量
     */
    fun changeAmount(amount: Int)

    /**
     * 与内部 ViewPager2#onPageScrolled 联合
     */
    fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int)

    /**
     * 设置内部圆点整体的位置
     */
    fun setIndicatorsInnerGravity(@Indicators.InnerGravity gravity: Int)

    /**
     * 设置整个横幅的位置
     */
    fun setIndicatorsOuterGravity(@Indicators.OuterGravity gravity: Int): FrameLayout.LayoutParams {
        return when (gravity) {
            Indicators.OuterGravity.TOP -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            )
            Indicators.OuterGravity.BOTTOM -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            Indicators.OuterGravity.LEFT -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.START
            )
            Indicators.OuterGravity.RIGHT -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.END
            )
            // 横向且垂直居中
            Indicators.OuterGravity.HORIZONTAL_CENTER -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            )
            // 竖直且水平居中
            Indicators.OuterGravity.VERTICAL_CENTER -> FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL
            )
            // 为了过 when 的检查，永远不会被调用
            else -> {
                FrameLayout.LayoutParams( // never
                    300,
                    300,
                    Gravity.CENTER
                )
            }
        }
    }

    /**
     * 与内部 ViewPager2#onPageSelected 联合
     */
    fun onPageSelected(position: Int) {
    }

    /**
     * 与内部 ViewPager2#onPageScrollStateChanged 联合
     */
    fun onPageScrollStateChanged(@ScrollState state: Int) {
    }

    /**
     * 设置可见性
     */
    fun setShowIndicators(boolean: Boolean) {
        getIndicatorView().visibility = if (boolean) View.VISIBLE else View.GONE
    }

    /**
     * 设置指示器的背景颜色，会在指示器初始化时调用
     */
    fun setIndicatorsBackgroundColor(color: Int) {
        getIndicatorView().setBackgroundColor(color)
    }
}