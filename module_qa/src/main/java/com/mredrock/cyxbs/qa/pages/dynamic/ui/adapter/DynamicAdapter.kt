package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
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
class DynamicAdapter(private val onItemClickEvent: (Dynamic, View) -> Unit) : BaseEndlessRvAdapter<Dynamic>(DIFF_CALLBACK as DiffUtil.ItemCallback<Dynamic>) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Dynamic>() {
            override fun areItemsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem == newItem
        }

        @JvmStatic
        val PIC_URL_BASE = "https://cyxbsmobile.redrock.team/app/index.php" // 临时前缀
    }

    private var shouldAnimateSet: MutableSet<Int> = HashSet()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DynamicViewHolder(parent)

    //给问题列表首个加上背景，因为还有header，不方便加在布局
    override fun onBindViewHolder(holder: BaseViewHolder<Dynamic>, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == 0) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.qa_ic_question_list_top_background)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.common_qa_question_list_color)
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Dynamic>, position: Int, data: Dynamic) {
        super.onItemClickListener(holder, position, data)
        if (holder !is DynamicViewHolder) return
        onItemClickEvent.invoke(data, holder.itemView.findViewById<ConstraintLayout>(R.id.qa_ctl_dynamic))
    }

    class DynamicViewHolder(parent: ViewGroup) : BaseViewHolder<Dynamic>(parent, R.layout.qa_recycler_item_dynamic) {
        override fun refresh(data: Dynamic?) {
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
                    data.pics.map {
                        PIC_URL_BASE + it
                    }.apply {
                        val tag = qa_dynamic_nine_grid_view.tag
                        if (null == tag || tag == this) {
                            val tagStore = qa_dynamic_nine_grid_view.tag
                            qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_dynamic_nine_grid_view.tag = tagStore
                        } else {
                            val tagStore = this
                            qa_dynamic_nine_grid_view.tag = null
                            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                            qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                            qa_dynamic_nine_grid_view.tag = tagStore
                        }
                    }

                }
                qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.pics.map { PIC_URL_BASE + it }.toTypedArray(), index)
                }
            }
        }
    }
}