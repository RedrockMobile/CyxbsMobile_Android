package com.mredrock.cyxbs.qa.pages.square.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.DynamicAdapter
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.questionTimeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic.view.*

/**
 *@Date 2020-11-22
 *@Time 20:12
 *@author SpreadWater
 *@description  圈子详情页的adapter
 */
class CircleDetailAdapter(private val onItemClickEvent: (Dynamic) -> Unit) : BaseEndlessRvAdapter<Dynamic>(DIFF_CALLBACK as DiffUtil.ItemCallback<Dynamic>) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Dynamic>() {
            override fun areItemsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem == newItem
        }
        @JvmStatic
        val PIC_URL_BASE = "https://cyxbsmobile.redrock.team/app/index.php" // 临时前缀
    }

    //给问题列表首个加上背景，因为还有header，不方便加在布局
    override fun onBindViewHolder(holder: BaseViewHolder<Dynamic>, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == 0) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.qa_ic_question_list_top_background)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.common_qa_question_list_color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CircleDetailViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Dynamic>, position: Int, data: Dynamic) {
        super.onItemClickListener(holder, position, data)
        if (holder !is CircleDetailViewHolder) return
        onItemClickEvent.invoke(data)
    }

    class CircleDetailViewHolder(parent: ViewGroup) : BaseViewHolder<Dynamic>(parent, R.layout.qa_recycler_item_dynamic) {
        private var photoUrls = ArrayList<String>()
        override fun refresh(data: Dynamic?) {
            data ?: return
            itemView.apply {
                qa_iv_dynamic_avatar.setAvatarImageFromUrl(PIC_URL_BASE + data.avatar)
                qa_tv_dynamic_topic.text = data.topic
                qa_tv_dynamic_nickname.text = data.nickName
                qa_tv_dynamic_content.text = data.content
                qa_tv_dynamic_praise_count.text = data.praiseCount.toString()
                qa_tv_dynamic_comment_count.text = data.commentCount.toString()
                qa_tv_dynamic_publish_at.text = questionTimeDescription(System.currentTimeMillis(), data.publishTime.toString().toDate().time)
                //解决图片错乱的问题
                if (data.pics.isNullOrEmpty())
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    val tag = qa_dynamic_nine_grid_view.tag
                    if (null == tag || tag == data.pics) {
                        val tagStore = qa_dynamic_nine_grid_view.tag
                        qa_dynamic_nine_grid_view.setImages(data.pics, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                        qa_dynamic_nine_grid_view.tag = tagStore
                    } else {
                        val tagStore = data.pics
                        qa_dynamic_nine_grid_view.tag = null
                        qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                        qa_dynamic_nine_grid_view.setImages(data.pics, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                        qa_dynamic_nine_grid_view.tag = tagStore
                    }
                }
                qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                    qa_iv_dynamic_praise_count_image.toggle()
                }
                qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.pics.map {PIC_URL_BASE + it }.toTypedArray(), index)
                }
            }
        }

        fun setPhotoUrls(photoUrl: List<String>): ArrayList<String> {
            photoUrls.clear()
            var count = 0
            for (it in photoUrl)
                if (count < 3) {
                    this.photoUrls.add(it)
                    count++
                }
            return photoUrls
        }
    }
}