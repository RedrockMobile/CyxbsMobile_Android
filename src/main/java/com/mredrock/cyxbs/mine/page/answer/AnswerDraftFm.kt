package com.mredrock.cyxbs.mine.page.answer

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.QA_ANSWER
import com.mredrock.cyxbs.common.event.AnswerDraftEvent
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AnswerDraft
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_answer_draft.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by roger on 2019/12/3
 */
class AnswerDraftFm : BaseRVFragment<AnswerDraft>() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AnswerViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadAnswerDraftList()
        viewModel.answerDraft.observe(this, Observer {
            setNewData(it)
        })
        viewModel.eventOnAnswerDraft.observe(this, Observer {
            setState(it)
        })
    }

    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_answer_draft
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AnswerDraft) {
        holder.itemView.mine_answer_draft_tv_content.text = data.draftAnswerContent
        holder.itemView.mine_answer_draft_tv_lastedit_at.text = data.latestEditTime.split(" ")[0].replace("-", ".")
        holder.itemView.mine_answer_draft_iv_garbage.setOnClickListener {
            CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.mine_layout_dialog_with_title_and_content,
                        positiveString = "删除",
                        onPositiveClick = {
                            viewModel.deleteDraftById(data.draftAnswerId)
                            dismiss()
                        },
                        onNegativeClick = { dismiss() },
                        elseFunction = {
                            it.findViewById<TextView>(R.id.dialog_title).text = "删除草稿";
                            it.findViewById<TextView>(R.id.dialog_content).text = "确定要删除该草稿吗?";
                        }

                )
            }.show(fragmentManager, "delete_draft")
        }

        holder.itemView.setOnClickListener {
            //草稿箱的EventBus数据传递
            EventBus.getDefault().postSticky(AnswerDraftEvent(data.draftAnswerContent, data.draftAnswerId.toString(), data.questionId.toString()))
            ARouter.getInstance().build(QA_ANSWER).navigation()
        }
    }


    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanAnswerDraftPage()
        viewModel.loadAnswerDraftList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}