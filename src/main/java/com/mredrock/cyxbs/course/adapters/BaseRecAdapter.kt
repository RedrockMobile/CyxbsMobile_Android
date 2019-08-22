package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * 这个类作为所有RecyclerView的Adapter的基类。然后通过[getThePositionLayoutId]来获取对应位置的View的LayoutId
 * 所有有View的操作逻辑放在[onBindViewHolder]中，这样可以不用自己重写ViewHolder了。当有多个LayoutId的时候，
 * 你只需要在[onBindViewHolder]中进行不同数据类型的判断然后进行不同操作就行。
 *
 * Created by anriku on 2018/9/10.
 */
abstract class BaseRecAdapter(protected val context: Context) :
        androidx.recyclerview.widget.RecyclerView.Adapter<BaseRecAdapter.BaseViewHolder>() {

    protected val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflater.inflate(viewType, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return getThePositionLayoutId(position)
    }

    /**
     * @return 返回对应位置View的LayoutId
     */
    @LayoutRes
    abstract fun getThePositionLayoutId(position: Int): Int

    abstract override fun getItemCount(): Int

    abstract override fun onBindViewHolder(holder: BaseViewHolder, position: Int)

    class BaseViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}