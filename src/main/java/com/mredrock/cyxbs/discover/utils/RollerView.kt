package com.mredrock.cyxbs.discover.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mredrock.cyxbs.discover.R

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */

class RollerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        FrameLayout(context, attrs), Runnable, androidx.viewpager.widget.ViewPager.PageTransformer {
    private var mViewPager: androidx.viewpager.widget.ViewPager? = null
    private var mRollerViewAdapter: RollerViewAdapter? = null
    private var mAlpha: Float? = 0f
    private var mPageMargin: Int = 0
    private var mPageScale: Float = 0.toFloat()
    private var mEdgeSize: Float = 0.toFloat()
    private var mShowMultiPage: Boolean = false
    private var mAspectRatio: Float = 0.toFloat()
    private var mDelay: Int = 0
    private var mIsTouch: Boolean = false

    private var mAllowAnimator: Boolean = false

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        initAttrs(context, attrs)
        initView()
        if (mShowMultiPage) {
            showMultiPage()
        }
    }

    private fun initView() {
        mViewPager = androidx.viewpager.widget.ViewPager(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.gravity = Gravity.CENTER
        mViewPager!!.layoutParams = params
        mViewPager!!.adapter = Adapter()
        mViewPager!!.setPageTransformer(true, this)
        addView(mViewPager)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RollerView)
        mPageMargin = array.getDimensionPixelSize(R.styleable.RollerView_pageMargin, 0)
        mEdgeSize = array.getDimensionPixelSize(R.styleable.RollerView_EdgePageSize, 0).toFloat()
        mPageScale = array.getFloat(R.styleable.RollerView_pageScale, 1f)
        mAlpha = array.getFloat(R.styleable.RollerView_alpha, 1f)
        mAspectRatio = array.getFloat(R.styleable.RollerView_aspectRatio, 0f)
        mShowMultiPage = array.getBoolean(R.styleable.RollerView_showMultiPage, true)
        mDelay = array.getInt(R.styleable.RollerView_delay, 5000)
        array.recycle()
    }

    private fun showMultiPage() {
        mViewPager!!.offscreenPageLimit = 0
        mViewPager!!.pageMargin = mPageMargin

        mViewPager!!.clipChildren = false
        val root = rootView as ViewGroup
        var parent = mViewPager!!.parent as ViewGroup
        while (root !== parent) {
            parent.clipChildren = false
            parent = parent.parent as ViewGroup
        }
        root.clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //处理比例模式
        if (mAspectRatio != 0f && widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            widthSize = if (widthSize == 0) (heightSize * mAspectRatio).toInt() else widthSize
            heightSize = if (heightSize == 0) (widthSize / mAspectRatio).toInt() else heightSize
        }
        //使viewpager显示多个页面
        if (mShowMultiPage) {
            val params = mViewPager!!.layoutParams
            params.height = heightSize
            params.width = (widthSize.toFloat() - (mPageMargin * 2).toFloat() - mEdgeSize * 2).toInt()
            mViewPager!!.layoutParams = params
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                MeasureSpec.makeMeasureSpec(heightSize, heightMode))
    }

    fun setAdapter(adapter: RollerViewAdapter?) {
        if (adapter != null) {
            mRollerViewAdapter = adapter
            (mViewPager!!.adapter as Adapter).setAdapter(adapter)
            mViewPager!!.currentItem = 1
            mViewPager!!.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAllowAnimator = true
        mViewPager!!.post(this)
    }

    override fun run() {
        if (!mIsTouch) {
            val position = mViewPager!!.currentItem + 1
            mViewPager!!.setCurrentItem(if (position < 0) 0 else position, true)
        }
        if (mAllowAnimator) {
            mViewPager!!.postDelayed(this, mDelay.toLong())
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev!!.action) {
            MotionEvent.ACTION_DOWN -> mIsTouch = true
            MotionEvent.ACTION_UP -> mIsTouch = false
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAllowAnimator = false
    }

    override fun transformPage(view: View, position: Float) {
        val minAlpha = mAlpha!!
        when {
            position < -1 -> {
                view.alpha = minAlpha
                view.scaleX = mPageScale
                view.scaleY = mPageScale
            }
            position <= 1 -> {
                val scaleFactor = Math.max(mPageScale, 1 - Math.abs(position))
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.alpha = minAlpha + (scaleFactor - mPageScale) / (1 - mPageScale) * (1 - minAlpha)
            }
            else -> {
                view.alpha = minAlpha
                view.scaleX = mPageScale
                view.scaleY = mPageScale
            }
        }
    }

    private inner class Adapter : androidx.viewpager.widget.PagerAdapter() {
        private var mRollerViewAdapter: RollerViewAdapter? = null

        fun setAdapter(adapter: RollerViewAdapter) {
            mRollerViewAdapter = adapter
        }

        override fun getCount() = if (mRollerViewAdapter == null) 0 else Integer.MAX_VALUE


        override fun isViewFromObject(view: View, `object`: Any) = view === `object`


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = mRollerViewAdapter!!.getView(container,
                    position % mRollerViewAdapter!!.itemCount)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    abstract class RollerViewAdapter {
        abstract val itemCount: Int

        abstract fun getView(container: ViewGroup, position: Int): View
    }
}