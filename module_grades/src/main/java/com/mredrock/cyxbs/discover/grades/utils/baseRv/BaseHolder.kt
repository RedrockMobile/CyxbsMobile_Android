package com.mredrock.cyxbs.discover.grades.utils.baseRv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun getHolder(context: Context,
                      parent: ViewGroup,
                      layoutIds: Int): BaseHolder {
            return if (layoutIds != 2000) {
                BaseHolder(LayoutInflater.from(context).inflate(layoutIds, parent, false))
            } else {
                BaseHolder(BaseFooter(context))
            }
        }
    }
}