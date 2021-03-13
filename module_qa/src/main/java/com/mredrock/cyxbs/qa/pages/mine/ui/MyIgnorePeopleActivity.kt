package com.mredrock.cyxbs.qa.pages.mine.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_MY_IGNORE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.MyIgnoreRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyIgnoreViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_my_ignore_people.*

@Route(path = QA_MY_IGNORE)
class MyIgnorePeopleActivity : BaseViewModelActivity<MyIgnoreViewModel>() {
    lateinit var myIgnoreRvAdapter: MyIgnoreRvAdapter
    lateinit var emptyRvAdapter: EmptyRvAdapter
    lateinit var footerRvAdapter: FooterRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_ignore_people)
        initView()
        initObserve()
        initClick()
    }

    private fun initView() {
        myIgnoreRvAdapter = MyIgnoreRvAdapter()
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        emptyRvAdapter = EmptyRvAdapter("暂时还没有屏蔽的人哦")

        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = myIgnoreRvAdapter,
                footerAdapter = footerRvAdapter,
                emptyAdapter = emptyRvAdapter
        )

        qa_rv_my_ignore.adapter = adapterWrapper
        qa_rv_my_ignore.layoutManager = LinearLayoutManager(this)
    }

    private fun initObserve(){
        //观察List是否变化
        viewModel.ignoreList.observe {
            myIgnoreRvAdapter.submitList(it)
        }

        //观察网络状态
        viewModel.networkState.observe {
            LogUtils.d("RayleighZ","刷新网络状态，data is $it")
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        //观察网络请求返回结果
        viewModel.initialLoad.observe {
            when(it){
                NetworkState.LOADING -> {
                    qa_swl_my_ignore.isRefreshing = true
                    (qa_rv_my_ignore.adapter as? RvAdapterWrapper)?.apply {}
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_swl_my_ignore.isRefreshing = false
                }
                else -> {
                    qa_swl_my_ignore.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    private fun initClick(){
        qa_swl_my_ignore.setOnRefreshListener {
            viewModel.invalidateList()
        }
    }
}