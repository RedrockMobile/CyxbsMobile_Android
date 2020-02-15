package com.mredrock.cyxbs.qa.pages.question.ui

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.BaseEndlessRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.answer.ui.AnswerListActivity
import com.mredrock.cyxbs.qa.pages.main.QuestionContainerFragment
import com.mredrock.cyxbs.qa.utils.setNicknameTv
import kotlinx.android.synthetic.main.qa_recycler_item_question.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/26.
 */
class QuestionListRvAdapter(private val fragment: Fragment) : BaseEndlessRvAdapter<Question>(DIFF_CALLBACK) {
    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Question>, position: Int, data: Question) {
        super.onItemClickListener(holder, position, data)
        if (BaseApp.isLogin) {
            AnswerListActivity.activityStart(fragment, data, QuestionContainerFragment.REQUEST_LIST_REFRESH_ACTIVITY)
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用邮问哦~"))
        }
    }

    class QuestionViewHolder(parent: ViewGroup) : BaseViewHolder<Question>(parent, R.layout.qa_recycler_item_question) {
        override fun refresh(data: Question?) {
            data ?: return
            itemView.apply {
                iv_avatar.setAvatarImageFromUrl(data.photoThumbnailSrc)
                tv_nickname.setNicknameTv(data.nickname, data.isEmotion && !data.isAnonymous, data.isMale)
                tv_title.text = data.title
                tv_reward_count.text = data.reward.toString()
                tv_answer_count.text = data.answerNum.toString()
                tv_view_count.text = data.viewCount.toString()
                tv_publish_at.text = data.createdAt.split(" ")[0].replace("-", "/")
            }
        }

    }
}