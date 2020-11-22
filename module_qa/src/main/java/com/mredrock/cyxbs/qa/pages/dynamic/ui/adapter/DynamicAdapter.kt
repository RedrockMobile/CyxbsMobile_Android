package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.questionTimeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic.view.*

/**
 * @Author: xgl
 * @ClassName: DynamicAdapter
 * @Description:
 * @Date: 2020/11/17 20:11
 */
class DynamicAdapter(private val onItemClickEvent: (Question) -> Unit) : BaseEndlessRvAdapter<Question>(DIFF_CALLBACK as DiffUtil.ItemCallback<Question>) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
        }
    }

    private var shouldAnimateSet: MutableSet<Int> = HashSet()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DynamicViewHolder(parent)

    //给问题列表首个加上背景，因为还有header，不方便加在布局
    override fun onBindViewHolder(holder: BaseViewHolder<Question>, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == 0) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.qa_ic_question_list_top_background)
        } else {
            holder.itemView.apply {

                tv_dynamic_praise_count_image.setOnSingleClickListener {
                    tv_dynamic_praise_count_image.toggle()
                }
            }
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.common_qa_question_list_color)
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        if (holder !is DynamicViewHolder) return
        onItemClickEvent.invoke(data)
    }

    class DynamicViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_dynamic) {
        private var photoUrls = ArrayList<String>()
        private var count = 0
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                iv_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_nickname.text = data.nickname
                tv_title.text = data.title
                tv_dynamic_praise_count.text = 999.toString()
                tv_dynamic_count.text = 999.toString()
                tv_publish_at.text = questionTimeDescription(System.currentTimeMillis(), data.createdAt.toDate().time)
                //解决图片错乱的问题
                if (data.photoUrl.isNullOrEmpty())
                    nine_grid_view_dynamic.setRectangleImages(emptyList())
                else {
                    val tag = nine_grid_view_dynamic.tag
                    if (null == tag || tag == data.photoUrl) {
                        val tagStore = nine_grid_view_dynamic.tag
                        nine_grid_view_dynamic.setRectangleImages(setPhotoUrls(data.photoUrl))
                        nine_grid_view_dynamic.tag = tagStore
                    } else {
                        val tagStore = data.photoUrl
                        nine_grid_view_dynamic.tag = null
                        nine_grid_view_dynamic.setRectangleImages(emptyList())
                        nine_grid_view_dynamic.setRectangleImages(setPhotoUrls(data.photoUrl))
                        nine_grid_view_dynamic.tag = tagStore
                    }
                }
                nine_grid_view_dynamic.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl.toTypedArray(), index)
                }
            }
        }

        fun setPhotoUrls(photoUrl: List<String>): ArrayList<String> {
            photoUrls.clear()
            for (it in photoUrl)
                if (count < 3) {
                    this.photoUrls.add(it)
                    count++
                }
            return photoUrls
        }
    }
}