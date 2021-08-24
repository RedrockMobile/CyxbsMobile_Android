package com.mredrock.cyxbs.mine.page.feedback.edit.ui

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFeedbackEditBinding
import com.mredrock.cyxbs.mine.page.feedback.edit.presenter.FeedbackEditPresenter
import com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel.FeedbackEditViewModel

/**
 * @Date : 2021/8/23   20:52
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditActivity : BaseMVPVMActivity<FeedbackEditViewModel, MineActivityFeedbackEditBinding, FeedbackEditPresenter>() {

    /**
     * 得到P层
     */
    override fun createPresenter(): FeedbackEditPresenter = FeedbackEditPresenter()

    /**
     * 得到布局id
     */
    override fun getLayoutId(): Int = R.layout.mine_activity_feedback_edit

    /**
     * 提供viewModel
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
    }

    /**
     * 初始化view
     */
    override fun initView() {
        // TODO: 2021/8/23 自定义一个EditText 可实现以下功能：1.一键删除 2.选中时字体呈现不同颜色 3.超过字数限制会有提示动画
        binding?.apply {
            etEditDescription.addTextChangedListener(presenter?.DesTextWatcher())
            etEditTitle.addTextChangedListener(presenter?.TitleTextWatcher())
            chipOne.setOnCheckedChangeListener(presenter)
            chipTwo.setOnCheckedChangeListener(presenter)
            chipThree.setOnCheckedChangeListener(presenter)
            chipFour.setOnCheckedChangeListener(presenter)
        }
    }

    /**
     * 监听LiveData
     */
    override fun observeData() {

    }

    /**
     * 初始化listener
     */
    override fun initListener() {

    }
}