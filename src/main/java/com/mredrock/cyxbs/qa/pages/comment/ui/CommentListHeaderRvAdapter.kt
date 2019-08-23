package com.mredrock.cyxbs.qa.pages.comment.ui

import android.text.Html
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.utils.setAdoptedTv
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycler_item_comment_header.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/10/8.
 */
class CommentListHeaderRvAdapter(
    private val title: String,
    private val isEmotion: Boolean,
    private val showAdoptIcon: Boolean
) : BaseRvAdapter<Answer>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            HeaderViewHolder(title, isEmotion, showAdoptIcon, parent)

    inner class HeaderViewHolder(private val title: String,
                           private val isEmotion: Boolean,
                           private val showAdoptIcon: Boolean,
                           parent: ViewGroup) : BaseViewHolder<Answer>(parent, R.layout.qa_recycler_item_comment_header) {

        override fun refresh(data: Answer?) {
            data ?: return
            itemView.apply {
                tv_question_title.text = Html.fromHtml(title)
                iv_answer_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_answer_nickname.setNicknameTv(data.nickname, isEmotion, data.isMale)
                tv_answer_content.text = data.content
                setDate(tv_answer_create_at, data.createdAt)
                ngv_answer.setImages(data.photoUrl)
                ngv_answer.setOnItemClickListener { _, index ->
                    ViewImageActivity.activityStart(context, data.photoUrl[index])
                }
                setAdoptedTv(tv_adopted, tv_adopt, data.isAdopted, showAdoptIcon)
                tv_adopt.setOnClickListener {
                    EventBus.getDefault().post(AdoptAnswerEvent(data.id))
                }
                tv_answer_count.text = context.getString(R.string.qa_comment_item_answer_count, data.commentNum)
            }
        }

        private fun setDate(dateTv: TextView, date: String) {
            val desc = timeDescription(System.currentTimeMillis(), date.toDate().time)
            if (desc.last() in '0'..'9') {
                dateTv.text = desc
            } else {
                dateTv.text = context.getString(R.string.qa_answer_list_answer_date, desc)
            }
        }

    }
}