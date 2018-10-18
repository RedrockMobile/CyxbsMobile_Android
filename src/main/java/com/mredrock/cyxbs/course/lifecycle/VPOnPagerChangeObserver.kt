package com.mredrock.cyxbs.course.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v4.view.ViewPager

/**
 * This class is used to automatically add and remove [ViewPager.OnPageChangeListener] in the [ViewPager].
 *
 * Created by anriku on 2018/9/18.
 */
class VPOnPagerChangeObserver(private val mViewPager: ViewPager,
                              private val mOnPageScrollStateChanged: (Int) -> Unit = {},
                              private val mOnPageScrolled: (Int, Float, Int) -> Unit = { _, _, _ -> },
                              private val mOnPageSelected: (Int) -> Unit = {}) : LifecycleObserver {

    private lateinit var onPagerChangeListener: ViewPager.OnPageChangeListener

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun addOnPageChangeListener() {
        onPagerChangeListener = object : ViewPager.OnPageChangeListener {
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