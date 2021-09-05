package com.mredrock.cyxbs.mine.page.feedback.history.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHistoryDetailBinding
import com.mredrock.cyxbs.mine.page.feedback.base.ui.BaseMVPVMActivity

class HistoryDetailActivity :
    BaseMVPVMActivity<HistoryDetailViewModel, MineActivityHistoryDetailBinding, HistoryDetailPresenter>() {

    /**
     * 创建Presenter对象
     */
    override fun createPresenter(): HistoryDetailPresenter = HistoryDetailPresenter(intent.getLongExtra("id",-1L),intent.getBooleanExtra("isReply",false))

    /**
     * 返回布局Id
     */
    override fun getLayoutId(): Int = R.layout.mine_activity_history_detail

    /**
     * rv_recycle的Adapter
     */
    private val replyBannerRvAdapter by lazy {
        ReplyBannerAdapter()
    }


    /**
     * 初始化View和View的一些配置项
     */
    override fun initView() {
        binding?.apply {
            vm = viewModel
        }
        /**
         * 设置rv_recycle
         */
        binding?.rvReplyBanner?.apply {
            adapter = replyBannerRvAdapter
            layoutManager = GridLayoutManager(this@HistoryDetailActivity, 3)
        }
    }

    override fun initListener() {
        binding?.apply {
            includeToolBar.fabBack.setOnSingleClickListener {
                finish()
            }
        }
    }

    /**
     * 观察vm中的数据变动
     */
    override fun observeData() {
        viewModel.apply {
            observeReplyBannerUrl(replyPicUrls)
        }
    }

    /**
     * 观察vm中回复的图片的url地址的变化
     */
    private fun observeReplyBannerUrl(replyPicUrls: LiveData<List<String>>) {
        replyPicUrls.observe({lifecycle}) {
            Log.d("sss", "observeReplyBannerUrl:${it[0]}  ${it[1]} ")
            replyBannerRvAdapter.submitList(it)
        }
    }

}