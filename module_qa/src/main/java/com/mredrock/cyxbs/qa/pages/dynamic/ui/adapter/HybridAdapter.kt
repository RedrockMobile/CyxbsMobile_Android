package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.H5Dynamic
import com.mredrock.cyxbs.qa.beannew.Message
import com.mredrock.cyxbs.qa.beannew.MessageWrapper
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.ui.widget.OptionalPopWindow
import com.mredrock.cyxbs.qa.ui.widget.ShareDialog
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*
import kotlinx.android.synthetic.main.qa_recycler_item_h5_dynamic.view.*

/**
 * @author: RayleighZ
 * @describe: Based on DynamicAdapter, feat hybrid dynamic
 */
class HybridAdapter(val context: Context?, private val onItemClickEvent: (Dynamic, View) -> Unit) :
    BaseEndlessRvAdapter<Message>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

            //比较二者是否是统一数据类型
            override fun areItemsTheSame(oldItem: Message, newItem: Message) =
                oldItem.javaClass == newItem.javaClass

            //只需比较两者内容上是否相同
            override fun areContentsTheSame(oldItem: Message, newItem: Message) =
                oldItem.toString() == newItem.toString()
        }
    }

    var onShareClickListener: ((Dynamic, String) -> Unit)? = null
    var onTopicListener: ((String, View) -> Unit)? = null
    var onPopWindowClickListener: ((Int, String, Dynamic) -> Unit)? = null
    var onAvatarClickListener: ((redid: String) -> Unit)? = null

    var curSharedItem: View? = null
    var curSharedDynamic: Dynamic? = null
    var curSharedItemPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Message> {
        return when (viewType) {
            normalFlag -> DynamicViewHolder(parent)
            h5Flag -> {
                H5DynamicViewHolder(parent)
            }
            else -> {
                DynamicViewHolder(parent)
            }
        }
    }

    //下面是为什么不直接用NORMAL_DYNAMIC|H5_DYNAMIC的理由
    //这里的Adapter是在外部RvAdapterWrapper中的，直接使用会和外部FLAG冲突
    //为了保证后续拓展时内外不冲突，这里统一使用负值作为ViewType
    private val normalFlag = MessageWrapper.NORMAL_DYNAMIC - 2
    private val h5Flag = MessageWrapper.H5_DYNAMIC - 2

    //封装一层viewType
    override fun getItemViewType(position: Int): Int =
        getItem(position)?.let {
            if (it is Dynamic) {
                normalFlag
            } else {
                h5Flag
            }
        } ?: normalFlag


    override fun onBindViewHolder(holder: BaseViewHolder<Message>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            val data = getItem(position)
            if (data is Dynamic) {
                initDynamicItem(this, data, position)
            } else if (data is H5Dynamic) {
                holder.refresh(data)
                initH5Item(this, data, position)
            }
        }
    }

    private fun initDynamicItem(itemView: View, dynamic: Dynamic, position: Int) {
        itemView.apply {
            qa_iv_dynamic_share.setOnSingleClickListener {
                ShareDialog(context).apply {
                    initView(onCancelListener = {
                        dismiss()
                    }, qqShare = {
                        onShareClickListener?.invoke(
                            dynamic,
                            CommentConfig.QQ_FRIEND
                        )
                    }, qqZoneShare = {
                        onShareClickListener?.invoke(dynamic, CommentConfig.QQ_ZONE)
                    }, weChatShare = {
                        CyxbsToast.makeText(
                            context,
                            R.string.qa_share_wechat_text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }, friendShipCircle = {
                        CyxbsToast.makeText(
                            context,
                            R.string.qa_share_wechat_text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }, copyLink = {
                        onShareClickListener?.invoke(dynamic, CommentConfig.COPY_LINK)
                    })
                }.show()
            }
            qa_tv_dynamic_topic.setOnSingleClickListener {
                dynamic.topic.let { it1 -> onTopicListener?.invoke(it1, it) }
            }
            qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener { view ->
                if (dynamic.isSelf == 0) {
                    if (dynamic.isFollowTopic == 0) {
                        OptionalPopWindow.Builder().with(context)
                            .addOptionAndCallback(
                                CommentConfig.IGNORE,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.IGNORE, dynamic
                                )
                            }.addOptionAndCallback(
                                CommentConfig.REPORT,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.REPORT, dynamic
                                )
                            }.addOptionAndCallback(
                                CommentConfig.FOLLOW,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.FOLLOW, dynamic
                                )
                            }.showFromBottom(
                                LayoutInflater.from(context).inflate(
                                    R.layout.qa_fragment_dynamic, null, false
                                )
                            )
                    } else {
                        OptionalPopWindow.Builder().with(context)
                            .addOptionAndCallback(
                                CommentConfig.IGNORE,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.IGNORE, dynamic
                                )
                            }.addOptionAndCallback(
                                CommentConfig.REPORT,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.REPORT, dynamic
                                )
                            }.addOptionAndCallback(
                                CommentConfig.UN_FOLLOW,
                                R.layout.qa_popupwindow_option_bottom
                            ) {
                                onPopWindowClickListener?.invoke(
                                    position,
                                    CommentConfig.UN_FOLLOW, dynamic
                                )
                            }.showFromBottom(
                                LayoutInflater.from(context).inflate(
                                    R.layout.qa_fragment_dynamic, null, false
                                )
                            )
                    }
                } else {
                    OptionalPopWindow.Builder().with(context)
                        .addOptionAndCallback(
                            CommentConfig.DELETE,
                            R.layout.qa_popupwindow_option_bottom
                        ) {
                            onPopWindowClickListener?.invoke(
                                position,
                                CommentConfig.DELETE, dynamic
                            )
                        }
                        .addOptionAndCallback(
                            CommentConfig.EDIT,
                            R.layout.qa_popupwindow_option_bottom
                        ) {
                            val intent = Intent(context, QuizActivity::class.java)
                            intent.putExtra("dynamic", dynamic)
                            context?.startActivity(intent)
                        }
                        .showFromBottom(
                            LayoutInflater.from(context).inflate(
                                R.layout.qa_fragment_dynamic, null, false
                            )
                        )
                }
            }
            qa_iv_dynamic_avatar.setOnClickListener {
                onAvatarClickListener?.invoke(dynamic.uid)
            }
        }
    }

    private fun initH5Item(itemView: View, h5Dynamic: H5Dynamic, position: Int) {
        itemView.apply {

        }
    }

    override fun onItemClickListener(
        holder: BaseViewHolder<Message>,
        position: Int,
        data: Message
    ) {
        super.onItemClickListener(holder, position, data)

        //判断数据类型，执行不同逻辑
        if (data is Dynamic) {
            if (holder !is DynamicViewHolder) return
            curSharedDynamic = data
            curSharedItem = holder.itemView
            curSharedItemPosition = position
            onItemClickEvent.invoke(data, holder.itemView)
        } else if (data is H5Dynamic) {
            //跳转到web容器
            data.linkUrl?.let { url ->
                ServiceManager.getService(IProtocolService::class.java).jump(url)
            }
        }

    }

    class DynamicViewHolder(parent: ViewGroup) :
        BaseViewHolder<Message>(parent, R.layout.qa_recycler_item_dynamic_header) {
        override fun refresh(data: Message?) {
            data ?: return
            if (data is Dynamic) {
                itemView.apply {
                    qa_iv_dynamic_praise_count_image.registerLikeView(
                        data.postId,
                        CommentConfig.PRAISE_MODEL_DYNAMIC,
                        data.isPraised,
                        data.praiseCount
                    )
                    qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
                        qa_iv_dynamic_praise_count_image.click()
                    }
                    qa_iv_dynamic_avatar.setAvatarImageFromUrl(data.avatar)
                    qa_tv_dynamic_topic.text = "# " + data.topic
                    qa_tv_dynamic_nickname.text = data.nickName
                    qa_tv_dynamic_content.setContent(data.content)
                    qa_tv_dynamic_comment_count.text = data.commentCount.toString()
                    qa_tv_dynamic_publish_at.text =
                        dynamicTimeDescription(System.currentTimeMillis(), data.publishTime * 1000)
                    Glide.with(context).load(data.identityPic).into(qa_iv_dynamic_identity)
                    //解决图片错乱的问题
                    if (data.pics.isNullOrEmpty())
                        qa_dynamic_nine_grid_view.setRectangleImages(
                            emptyList(),
                            NineGridView.MODE_IMAGE_THREE_SIZE
                        )
                    else {
                        data.pics.apply {
                            val tag = qa_dynamic_nine_grid_view.tag
                            if (null == tag || tag == this) {
                                val tagStore = qa_dynamic_nine_grid_view.tag
                                qa_dynamic_nine_grid_view.setImages(
                                    this,
                                    NineGridView.MODE_IMAGE_THREE_SIZE,
                                    NineGridView.ImageMode.MODE_IMAGE_RECTANGLE
                                )
                                qa_dynamic_nine_grid_view.tag = tagStore
                            } else {
                                val tagStore = this
                                qa_dynamic_nine_grid_view.tag = null
                                qa_dynamic_nine_grid_view.setRectangleImages(
                                    emptyList(),
                                    NineGridView.MODE_IMAGE_THREE_SIZE
                                )
                                qa_dynamic_nine_grid_view.setImages(
                                    this,
                                    NineGridView.MODE_IMAGE_THREE_SIZE,
                                    NineGridView.ImageMode.MODE_IMAGE_RECTANGLE
                                )
                                qa_dynamic_nine_grid_view.tag = tagStore
                            }
                        }
                    }
                    qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
                        ViewImageActivity.activityStart(
                            context,
                            data.pics.map { it }.toTypedArray(),
                            index
                        )
                    }
                }
            }
        }
    }

    class H5DynamicViewHolder(parent: ViewGroup) :
        BaseViewHolder<Message>(parent, R.layout.qa_recycler_item_h5_dynamic) {
        override fun refresh(data: Message?) {
            LogUtils.d("Ryzen", "$data")
            if (data is H5Dynamic) {
                itemView.apply {
                    qa_iv_dynamic_h5_avatar.setAvatarImageFromUrl(data.avatar)
                    qa_tv_recycler_item_h5_nickname.text = data.nickName
                    qa_iv_h5_dynamic_pic.setImageFromUrl(data.pic)
                }
            }
        }
    }
}