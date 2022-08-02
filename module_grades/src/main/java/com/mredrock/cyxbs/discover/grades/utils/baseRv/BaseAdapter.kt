package com.mredrock.cyxbs.discover.grades.utils.baseRv

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
@Deprecated("以前傻逼学长写的傻逼 BaseAdapter")
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var nowPosition = 0//当前位置
    private lateinit var footerTv: TextView//自定义文字
    private var isInitTv: Boolean = false

    var positionListener: ((position: Int) -> Unit)? = null
    var loadMoreListener: (() -> Unit)? = null
    var isShowFooter: Boolean = true
}