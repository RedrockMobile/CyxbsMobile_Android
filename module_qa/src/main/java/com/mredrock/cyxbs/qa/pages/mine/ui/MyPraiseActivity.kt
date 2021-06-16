package com.mredrock.cyxbs.qa.pages.mine.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.WrapperListAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_ANSWER
import com.mredrock.cyxbs.common.config.QA_MY_PRAISE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.MyPraiseRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyPraiseViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_my_comment.*
import kotlinx.android.synthetic.main.qa_activity_my_praise.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_common_toolbar.view.*

@Route(path = QA_MY_PRAISE)
class MyPraiseActivity : BaseViewModelActivity<MyPraiseViewModel>() {

    private lateinit var myPraiseRvAdapter: MyPraiseRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_praise)
        initView()
        initObserve()
        initClick()
    }

    private fun initView(){
        //初始化toolBar
        qa_tv_toolbar_title.text = "收到的赞"
        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }
        qa_tb_my_praise.setBackgroundColor(Color.TRANSPARENT)

        //加载RecyclerView
        myPraiseRvAdapter = MyPraiseRvAdapter(this)
        emptyRvAdapter = EmptyRvAdapter("暂时没有收到赞哦")
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }

        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = myPraiseRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )

        qa_rv_my_praise.adapter = adapterWrapper
        qa_rv_my_praise.layoutManager = LinearLayoutManager(this)
    }

    private fun initObserve(){
        //观察List是否变化
        viewModel.praiseList.observe {
            myPraiseRvAdapter.submitList(it)
        }

        //观察网络状态
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        //观察网络请求返回结果
        viewModel.initialLoad.observe {
            when(it){
                NetworkState.LOADING -> {
                    qa_swl_my_praise.isRefreshing = true
                    (qa_rv_my_praise.adapter as? RvAdapterWrapper)?.apply {
                    }
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_swl_my_praise.isRefreshing = false
                }
                else -> {
                    qa_swl_my_praise.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    private fun initClick(){
        qa_swl_my_praise.setOnRefreshListener {
            viewModel.invalidateCWList()
        }
    }

}