package com.mredrock.cyxbs.qa.pages.question.ui

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.main.QuestionContainerFragment
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import com.mredrock.cyxbs.qa.utils.timeDescription
import com.mredrock.cyxbs.qa.utils.toDate
import kotlinx.android.synthetic.main.qa_recycle_item_question.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/26.
 */
class QuestionListRvAdapter(private val fragment: androidx.fragment.app.Fragment) : BaseEndlessRvAdapter<Question>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem?.id == newItem?.id

            override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        val context = holder.itemView.context
        if (BaseApp.isLogin) {
            AnswerListActivity.activityStart(fragment, data, QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY)
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用邮问哦~"))
        }
    }

    class QuestionViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycle_item_question) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                iv_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_nickname.setNicknameTv(data.nickname, data.isEmotion && !data.isAnonymous, data.isMale)
                setDisappear(tv_disappear_at, data.disappearAt)
                tv_tag.text = context.getString(R.string.qa_question_item_tag, data.tags)
                tv_title.text = data.title
                tv_content.text = data.description
                tv_reward.text = context.getString(R.string.qa_question_item_reward, data.reward)
                setShowPictureButton(tv_show_picture, data.photoUrl)
            }
        }

        private fun setDisappear(tv: TextView, rowTime: String) {
            tv.text = context.getString(R.string.qa_question_item_disappear,
                    timeDescription(System.currentTimeMillis(), rowTime.toDate().time))
        }

        private fun setShowPictureButton(tv: TextView, url: List<String>) {
            if (url.isEmpty()) {
                tv.gone()
            } else {
                tv.visible()
            }
        }
    }
}