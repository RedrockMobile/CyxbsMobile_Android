package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.TestData
import com.mredrock.cyxbs.qa.ui.widget.ImageViewAddCount
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow


/**
 * @Author: xgl
 * @ClassName: CirclesAdapter
 * @Description:
 * @Date: 2020/11/18 23:18
 */
class CirclesAdapter(val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val NO_CIRCLE = 0
        const val HAVE_CIRCLE = 1
    }

    private val circlesItemList = ArrayList<TestData>()

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
//                    contentView = deleteCommentPopWindow.contentView
//                    //测量popwindow的宽高.
//                    contentView?.measure(deleteCommentPopWindow.makeDropDownMeasureSpec(deleteCommentPopWindow.width),
//                            deleteCommentPopWindow.makeDropDownMeasureSpec(deleteCommentPopWindow.height))
//                    offsetX=-((deleteCommentPopWindow.contentView.measuredWidth-it.width)/2)
//                    PopupWindowCompat.showAsDropDown(deleteCommentPopWindow,it,offsetX,offsetY,Gravity.START)
//                    showBackgroundAnimator()
                    OptionalPopWindow.Builder().with(mContext)
                            .addOptionAndCallback("删除") {
                                Toast.makeText(context, "点击了删除", Toast.LENGTH_SHORT).show()
                            }.addOptionAndCallback("关注") {
                                Toast.makeText(context, "点击了关注", Toast.LENGTH_SHORT).show()
                            }.addOptionAndCallback("举报") {
                                Toast.makeText(context, "点击了举报", Toast.LENGTH_SHORT).show()
                            }.show(it, OptionalPopWindow.AlignMode.MIDDLE, 20)

//                    changeToActivity(CircleSquareActivity())
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

}