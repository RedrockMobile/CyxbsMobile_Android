package com.mredrock.cyxbs.qa.pages.mine.ui

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.QA_MY_COMMENT
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.pages.mine.ui.adapter.MyCommentRvAdapter
import com.mredrock.cyxbs.qa.pages.mine.viewmodel.MyCommentViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_my_comment.*
import kotlinx.android.synthetic.main.qa_common_toolbar.view.*
import kotlin.collections.ArrayList

@Route(path = QA_MY_COMMENT)
class MyCommentActivity : BaseViewModelActivity<MyCommentViewModel>() {

    private lateinit var myCommentRvAdapter: MyCommentRvAdapter
    private val cwList = ArrayList<CommentWrapper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_my_comment)
        initView()
    }

    private fun initView(){
        //初始化toolbar
        qa_tb_my_comment.qa_tv_toolbar_title.text = "评论回复"
        qa_tb_my_comment.setBackgroundColor(ContextCompat.getColor(this, R.color.qa_ignore_item_background_color))

        //初始化三个Adapter
        myCommentRvAdapter = MyCommentRvAdapter(viewModel)
        val footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        val emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_my_comment_no_reply))
        val adapterWrapper = RvAdapterWrapper(
                normalAdapter = myCommentRvAdapter,
                emptyAdapter = emptyRvAdapter,
                footerAdapter = footerRvAdapter
        )

        qa_rv_my_comment.adapter = adapterWrapper
        qa_rv_my_comment.layoutManager = LinearLayoutManager(this)

        //设置分割线
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this,R.drawable.qa_shape_divide_line)?.let {
            divider.setDrawable(it)
        }
        qa_rv_my_comment.addItemDecoration(divider)

        //设置刷新
        qa_swl_my_comment.setOnRefreshListener {
            LogUtils.d("RayleighZ","try to refresh")
            viewModel.invalidateCWList()
        }

        //观察cwList是否变化
        viewModel.cwList.observe {
            LogUtils.d("RayleighZ", "回调到Activity, size is ${it?.size}")
            myCommentRvAdapter.submitList(it)
        }

        //观察网络状态
        viewModel.networkState.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }
    }

    private fun sendRequest(){
//        viewModel.
    }
}