package com.mredrock.cyxbs.noclass.page.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.widget.MyFlexLayout

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      NoClassGatheringAdapter
 * @Author:         Yan
 * @CreateDate:     2022年09月11日 01:09:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    思路实现：就是先用一整个flex，然后记录下来每一行的view，然后每页添加两行。一页添加，满了就回调添加页面，添加页面，重新开始
 */
class NoClassGatheringAdapter(
    private val context: Context,
    private val mBusyNameList: List<String>,
    private val mSetIndicatorNum: ((Int) -> Unit),
) : RecyclerView.Adapter<NoClassGatheringAdapter.VH>() {

    /**
     * 页面的数量，也是指示点的数量
     */
    private var pageSize = 1

    /**
     * 所有的childCount
     */
    private val mPageItemNum by lazy { ArrayList<Int>() }

    /**
     * 所有child的高度
     */
    private val mChildHeight by lazy { ArrayList<Float>() }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myFlexLayout: MyFlexLayout = itemView.findViewById(R.id.noclass_gl_container)

        init {
            myFlexLayout.setOnLayoutCallBack {
                pageSize = it.size
                mSetIndicatorNum(pageSize)
            }
            // 第一次初始化一下，最多十行，也就是五页
            setView(mBusyNameList)
            // 获取完之后让每个可见度为GONE
            for (i in 0 until myFlexLayout.childCount) {
                val child = myFlexLayout.getChildAt(i)
                Log.d("lx", "y:${child.y} ")
                mChildHeight.add(child.y)
                child.gone()
            }
        }

        /**
         * 这里是将所有的view添加到一个大的flexLayout中
         */
        private fun setView(nameList: List<String>) {
            // 先来获取之前所有的item数
            var noClassView: View
            var linearLayoutView: LinearLayout
            var textView: TextView
            for (element in mBusyNameList) {
                noClassView = noclassView(context)
                linearLayoutView =
                    noClassView.findViewById(R.id.noclass_ll_gathering_item_container)
                textView = linearLayoutView.getChildAt(0) as TextView
                textView.text = element
                myFlexLayout.addView(noClassView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.noclass_layout_gathering, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // 是否应该分页
        var isSplit: Boolean = false
        val before = mPageItemNum.filter { mPageItemNum.indexOf(it) < position }.sum()
        Log.d("lx", "onBindViewHolder:before=${before} ")
        var y = holder.myFlexLayout.getChildAt(before).y
        for (i in before until mBusyNameList.size) {
            val child = holder.myFlexLayout.getChildAt(i)
            if (mChildHeight[i] == y) {
                Log.d("lx", "onBindViewHolder:child.y=${child.y}  y=${y} ")
                child.visible()
            } else {
                Log.d("lx", "onBindViewHolder: 轮到这了")
                if (!isSplit) {
                    y = child.y
                    isSplit = true
                } else {
                    mPageItemNum[position] = i
                    Log.d("lx", "onBindViewHolder: i = ${i}")
                    break
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return pageSize
    }

    /**
     * viewpager2一页的每一项
     */
    private fun noclassView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering, null).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 14, 12, 14)
            }
        }
    }

    /**
     * 设置页面的个数,连带指示器的个数
     */
    fun setPageSize(size: Int) {
        if (size > pageSize) {
            pageSize = size
            mSetIndicatorNum(pageSize)
        }
    }
}