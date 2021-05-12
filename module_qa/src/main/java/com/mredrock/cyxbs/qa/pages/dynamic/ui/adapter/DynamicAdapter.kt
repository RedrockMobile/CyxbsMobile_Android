package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.config.CommentConfig.COPY_LINK
import com.mredrock.cyxbs.qa.config.CommentConfig.DELETE
import com.mredrock.cyxbs.qa.config.CommentConfig.FOLLOW
import com.mredrock.cyxbs.qa.config.CommentConfig.IGNORE
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_FRIEND
import com.mredrock.cyxbs.qa.config.CommentConfig.QQ_ZONE
import com.mredrock.cyxbs.qa.config.CommentConfig.REPORT
import com.mredrock.cyxbs.qa.config.CommentConfig.UN_FOLLOW
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.ui.widget.ShareDialog
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*

/**
 * @Author: xgl
 * @ClassName: DynamicAdapter
 * @Description:
 * @Date: 2020/11/17 20:11
 */
class DynamicAdapter(val context: Context, private val onItemClickEvent: (Dynamic, View) -> Unit) : BaseEndlessRvAdapter<Dynamic>(DIFF_CALLBACK as DiffUtil.ItemCallback<Dynamic>) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Dynamic>() {
            override fun areItemsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Dynamic, newItem: Dynamic) = oldItem == newItem
        }
    }

    var onShareClickListener: ((Dynamic, String) -> Unit)? = null
    var onTopicListener: ((String, View) -> Unit)? = null
    var onPopWindowClickListener: ((Int, String, Dynamic) -> Unit)? = null

    var curSharedItem: View? = null
    var curSharedDynamic: Dynamic? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DynamicViewHolder(parent)

    override fun onBindViewHolder(holder: BaseViewHolder<Dynamic>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            qa_iv_dynamic_share.setOnSingleClickListener {
                ShareDialog(context).apply {
                    initView(onCancelListener = View.OnClickListener {
                        dismiss()
                    }, qqshare = View.OnClickListener {
                        getItem(position)?.let { it1 -> onShareClickListener?.invoke(it1, QQ_FRIEND) }
                    }, qqZoneShare = View.OnClickListener {
                        getItem(position)?.let { it1 -> onShareClickListener?.invoke(it1, QQ_ZONE) }
                    }, weChatShare = View.OnClickListener {
                        CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()
                    }, friendShipCircle = View.OnClickListener {
                        CyxbsToast.makeText(context, R.string.qa_share_wechat_text, Toast.LENGTH_SHORT).show()
                    }, copylink = View.OnClickListener {
                        getItem(position)?.let { it1 -> onShareClickListener?.invoke(it1, COPY_LINK) }
                    })
                }.show()
            }
            qa_tv_dynamic_topic.setOnSingleClickListener {
                getItem(position)?.topic?.let { it1 -> onTopicListener?.invoke(it1, it) }
            }
            qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener { view ->
                getItem(position)?.let { dynamic ->
                    if (dynamic.isSelf == 0) {
                        if (dynamic.isFollowTopic == 0) {
                            OptionalPopWindow.Builder().with(context)
                                    .addOptionAndCallback(IGNORE) {
                                        onPopWindowClickListener?.invoke(position, IGNORE, dynamic)
                                    }.addOptionAndCallback(REPORT) {
                                        onPopWindowClickListener?.invoke(position, REPORT, dynamic)
                                    }.addOptionAndCallback(FOLLOW) {
                                        onPopWindowClickListener?.invoke(position, FOLLOW, dynamic)
                                    }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                        } else {
                            OptionalPopWindow.Builder().with(context)
                                    .addOptionAndCallback(IGNORE) {
                                        onPopWindowClickListener?.invoke(position, IGNORE, dynamic)
                                    }.addOptionAndCallback(REPORT) {
                                        onPopWindowClickListener?.invoke(position, REPORT, dynamic)
                                    }.addOptionAndCallback(UN_FOLLOW) {
                                        onPopWindowClickListener?.invoke(position, UN_FOLLOW, dynamic)
                                    }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                        }
                    } else {
                        OptionalPopWindow.Builder().with(context)
                                .addOptionAndCallback(DELETE) {
                                    onPopWindowClickListener?.invoke(position, DELETE, dynamic)
                                }.show(view, OptionalPopWindow.AlignMode.RIGHT, 0)
                    }
                }
            }
        }
    }

    override fun onItemClickListener(holder: BaseViewHolder<Dynamic>, position: Int, data: Dynamic) {
        super.onItemClickListener(holder, position, data)
        if (holder !is DynamicViewHolder) return
        curSharedDynamic = data
        curSharedItem = holder.itemView
        onItemClickEvent.invoke(data, holder.itemView.findViewById<ConstraintLayout>(R.id.qa_ctl_dynamic))
    }

    class DynamicViewHolder(parent: ViewGroup) : BaseViewHolder<Dynamic>(parent, R.layout.qa_recycler_item_dynamic_header) {
        override fun refresh(data: Dynamic?) {
            data ?: return
            itemView.apply {
                qa_iv_dynamic_praise_count_image.registerLikeView(data.postId, CommentConfig.PRAISE_MODEL_DYNAMIC, data.isPraised, data.praiseCount)
                qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                    qa_iv_dynamic_praise_count_image.click()
                }
                qa_iv_dynamic_avatar.setAvatarImageFromUrl(data.avatar)
                qa_tv_dynamic_topic.text = "# " + data.topic
                qa_tv_dynamic_nickname.text = data.nickName
                qa_tv_dynamic_content.setContent(data.content)
                qa_tv_dynamic_comment_count.text = data.commentCount.toString()
                qa_tv_dynamic_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                //解决图片错乱的问题
                if (data.pics.isNullOrEmpty())
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                else {
                    data.pics.apply {
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