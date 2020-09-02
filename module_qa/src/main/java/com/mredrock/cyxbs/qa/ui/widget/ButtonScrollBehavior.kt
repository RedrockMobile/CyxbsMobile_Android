package com.mredrock.cyxbs.qa.ui.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.SCROLL_AXIS_VERTICAL
import androidx.coordinatorlayout.widget.CoordinatorLayout


class ButtonScrollBehavior : CoordinatorLayout.Behavior<View> {

    private var isShow = true
    private var preAnimator: Animator? = null
    private val thresholdValue = 5


    constructor()
    constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs
    )


    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
    ): Boolean {
        return SCROLL_AXIS_VERTICAL == axes
    }


    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
    ) {
        if (dy >= thresholdValue && isShow) {
            ValueAnimator.ofFloat(child.translationY, (coordinatorLayout.bottom - child.top).toFloat()).apply {
                addUpdateListener {
                    child.translationY = it.animatedValue as Float
                }
                preAnimator?.cancel()
                preAnimator = this
            }.start()
            isShow = false
        } else if (dy <= -thresholdValue && !isShow) {
            ValueAnimator.ofFloat(child.translationY, 0f).apply {
                addUpdateListener {
                    child.translationY = it.animatedValue as Float
                }
                preAnimator?.cancel()
                preAnimator = this
            }.start()
            isShow = true
        }
    }
}