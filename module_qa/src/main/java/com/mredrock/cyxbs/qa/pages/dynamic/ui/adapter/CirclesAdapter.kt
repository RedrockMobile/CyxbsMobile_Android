package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.PopupWindowCompat
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.TestData
import com.mredrock.cyxbs.qa.ui.widget.DeleteCommentPopWindow
import com.mredrock.cyxbs.qa.ui.widget.ImageViewAddCount


/**
 * @Author: xgl
 * @ClassName: CirclesAdapter
 * @Description:
 * @Date: 2020/11/18 23:18
 */
class CirclesAdapter(val mcontext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val NO_CIRCLE = 0
        const val HAVE_CIRCLE = 1
    }

    private val circlesItemList = ArrayList<TestData>()
    private val deleteCommentPopWindow = DeleteCommentPopWindow(mcontext)
    private var contentView: View? = null
    var offsetX=0
    var offsetY = 0
    var mAlpha=0.2f
    class NoCircleItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_add_circles: ImageView = itemView.findViewById(R.id.iv_add_circles)
    }

    class CirclesItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_circle: ImageViewAddCount = itemView.findViewById(R.id.iv_circle)
        val tv_circle_name: TextView = itemView.findViewById(R.id.tv_circle_name)
    }

    override fun getItemViewType(position: Int): Int {
        return if (circlesItemList.size == 0)
            NO_CIRCLE
        else
            HAVE_CIRCLE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_CIRCLE -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.qa_recycler_item_no_circles,
                        parent,
                        false
                )
                return NoCircleItem(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.qa_recycler_item_circles,
                        parent,
                        false
                )
                return CirclesItem(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            NO_CIRCLE -> {
                val viewHolder = holder as NoCircleItem
                viewHolder.iv_add_circles.setOnSingleClickListener {
                }
            }

            HAVE_CIRCLE -> {
                val viewHolder = holder as CirclesItem
                viewHolder.iv_circle.apply {
                    setHaveMessage(true)
                    setMessageBum(50)
                }
                viewHolder.iv_circle.setOnClickListener {
                    /*
                        popwindow弹出测试
                     */
                    contentView = deleteCommentPopWindow.contentView
                    //测量popwindow的宽高.
                    contentView?.measure(deleteCommentPopWindow.makeDropDownMeasureSpec(deleteCommentPopWindow.width),
                            deleteCommentPopWindow.makeDropDownMeasureSpec(deleteCommentPopWindow.height))
                    offsetX=-(deleteCommentPopWindow.contentView.measuredWidth-it.width/2)
                    PopupWindowCompat.showAsDropDown(deleteCommentPopWindow,it,offsetX,offsetY,Gravity.START)
                    showBackgroundAnimator()
//                    changeToActivity(CircleSquareActivity())
                }
                deleteCommentPopWindow.setOnDismissListener {
                    hideBackgroundAnimator()
                }
                viewHolder.tv_circle_name.text = circlesItemList[position].circleName
            }
        }
    }

    override fun getItemCount(): Int {
        return circlesItemList.size
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(context, activity::class.java)
        context.startActivity(intent)
    }

    fun addData(newDataLists: List<TestData>) {
        circlesItemList.clear()
        initRefreshImages(newDataLists)
    }

    fun initRefreshImages(dataLists: List<TestData>) {
        circlesItemList.addAll(dataLists)
        notifyDataSetChanged()
    }

    /*
        设置popwindow的背景色
     */
     fun setWindowBackgroundAlpha(alpha: Float) {
        if (mcontext is Activity) {
            val window: Window = mcontext.window
            val layoutParams: WindowManager.LayoutParams = window.getAttributes()
            layoutParams.alpha = alpha
            window.setAttributes(layoutParams)
        }
    }

    /**
     * 窗口显示，窗口背景透明度渐变动画
     */
    fun showBackgroundAnimator() {
        if (mAlpha >= 1f) return
        val animator = ValueAnimator.ofFloat(1.0f, mAlpha)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }
    fun hideBackgroundAnimator() {
        if (mAlpha >= 1f) return
        val animator = ValueAnimator.ofFloat(0.2f, 1.0f)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }
}