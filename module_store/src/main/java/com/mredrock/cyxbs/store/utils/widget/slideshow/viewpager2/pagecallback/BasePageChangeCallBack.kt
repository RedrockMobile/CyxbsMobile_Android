package com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.pagecallback

import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.store.utils.widget.slideshow.myinterface.IIndicator

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
internal class BasePageChangeCallBack(
    private val viewPager2: ViewPager2
) : ViewPager2.OnPageChangeCallback() {

    companion object {

        /**
         * 由 position 取余而得到的显示的数据位置
         */
        fun getFalsePosition(isCirculate: Boolean, realPosition: Int, dataSize: Int): Int {
            return if (isCirculate) {
                realPosition % dataSize
            }else realPosition
        }

        /**
         * 得到该回到的居中的位置
         */
        fun getBackPosition(currentItem: Int, itemCount: Int, dataSize: Int): Int {
            val dataPosition = currentItem % dataSize
            val centerPosition = itemCount / 2
            return centerPosition + (dataPosition - centerPosition % dataSize)
        }
    }

    fun setPageChangeCallBack(callBack: ViewPager2.OnPageChangeCallback) {
        mCallBack = callBack
    }

    fun removePageChangeCallback() {
        mCallBack = null
    }

    fun openCirculateEnabled(dataSize: Int) {
        if (!mIsCirculate) {
            if (dataSize > 1) {
                mIsCirculate = true
                mDataSize = dataSize
            }
        }
    }

    fun setIndicators(indicators: IIndicator) {
        mIndicators = indicators
    }

    private var mIndicators: IIndicator? = null
    private var mCallBack: ViewPager2.OnPageChangeCallback? = null
    private var mPositionFloat = 0F
    private var mIsCirculate = false
    private var mDataSize = 1
    private var mIdlePosition = -1

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mIdlePosition == -1) mIdlePosition = position
        mPositionFloat = position + positionOffset
        pageScrolledCallback(position, positionOffset, positionOffsetPixels)
    }

    private fun pageScrolledCallback(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mIsCirculate) {
            if (mPositionFloat % mDataSize > mDataSize - 1) {// 当划出边界时
                val pf = (1 - positionOffset) * (mDataSize - 1)// 这里可以表示从右边界到左边界(或相反)经过的值
                mIndicators?.onPageScrolled(pf.toInt(), pf - pf.toInt(), positionOffsetPixels)
                mCallBack?.onPageScrolled(pf.toInt(), pf - pf.toInt(), positionOffsetPixels)
                return
            }
        }
        val falsePosition = getFalsePosition(mIsCirculate, position, mDataSize)
        mIndicators?.onPageScrolled(
            falsePosition,
            positionOffset,
            positionOffsetPixels)
        mCallBack?.onPageScrolled(
            falsePosition,
            positionOffset,
            positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        pageSelected(position)
    }

    private fun pageSelected(position: Int) {
        val falsePosition = getFalsePosition(mIsCirculate, position, mDataSize)
        mIndicators?.onPageSelected(falsePosition)
        mCallBack?.onPageSelected(falsePosition)
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager2.SCROLL_STATE_IDLE) {
            mIdlePosition = mPositionFloat.toInt()
            /*
            * 开启循环后，vp 一来就处于中间位置，且左右两边划到边界时需要划很久，只要一旦停下来就快速又移到中间位置
            * */
            if (mIsCirculate) {
                viewPager2.setCurrentItem(
                    getBackPosition(viewPager2.currentItem, viewPager2.adapter!!.itemCount, mDataSize),
                    false
                )
            }
        }
        mIndicators?.onPageScrollStateChanged(state)
        mCallBack?.onPageScrollStateChanged(state)
    }
}