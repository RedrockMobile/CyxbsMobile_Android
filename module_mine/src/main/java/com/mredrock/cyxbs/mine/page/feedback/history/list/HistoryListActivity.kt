package com.mredrock.cyxbs.mine.page.feedback.history.list

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHistoryListBinding
import com.mredrock.cyxbs.mine.page.feedback.adapter.RvListAdapter
import com.mredrock.cyxbs.mine.page.feedback.base.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.mine.page.feedback.history.detail.HistoryDetailActivity
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.History
import java.util.*

class HistoryListActivity :
    BaseMVPVMActivity<HistoryListViewModel, MineActivityHistoryListBinding, HistoryListPresenter>() {

    /**
     * rv_history_list
     */
    private val rvAdapter by lazy {
        RvListAdapter()
    }

    /**
     * 创建Presenter
     */
    override fun createPresenter(): HistoryListPresenter = HistoryListPresenter()

    /**
     * 获取布局Id
     */
    override fun getLayoutId(): Int = R.layout.mine_activity_history_list

    /**
     * 初始化视图
     */
    override fun initView() {
        binding?.vm = viewModel
        //初始化Rv配置
        binding?.rvHistoryList?.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@HistoryListActivity)
        }

    }

    /**
     * 初始化监听器
     */
    override fun initListener() {
        //RecyclerView的Item点击
        rvAdapter.setOnItemClickListener(
            object : RvListAdapter.ItemClickListener {
                var tag: Long = 0L
                override fun clicked(view: View, data: History) {
                    //防止多次点击
                    val current = System.currentTimeMillis()
                    if (current - tag < 500) return
                    tag = current

                    presenter?.savedState(data)

                    toast(data.toString())

                    val intentExtra = Intent(this@HistoryListActivity,
                        HistoryDetailActivity::class.java).putExtra("id", data.id)
                    startActivity(intentExtra)
                }

            }
        )
        //返回键的点击监听
        binding?.includeToolBar?.fabBack?.setOnSingleClickListener {
            finish()
        }
    }

    /**
     * 观察vm数据变化
     */
    override fun observeData() {
        viewModel?.apply {
            observeRvList(listData)
        }
    }

    /**
     * Rv的数据
     */
    private fun observeRvList(listData: LiveData<List<History>>) {
        listData.observe(this) {
            rvAdapter.submitList(it)
        }
    }

}