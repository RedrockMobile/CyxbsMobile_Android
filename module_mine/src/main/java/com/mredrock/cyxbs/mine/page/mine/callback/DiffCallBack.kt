package com.mredrock.cyxbs.mine.page.mine.callback

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus

/**
 * @ClassName DiffCallBack
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/16 11:58
 * @Version 1.0
 */
class DiffCallBack(val mOldDatas:List<AuthenticationStatus.Data>, val mNewDatas:List<AuthenticationStatus.Data>): DiffUtil.Callback() {
    override fun getOldListSize()= if (mOldDatas != null) mOldDatas.size else 0

    override fun getNewListSize()= if (mNewDatas != null) mNewDatas.size else 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)=mOldDatas.get(oldItemPosition).id.equals(mNewDatas.get(newItemPosition).id)
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}