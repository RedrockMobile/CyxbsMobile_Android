package com.mredrock.cyxbs.discover.map.component


import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class BannerView : ViewPager {

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    /**
     * 播放时间
     */
    private var showTime = 3 * 1000

    /**
     * 滚动方向
     */
    private var direction = Direction.LEFT


    /**
     * 播放器
     */
    private val player = Runnable { play(direction) }

    enum class Direction {
        /**
         * 向左行动，播放的应该是右边的
         */
        LEFT,

        /**
         * 向右行动，播放的应该是左边的
         */
        RIGHT
    }

    /**
     * 设置滚动方向，默认向左滚动
     *
     * @param direction 方向
     */
    fun setDirection(direction: Direction) {
        this.direction = direction
    }

    /**
     * 设置播放时间，默认3秒
     */
    fun setShowTime(showTimeMillis: Int) {
        showTime = showTimeMillis
    }

    /**
     * 开始
     */
    fun start() {
        stop()
        postDelayed(player, showTime.toLong())
    }

    /**
     * 停止
     */
    fun stop() {
        removeCallbacks(player)
    }

    /**
     * 播放上一个
     */
    fun previous() {
        if (direction == Direction.RIGHT) {
            play(Direction.LEFT)
        } else if (direction == Direction.LEFT) {
            play(Direction.RIGHT)
        }
    }

    /**
     * 播放下一个
     */
    operator fun next() {
        play(direction)
    }

    /**
     * 执行播放
     *
     * @param direction 播放方向
     */
    @Synchronized
    private fun play(direction: Direction) {
        val pagerAdapter = adapter
        if (pagerAdapter != null) {
            val count = pagerAdapter.count
            var currentItem = currentItem
            when (direction) {
                Direction.LEFT -> {
                    currentItem++
                    if (currentItem == count) {
                        currentItem = 0
                    }
                }
                Direction.RIGHT -> {
                    currentItem--
                    if (currentItem < 0) currentItem = count
                }
            }
            setCurrentItem(currentItem)
        }
        start()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val scroller = ViewPagerScroller(context)
        scroller.setScrollDuration(2000)
        scroller.initViewPagerScroll(this)
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_IDLE) {
                    start()
                    scroller.setScrollDuration(2000)
                } else if (state == SCROLL_STATE_DRAGGING) {
                    stop()
                    scroller.setScrollDuration(250)
                }

            }
        })
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(player)
        super.onDetachedFromWindow()
    }
}