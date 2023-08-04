package com.mredrock.cyxbs.sport.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import com.mredrock.cyxbs.sport.R
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle


/**
 * @author : why
 * @time   : 2022/8/9 20:02
 * @bless  : God bless my code
 */

@SuppressLint("RestrictedApi")
class MyHeader(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr), RefreshHeader {

    /**
     * 中间用于旋转的小图标
     */
    private var mImage: ImageView? = null

    /**
     * 用于记录旋转的角度，便于和刷新无缝衔接
     */
    private var mRotation: Float? = null

    //旋转的动画
    private val animator = ValueAnimator().apply {
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            duration = 1500
            mImage?.rotation = it.animatedValue as Float
        }
        interpolator = LinearInterpolator()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        val view: View = View.inflate(context, R.layout.sport_myheader, this)
        //获取到刷新小图标
        mImage = view.findViewById(R.id.sport_iv_detail_srl_header)
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        //设置模式为平移
        return SpinnerStyle.Translate
    }


    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        //如果手指正在拖动，则让图标跟着旋转
        if (isDragging) {
            mImage?.rotation = percent * 360f
            mRotation = mImage?.rotation
        }
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.Refreshing -> {
                //设置初始值，无缝衔接
                //这里写成 (mRotation ?: 0f) + 360f 是为了防止mRotation保存的角度大于360导致其反向旋转
                //因此在此基础上加360以确保其旋转方向正确
                animator.setFloatValues(mRotation ?: 0f, (mRotation ?: 0f) + 360f)
                //开始刷新后开启动画
                animator.start()
            }
            else -> {}
        }
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        //刷新结束后取消动画
        animator.cancel()
        return 0
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean = false
    override fun autoOpen(duration: Int, dragRate: Float, animationOnly: Boolean): Boolean {
        return true
    }

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }
}