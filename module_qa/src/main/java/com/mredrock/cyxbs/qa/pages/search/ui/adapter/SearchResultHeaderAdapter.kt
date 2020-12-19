package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchResultHeaderAdapter(private val onItemClickEvent: (Dynamic) -> Unit) : BaseRvAdapter<Dynamic>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Dynamic> = ViewHolder(parent)
    override fun onItemClickListener(holder: BaseViewHolder<Dynamic>, position: Int, data: Dynamic) {
        super.onItemClickListener(holder, position, data)
        onItemClickEvent.invoke(data)

    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<Dynamic>(parent, R.layout.qa_recycler_item_dynamic_header) {

        override fun refresh(data: Dynamic?) {
            if (data == null) return
            itemView.apply {
                qa_iv_dynamic_avatar.setAvatarImageFromUrl(data.avatar)
                qa_tv_dynamic_topic.text = "#" + data.topic
                qa_tv_dynamic_nickname.text = data.nickName + "xx"
                qa_tv_dynamic_content.text = data.content
                qa_tv_dynamic_comment_count.text = data.commentCount.toString()
                qa_tv_dynamic_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                //解决图片错乱的问题
                if (data.pics.isNullOrEmpty())
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    data.pics.map {
                        it
                    }.apply {
                        val tag = qa_dynamic_nine_grid_view.tag
                        if (null == tag || tag == this) {
                            val tagStore = qa_dynamic_nine_grid_view.tag
                            qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_dynamic_nine_grid_view.tag = tagStore
                            LogUtils.d("zt","111111测试"+this.get(0))
                        } else {
                            val tagStore = this
                            qa_dynamic_nine_grid_view.tag = null
                            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                            qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_dynamic_nine_grid_view.tag = tagStore
                            LogUtils.d("zt","111111测试"+this.get(0))
                        }
                    }

                }
                qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.pics.map { it }.toTypedArray(), index)
                }
            }
        }
    }
}