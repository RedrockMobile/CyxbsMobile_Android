package com.mredrock.cyxbs.mine.page.feedback.history.list.adapter

import android.view.View
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemBannerAddBinding
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemBannerPicBinding
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvBinder
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvHolder
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.Pic

/**
 *@author ZhiQiang Tu
 *@time 2021/8/25  0:05
 *@signature 我们不明前路，却已在路上
 */
class PicBannerBinderAdd : RvBinder<MineRecycleItemBannerAddBinding>() {
    private var listener: ((View, Int) -> Unit)? = null

    var tag: Long = 0

    override fun layoutId(): Int = R.layout.mine_recycle_item_banner_add

    fun setClickListener(listener: ((View, Int) -> Unit)?) {
        this.listener = listener
    }

    override fun onBind(holder: RvHolder) {
        binding?.apply {
            data = this@PicBannerBinderAdd
            this.holder = holder
        }
    }

    //点击事件对外暴露
    fun onClicked(view: View, holder: RvHolder) {
        //防止重复点击
        val time = System.currentTimeMillis()
        if (time - tag < 500) return
        tag = time
        val position = holder.adapterPosition
        listener?.invoke(view, position)
    }

    override fun areContentsTheSame(oldItem: RvBinder<*>): Boolean = true
}

class PicBannerBinderPic(private val pic: Pic) : RvBinder<MineRecycleItemBannerPicBinding>() {
    private var iconClicked: ((View, Int) -> Unit)? = null
    private var contentClicked: ((View, Int) -> Unit)? = null
    var tag: Long = 0

    override fun layoutId(): Int = R.layout.mine_recycle_item_banner_pic

    fun setOnContentClickListener(listener: (View, Int) -> Unit) {
        this.contentClicked = listener
    }

    fun setOnIconClickListener(listener: (View, Int) -> Unit) {
        this.iconClicked = listener
    }

    override fun onBind(holder: RvHolder) {
        binding?.apply {
            rtdlBannerPic.setOnContentClickListener {
                //防止重复点击
                val time = System.currentTimeMillis()
                if (time - tag < 500) return@setOnContentClickListener
                tag = time
                val position = holder.adapterPosition
                contentClicked?.invoke(it, position)
            }

            rtdlBannerPic.setOnIconClickListener {
                //防止重复点击
                val time = System.currentTimeMillis()
                if (time - tag < 500) return@setOnIconClickListener
                tag = time
                val position = holder.adapterPosition
                iconClicked?.invoke(it, position)
            }
        }

        binding?.data = pic

    }

    override fun areContentsTheSame(oldItem: RvBinder<*>): Boolean = true

}
