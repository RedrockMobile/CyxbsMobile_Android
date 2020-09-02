package com.mredrock.cyxbs.mine.page.ask

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.QuestionDraftEvent
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AskDraft
import com.mredrock.cyxbs.mine.util.ui.BaseRVFragment
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import kotlinx.android.synthetic.main.mine_list_item_my_ask_draft.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by roger on 2019/12/3
 */
class AskDraftFm : BaseRVFragment<AskDraft>() {

    private val NAVIGATION = 200

    private val viewModel by lazy { ViewModelProvider(this).get(AskViewModel::class.java) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadAskDraftList()
        viewModel.askDraft.observe(viewLifecycleOwner, Observer {
            setNewData(it)
        })
        viewModel.eventOnAskDraft.observe(viewLifecycleOwner, Observer {
            setState(it)
        })
    }


    override fun getItemLayout(): Int {
        return R.layout.mine_list_item_my_ask_draft
    }

    override fun bindFooterHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun bindDataHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, data: AskDraft) {
        holder.itemView.mine_ask_draft_tv_title.text = if (data.title.isEmpty()) "(标题为空)" else data.title
        holder.itemView.mine_ask_draft_tv_description.text = if (data.description.isEmpty()) "(内容为空)" else data.description
        holder.itemView.mine_ask_draft_tv_lastedit_at.text = data.latestEditTime.split(" ")[0].replace("-", ".")

        holder.itemView.mine_ask_draft_iv_garbage.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                val tag = "delete_draft"
                if (it1.findFragmentByTag(tag) == null) {
                    CommonDialogFragment().apply {
                        initView(
                                containerRes = R.layout.mine_layout_dialog_with_title_and_content,
                                positiveString = "删除",
                                onPositiveClick = {
                                    viewModel.deleteDraftById(data.draftQuestionId)
                                    dismiss()
                                },
                                onNegativeClick = { dismiss() },
                                elseFunction = {
                                    it.findViewById<TextView>(R.id.dialog_title).text = "删除草稿";
                                    it.findViewById<TextView>(R.id.dialog_content).text = "确定要删除该草稿吗?";
                                }

                        )
                    }.show(it1, tag)
                }

            }
        }

        holder.itemView.setOnClickListener {
            EventBus.getDefault().postSticky(QuestionDraftEvent(data.content, data.draftQuestionId.toString()))
            ARouter.getInstance().build(QA_QUIZ).navigation(activity, NAVIGATION)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NAVIGATION) {
            onSwipeLayoutRefresh()
        }
    }

    override fun onSwipeLayoutRefresh() {
        setState(RvFooter.State.LOADING)
        viewModel.cleanAskDraftPage()
        viewModel.loadAskDraftList()
        getSwipeLayout().isRefreshing = false
        getRecyclerView().scrollToPosition(0)
    }
}