package com.mredrock.cyxbs.noclass.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.mredrock.cyxbs.noclass.R

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.util
 * @ClassName:      AnimUtil
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 00:12:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    动画工具类
 */

/**
 * 折叠动画
 */
internal fun View.collapseAnim(viewHeight : Int,time : Long = 1000) : ValueAnimator{
    return ValueAnimator.ofInt(viewHeight,1).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            this@collapseAnim.apply {
                layoutParams.height = it.animatedValue as Int
                requestLayout()
            }
        }
        doOnEnd {
            this@collapseAnim.visibility = View.GONE
        }
    }
}


/**
 * 展开动画
 */
internal fun View.expandAnim(viewHeight: Int,time : Long = 800) : ValueAnimator{
    return ValueAnimator.ofInt(1,viewHeight).apply {
        duration = time
        interpolator = LinearInterpolator()
        doOnStart {
            this@expandAnim.visibility = View.VISIBLE
        }
        addUpdateListener {
            this@expandAnim.apply {
                layoutParams.height = it.animatedValue as Int
                requestLayout()
            }
        }
    }
}

/**
 * 震动效果
 */
internal fun View.startShake(){
    val shakeAnim = AnimationUtils.loadAnimation(this.context, R.anim.noclass_tv_shake_anim)
    this.startAnimation(shakeAnim)
    VibratorUtil.start(this.context,300)
}

/**
 * 旋转效果
 */
internal fun View.rotateAnim(startAngle : Float,endAngle : Float,time : Long = 200) : ValueAnimator{

    return ValueAnimator.ofFloat(startAngle,endAngle).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            this@rotateAnim.rotation = it.animatedValue as Float
        }
    }
}

/**
 * 向右位移效果
 */
internal fun View.translateXRightAnim(dx : Float,time : Long = 200) : ValueAnimator{
    return ValueAnimator.ofFloat(0f,dx).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            translationX = it.animatedValue as Float
        }
    }
}

/**
 * 向左位移效果
 */
internal fun View.translateXLeftAnim(dx : Float,time : Long = 200) : ValueAnimator{
    return ValueAnimator.ofFloat(dx,0f).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            translationX = it.animatedValue as Float
        }
    }
}

/**
 * 透明度动画
 */
internal fun View.alphaAnim(begin : Float,end : Float,time : Long = 200) : ValueAnimator{
    return ValueAnimator.ofFloat(begin,end).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            this@alphaAnim.alpha = it.animatedValue as Float
        }
    }
}

/**
 * 颜色动画
 */
internal fun View.colorAnim(fromColor:Int,toColor : Int,time: Long) : ValueAnimator{
    return ValueAnimator.ofArgb(fromColor,toColor).apply {
        duration = time
        interpolator = LinearInterpolator()
        addUpdateListener {
            this@colorAnim.setBackgroundColor(it.animatedValue as Int)
        }
    }
}