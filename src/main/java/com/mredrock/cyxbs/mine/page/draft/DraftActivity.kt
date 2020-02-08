package com.mredrock.cyxbs.mine.page.draft

import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.config.QA_ANSWER
import com.mredrock.cyxbs.common.config.QA_QUIZ
import com.mredrock.cyxbs.common.event.DraftEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Draft
import com.mredrock.cyxbs.common.utils.extensions.toast
import kotlinx.android.synthetic.main.mine_activity_draft.*
import kotlinx.android.synthetic.main.mine_dialog_answer_detail.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


/**
 * Created by zia on 2018/9/12.
 */
class DraftActivity(override val viewModelClass: Class<DraftViewModel> = DraftViewModel::class.java,
                    override val isFragmentActivity: Boolean = false)
    : BaseViewModelActivity<DraftViewModel>(), IDraftView {

    private val fragment = DraftFragment()
    private var isEdit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_draft)

        initToolBar()
        initObserver()

        val t = supportFragmentManager.beginTransaction()
        t.replace(mine_draft_frameLayout.id, fragment)
        t.commit()

    }

    private fun initToolBar() {
        setSupportActionBar(mine_draft_toolbar)
        title = ""
        mine_draft_toolbar_title.text = "草稿箱"
        mine_draft_toolbar.setNavigationOnClickListener { finish() }
        mine_draft_toolbarMenu.setOnClickListener {
            if (isEdit) {
                mine_draft_toolbarMenu.text = "取消"
            } else {
                mine_draft_toolbarMenu.text = "编辑"
            }
            fragment.setIsEdit(isEdit)//开始动画
            isEdit = !isEdit
        }
    }

    /**
     * 添加观察者
     */
    private fun initObserver() {
        viewModel.errorEvent.observe(this, Observer {
            if (it != null) {
//                toast(it)
            }
            fragment.getFooter().showLoadError()
        })

        viewModel.draftEvent.observe(this, Observer {
            if (it == null) return@Observer
            if (it.isEmpty() && viewModel.page <= 3) {
                showEmpty()
            } else {
                if (it.isEmpty()) {
                    fragment.getFooter().showNoMore()
                } else {
                    showRv()
                    fragment.addData(it)
                }
            }
        })

        viewModel.deleteEvent.observe(this, Observer {
            if (it != null) {
                toast("删除成功")
                fragment.removeItem(it)
            }
        })

        viewModel.sendMessageEvent.observe(this, Observer {
            if (it != null) {
                toast("发送成功")
                fragment.removeItem(it)
            }
        })
    }

    /**
     * 初始化数据，在fragment生成时和下拉刷新时调用
     */
    override fun initData() {
        fragment.getFooter().showLoading()
        viewModel.cleanPage()
        viewModel.loadDraftList()
    }

    /**
     * 加载更多数据
     */
    override fun loadMore() {
        viewModel.loadDraftList()
    }

    override fun deleteDraft(draft: Draft) {
        alert("删除后将无法回复，确定要删除吗？", "提示") {
            yesButton {
                viewModel.deleteDraft(draft)
            }
            noButton { }
        }.show()
    }

    override fun showEmpty() {
        fragment.getFooter().showNothing()
        mine_draft_empty_layout.visible()
    }

    override fun showRv() {
        mine_draft_empty_layout.invisible()
    }

    /**
     * Item点击事件，需要处理3类
     */
    override fun onClickItem(draft: Draft, position: Int) {
        when (draft.type) {
            //提问草稿
            Draft.TYPE_QUESTION -> {
                EventBus.getDefault().postSticky(DraftEvent(draft.decodeJson, draft.id, draft.targetId))
                ARouter.getInstance().build(QA_QUIZ).navigation()
            }
            //回答草稿
            Draft.TYPE_ANSWER -> {
                EventBus.getDefault().postSticky(DraftEvent(draft.decodeJson, draft.id, draft.targetId))
                ARouter.getInstance().build(QA_ANSWER).navigation()
            }
            //评论草稿
            Draft.TYPE_COMMENT -> {
                val str = draft.contentDisplay.replace(Draft.CONTENT_TYPE.getValue(Draft.TYPE_COMMENT), "")
                getBottomSheetDialog(str) {
                    viewModel.sendRemark(draft,str)
                }.show()
            }
        }
    }

    private var tempEditText = ""
    private fun getBottomSheetDialog(oldText: String, sendMessage: () -> Unit): BottomSheetDialog {
        val dialog = BottomSheetDialog(this)
        val container = dialog.layoutInflater.inflate(R.layout.mine_dialog_answer_detail,
                mine_draft_frameLayout, false)
        dialog.setContentView(container)
        container.mine_dialog_answer_detail_submit.setOnClickListener {
            val text = container.mine_dialog_answer_detail_comment.text.toString()
            if (text.isEmpty()) {
                toast("输入不能为空")
                return@setOnClickListener
            }
            sendMessage.invoke()
            container.mine_dialog_answer_detail_comment.setText("")
        }
        dialog.setOnShowListener {
            if (tempEditText.isEmpty()) {
                container.mine_dialog_answer_detail_comment.setText(oldText)
            } else {
                container.mine_dialog_answer_detail_comment.setText(tempEditText)
            }
        }
        dialog.setOnDismissListener {
            tempEditText = container.mine_dialog_answer_detail_comment.text.toString()
        }
        return dialog
    }
}
