package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
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
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.common_qa_question_list_color)
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        if (holder !is DynamicViewHolder) return
        onItemClickEvent.invoke(data)
    }

    class DynamicViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_dynamic) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {
                    OptionalPopWindow.Builder().with(context)
                            .addOptionAndCallback("删除") {
                                Toast.makeText(BaseApp.context, "点击了删除", Toast.LENGTH_SHORT).show()
                            }.addOptionAndCallback("关注") {
                                Toast.makeText(BaseApp.context, "点击了关注", Toast.LENGTH_SHORT).show()
                            }.addOptionAndCallback("举报") {
                                Toast.makeText(BaseApp.context, "点击了举报", Toast.LENGTH_SHORT).show()
                            }.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
                }
                qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                    qa_iv_dynamic_praise_count_image.toggle()
                }
                qa_iv_dynamic_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                qa_tv_dynamic_nickname.text = data.nickname
                qa_tv_dynamic_content.text = data.title
                qa_tv_dynamic_praise_count.text = 999.toString()
                qa_tv_dynamic_count.text = 999.toString()
                qa_tv_dynamic_publish_at.text = questionTimeDescription(System.currentTimeMillis(), data.createdAt.toDate().time)
                //解决图片错乱的问题
                if (data.photoUrl.isNullOrEmpty())
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    val tag = qa_dynamic_nine_grid_view.tag
                    if (null == tag || tag == data.photoUrl) {
                        val tagStore = qa_dynamic_nine_grid_view.tag
                        qa_dynamic_nine_grid_view.setImages(data.photoUrl, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                        qa_dynamic_nine_grid_view.tag = tagStore
                    } else {
                        val tagStore = data.photoUrl
                        qa_dynamic_nine_grid_view.tag = null
                        qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                        qa_dynamic_nine_grid_view.setImages(data.photoUrl, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                        qa_dynamic_nine_grid_view.tag = tagStore
                    }
                }
                qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl.toTypedArray(), index)
                }
            }
        }
    }
}