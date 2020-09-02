package com.mredrock.cyxbs.discover.map.component

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView

class ClickView : AppCompatImageView {
    constructor(context: Context?) : super(context) {
        //        必须设置setClickable(true), 要不然像ImageView 默认是点击不了的，它就收不到Action_Up 事件
//        如果一个view 的onTouchEvent 的Action_Down 返回false ，那么view的 Action_Up 是不会执行的
        this.isClickable = true
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.isClickable = true
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.isClickable = true
    }

    /**
     * 顶点判断【0：中间】【1：上】【2：右】【3：下】【4：左】
     */
    private var pivot = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> //                getX() 和 getY() 获取的是距离自身的坐标位置
                startAnimDown(event.x, event.y)
            MotionEvent.ACTION_UP -> endAnimal()
        }
        return super.onTouchEvent(event)
    }

    /**
     * 顶点判断【0：中间】【1：上】【2：右】【3：下】【4：左】
     */
    var interpolator = OvershootInterpolator(3f)
    private fun startAnimDown(touchX: Float, touchY: Float) {
//        获取的是自身的相对坐标位置
        if (mWidth / 5 * 2 < touchX && touchX < mWidth / 5 * 3 && mHeight / 5 * 2 < touchY && touchY < mHeight / 5 * 3) {
//            代表中心位置
            pivot = 0
        } else if (touchX < mWidth / 2 && touchY < mHeight / 2) {
            pivot = if (touchX > touchY) {
                1 // 代表用户点击的上面
            } else {
                4 // 代表用户点击的左边
            }
        } else if (touchX < mWidth / 2 && touchY > mHeight / 2) {
            pivot = if (touchX < mHeight - touchY) { //
                4 // 代表用户点击左边
            } else {
                3 // 代表用户点击下面
            }
        } else if (touchX > mWidth / 2 && touchY < mHeight / 2) {
            pivot = if (mWidth - touchX < touchY) { //点击右边
                2 // 点击右边
            } else {
                1 // 代表用户点击上面
            }
        } else if (touchX > mWidth / 2 && touchY > mHeight / 2) {
            pivot = if (mWidth - touchX > mHeight - touchY) { //点击下面
                3 //点击下面
            } else {
                2 // 代表用户点击右边
            }
        }
        //        上面这种算法只适合正方形的图片
        var myAnim = ""
        when (pivot) {
            0 -> {
                startCenterSmallAnimal()
                return
            }
            1, 3 -> myAnim = "rotationX"
            2, 4 -> myAnim = "rotationY"
        }
        //        设置动画的基准点
        this.pivotX = mWidth / 2.toFloat()
        this.pivotY = mHeight / 2.toFloat()
        //        【0：中间】【1：上】【2：右】【3：下】【4：左】
        when (pivot) {
            4 -> {
                endRotationValue = -17f
            }
            2 -> {
                endRotationValue = 17f
            }
            1 -> {
                endRotationValue = 17f
            }
            3 -> {
                endRotationValue = -17f
            }
        }
        val animObject = ObjectAnimator.ofFloat(this, myAnim, 0f, endRotationValue)
                .setDuration(300)
        animObject.interpolator = interpolator
        animObject.start()
    }

    private fun startCenterSmallAnimal() {
        val tzStart = this.translationZ.toInt()
        this.tag = tzStart
        val tz = PropertyValuesHolder.ofFloat("translationZ", tzStart.toFloat(), 0f)
        val tX = PropertyValuesHolder.ofFloat("scaleX", this.scaleX, 0.9f)
        val tY = PropertyValuesHolder.ofFloat("scaleY", this.scaleY, 0.9f)
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, tz, tX, tY).setDuration(300)
        objectAnimator.interpolator = interpolator
        objectAnimator.start()
    }

    private fun startCenterBigAnimal() {
        val tzValue = this.tag as Int
        val tz = PropertyValuesHolder.ofFloat("translationZ", 0f, tzValue.toFloat())
        val tX = PropertyValuesHolder.ofFloat("scaleX", this.scaleX, 1f)
        val tY = PropertyValuesHolder.ofFloat("scaleY", this.scaleY, 1f)
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, tz, tX, tY).setDuration(300)
        objectAnimator.interpolator = interpolator
        objectAnimator.start()
    }

    //    结束动画
    private fun endAnimal() {
        var anim = ""
        when (pivot) {
            0 -> {
                startCenterBigAnimal()
                return
            }
            1, 3 -> anim = "rotationX"
            2, 4 -> anim = "rotationY"
        }
        startRotationValue = if (pivot == 2 || pivot == 4) {
            this.rotationY.toInt()
        } else {
            this.rotationX.toInt()
        }
        val objectAnimator = ObjectAnimator.ofFloat(this, anim, startRotationValue.toFloat(), 0f)
        objectAnimator.duration = 300
        objectAnimator.interpolator = interpolator
        objectAnimator.start()
    }

    var endRotationValue = 0f
    var startRotationValue = 0

    //    当宽高发生变化时进行的回调
    private var mHeight = 0
    private var mWidth = 0

    //    System.out: touchX :552.0touchY :492.0
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
        mHeight = h
    }
}