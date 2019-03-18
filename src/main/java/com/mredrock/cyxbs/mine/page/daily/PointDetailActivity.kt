package com.mredrock.cyxbs.mine.page.daily

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_point_detail.*
import org.jetbrains.anko.toast


/**
 * Created by zzzia on 2018/8/16.
 * 积分明细
 */
class PointDetailActivity : BaseViewModelActivity<PointDetailViewModel>() {
    override val viewModelClass: Class<PointDetailViewModel> = PointDetailViewModel::class.java
    override val isFragmentActivity: Boolean = false

    private var adapter = PointDetailAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_point_detail)

        viewModel.recordEvent.observe(this, Observer {
            if (srl_point_detail.isRefreshing) {
                srl_point_detail.isRefreshing = false
            }
            it ?: return@Observer
            adapter.loadMore(it)
        })
        viewModel.errorEvent.observe(this, Observer {
            if (srl_point_detail.isRefreshing) {
                srl_point_detail.isRefreshing = false
            }
            it?:return@Observer
            toast(it)
        })

        common_toolbar.init("积 分 明 细")

        rv_point_detail.layoutManager = LinearLayoutManager(this)
        rv_point_detail.adapter = adapter

        srl_point_detail.setOnRefreshListener {
            viewModel.cleanPage()
            adapter.clear()
            viewModel.loadIntegralRecords()
        }

        viewModel.loadIntegralRecords()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mine_activity_point_detail, menu)
        menu?.getItem(0)?.setOnMenuItemClickListener {
            startActivity<PointSpecActivity>()
            return@setOnMenuItemClickListener false
        }
        return super.onCreateOptionsMenu(menu)
    }
}
