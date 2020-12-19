package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter


import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.NOTICE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.config.CommentConfig.UNNOTICE
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic.view.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply.view.*

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
    }

    var onPopWindowClickListener: ((String, Dynamic) -> Unit)? = null
    var onPraiseClickListener: ((Int, Dynamic) -> Unit)? = null
    private var shouldAnimateSet: MutableSet<Int> = HashSet()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DynamicViewHolder(parent)

    override fun onBindViewHolder(holder: BaseViewHolder<Dynamic>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener { view ->
                getItem(position)?.let { dynamic ->
                    if (dynamic._isFollowTopic == 0) {
                        OptionalPopWindow.Builder().with(context)
                                .addOptionAndCallback(NOTICE) {
                                    onPopWindowClickListener?.invoke(NOTICE, dynamic)
                                }
                                .addOptionAndCallback(IGNORE) {
                                    onPopWindowClickListener?.invoke(IGNORE, dynamic)
                                }.addOptionAndCallback(REPORT) {
                                    onPopWindowClickListener?.invoke(REPORT, dynamic)
                                }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                    } else {
                        OptionalPopWindow.Builder().with(context)
                                .addOptionAndCallback(UNNOTICE) {
                                    onPopWindowClickListener?.invoke(NOTICE, dynamic)
                                }
                                .addOptionAndCallback(IGNORE) {
                                    onPopWindowClickListener?.invoke(IGNORE, dynamic)
                                }.addOptionAndCallback(REPORT) {
                                    onPopWindowClickListener?.invoke(REPORT, dynamic)
                                }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                    }
                }
            }
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
                qa_iv_dynamic_praise_count_image.registerLikeView(data.postId, CommentConfig.PRAISEMODEL, data.isPraised, data.praiseCount)
                qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                    qa_iv_dynamic_praise_count_image.click()
                }
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
                    ViewImageActivity.activityStart(context, data.pics.map { it }.toTypedArray(), index)
                }
            }
        }
    }
}