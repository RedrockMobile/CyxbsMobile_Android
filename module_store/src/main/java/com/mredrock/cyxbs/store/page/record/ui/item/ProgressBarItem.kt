package com.mredrock.cyxbs.store.page.record.ui.item

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.common.utils.SimpleRvAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/4
 * @time 13:36
 */
class ProgressBarItem(
    position: Int // 传入该 ProgressBar 显示在 RecyclerView 底部的位置
) : SimpleRvAdapter.VHItem<ProgressBarItem.ProgressBarVH, Int>(
    mapOf(Pair(position, 0)), R.layout.store_recycler_item_record_get_progress_bar
) {

    private var mPosition  = position // 在 RecyclerView 中的位置
    private var mIsHide = false // 是否隐藏

    /**
     * 用于传入新数据使用差分刷新
     */
    fun refresh(position: Int) {
        mPosition = position
        diffRefreshAllItemMap(mapOf(Pair(position, 0)),
            isSameName = { oldData, newData -> oldData == newData },
            isSameData = { _, _ ->  false }
        )
    }

    /**
     * 在没有更多的数据时隐藏 ProgressBar
     */
    fun hideProgressBarWhenNoMoreData() {
        mIsHide = true
        refreshSelfMap(listOf(0), mPosition)
    }

    class ProgressBarVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.store_progressBar_fragment_record_get_progress)
    }

    override fun getNewViewHolder(itemView: View): ProgressBarVH {
        return ProgressBarVH(itemView)
    }

    override fun onCreate(holder: ProgressBarVH, map: Map<Int, Int>) {
    }

    override fun onRefactor(holder: ProgressBarVH, position: Int, value: Int) {
        if (holder.progressBar.visibility == View.INVISIBLE) {
            holder.progressBar.visible()
        }
        if (mIsHide) {
            holder.progressBar.invisible()
        }
        if (position < 15) { // 位置小于 15 时可能是在屏幕内, 就不展示
            holder.progressBar.invisible()
        }
    }
}