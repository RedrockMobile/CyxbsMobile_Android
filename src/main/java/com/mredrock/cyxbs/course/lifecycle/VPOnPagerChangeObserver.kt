package com.mredrock.cyxbs.course.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager

/**
 * This class is used to automatically add and remove [ViewPager.OnPageChangeListener] in the [ViewPager].
 *
 * Created by anriku on 2018/9/18.
 */
class VPOnPagerChangeObserver(private val mViewPager: androidx.viewpager.widget.ViewPager,
                              private val mOnPageScrollStateChanged: (Int) -> Unit = {},
                              private val mOnPageScrolled: (Int, Float, Int) -> Unit = { _, _, _ -> },
                              private val mOnPageSelected: (Int) -> Unit = {}) : LifecycleObserver {

    private lateinit var onPagerChangeListener: androidx.viewpager.widget.ViewPager.OnPageChangeListener

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun addOnPageChangeListener() {
        onPagerChangeListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                mOnPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mOnPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                mOnPageSelected(position)
            }
        }

        mViewPager.addOnPageChangeListener(onPagerChangeListener)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeOnPageChangeListener() {
        mViewPager.removeOnPageChangeListener(onPagerChangeListener)
    }

}