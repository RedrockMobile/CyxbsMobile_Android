package com.mredrock.cyxbs.qa.ui.widget.likeview

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import java.lang.ref.WeakReference

/**
 *@author zhangzhe
 *@date 2020/12/19
 *@description 采用观察者模式，观察点赞的变化
 */
class LikeViewSlim @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LikeView(context, attrs, defStyleAttr) {

    companion object {
        /**
         *  存放对应id和model的帖子/评论的点赞数和是否点赞
         *  Map的键：id和model的拼接，用“-”连接，如“15-1”表示id为15的帖子，“20-0”表示id为20的动态
         *  Map的值：Pair第一个参数：点赞数；第二个参数：是否点赞
         */
        private val likeMap = HashMap<String, Pair<Int, Boolean>?>()
        private val observer = HashMap<String, ArrayList<WeakReference<LikeViewSlim>>?>()
    }


    private var isLoading = false
    private var praiseCount = 0
    private var id = ""
    private var model = "0"
    private var isPraised = false

    // 对View的持有为弱引用，防止内存泄漏
    private var weakP: WeakReference<LikeViewSlim>? = null

    private val textPaint = TextPaint()

    init {
        textPaint.textSize = context.sp(11).toFloat()
        textPaint.isAntiAlias = true
    }


    // 改变默认尺寸方便画点赞数
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(heightMeasureSpec)
        val mode = MeasureSpec.EXACTLY
        width += 500
        val w = MeasureSpec.makeMeasureSpec(width, mode)
        super.onMeasure(w, heightMeasureSpec)
    }


    override fun onDraw(canvas: Canvas?) {
        val fontMetrics = textPaint.fontMetrics
        val offsetY: Float = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        textPaint.color = if (isPraised) {
            //如果点过赞，就换做点赞后的颜色
            getColor(R.color.qa_praise_text_color)
        } else {
            getColor(R.color.qa_question_bottom_count_color)
        }
        canvas?.drawText(praiseCount.toString(), width / 2f + context.dp2px(18f), height / 2f + offsetY, textPaint)
        canvas?.translate(0f, -7f)
        super.onDraw(canvas)
    }

    /**
     * 传入id和model，注册LikeView的监听，并且自动取消原来的监听
     */
    fun registerLikeView(id: String, model: String, isPraised: Boolean, praiseCount: Int) {
        // Log.e("sandyzhang", "[$id:$model], isPraised = $isPraised, praiseCount = $praiseCount")
        if (id == "0") {
            throw IllegalStateException("id must not be 0")
        }

        // 取消原先的订阅
        observer["${this.id}-${this.model}"]?.remove(weakP)

        this.id = id
        this.model = model

        // 查询map中是否有记录
        if (likeMap["$id-$model"] == null) {
            // 如果map中没有记录，则新建
            this.praiseCount = praiseCount
            this.isPraised = isPraised
            likeMap["$id-$model"] = Pair(this.praiseCount, this.isPraised)
        } else {

            this.isPraised = likeMap["$id-$model"]?.second ?: false
            // 如果map中有记录是否点赞。则根据外部传来的真实点赞值（除了自己），加上自己是否点赞
            this.praiseCount = praiseCount + (if (isPraised) -1 else 0) + (if (this.isPraised) 1 else 0) // likeMap["$id-$model"]?.first ?: 0

            likeMap["$id-$model"] = Pair(this.praiseCount, this.isPraised)
        }

        // 注册订阅者
        weakP = WeakReference(this)
        if (observer["${this.id}-${this.model}"] == null) {
            observer["${this.id}-${this.model}"] = arrayListOf()
        }
        observer["${this.id}-${this.model}"]?.add(weakP!!)

        setCheckedWithoutAnimator(this.isPraised)
        invalidate()
    }

    // 根据id和model去map中寻找对应的“帖子/动态”点赞数和是否点赞
    private fun notifyData() {
        val isPraisedData = likeMap["$id-$model"]?.second ?: false
        praiseCount = likeMap["$id-$model"]?.first ?: 0
        if (isPraisedData != isPraised) {
            setCheckedWithoutAnimator(isPraisedData)
            isPraised = isPraisedData
        }
        invalidate()
    }

    // 通知订阅者
    private fun sendBroadcast(key: String) {
        observer[key]?.apply {
            val itr = observer[key]!!.iterator()
            while (itr.hasNext()) {
                val weakR = itr.next()
                if (weakR.get() == null) {
                    // 如果view已经被回收，则删除订阅（弱引用）
                    itr.remove()
                } else {
                    weakR.get()?.notifyData()
                }
            }
        }
    }


    fun click() {
        val tmpId = this.id
        val tmpModel = this.model
        val originIsPraised = isPraised
        val originPraiseCount = praiseCount

        sendBroadcast("$tmpId-$tmpModel")

        if (isLoading) return
        isLoading = true

        if (isPraised) {
            isPraised = false
            praiseCount -= 1
            textPaint.color = getColor(R.color.qa_question_bottom_count_color)
        } else {
            isPraised = true
            praiseCount += 1
            textPaint.color = getColor(R.color.qa_praise_text_color)
        }

        isChecked = isPraised
        likeMap["$tmpId-$tmpModel"] = Pair(praiseCount, isPraised)
        invalidate()
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .praise(tmpId, tmpModel)
                .checkError()
                .setSchedulers()
                .doOnError {
                    // 如果失败，则通知所有订阅了的view，回到原始状态
                    likeMap["$tmpId-$tmpModel"] = Pair(originPraiseCount, originIsPraised)
                    sendBroadcast("$tmpId-$tmpModel")
                }.doFinally {
                    isLoading = false
                }.safeSubscribeBy {
                    // 如果成功，则保持
                    sendBroadcast("$tmpId-$tmpModel")
                }

    }

    private fun getColor(res: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(res, null)
        } else {
            resources.getColor(res)
        }
    }

}