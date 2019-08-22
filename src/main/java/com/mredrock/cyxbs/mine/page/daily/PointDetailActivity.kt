package com.mredrock.cyxbs.mine.page.daily

import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.loadAvatar
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

        initObserve()

        loadAvatar(BaseApp.user!!.photoThumbnailSrc, civ_head)

        common_toolbar.init("积 分 明 细")

        initRv()

        initData()
    }

    private fun initData() {
        viewModel.cleanPage()
        adapter.clear()
        viewModel.loadAllData()
    }

    private fun initRv() {
        adapter.loadMoreSource = {
            viewModel.loadRecord()
        }

        rv_point_detail.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@PointDetailActivity)
            adapter = this@PointDetailActivity.adapter
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this@PointDetailActivity, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL).let {
                it.setDrawable(ContextCompat.getDrawable(this@PointDetailActivity,R.drawable.mine_div_line)!!)
                it
            })
        }

        srl_point_detail.setOnRefreshListener {
            viewModel.cleanPage()
            adapter.clear()
            viewModel.loadAllData()
        }
    }

    private fun initObserve() {
        viewModel.apply {
            accountEvent.observe(this@PointDetailActivity, Observer {
                tv_point.text = it?.toString()
            })
            recordEvent.observe(this@PointDetailActivity, Observer {
                if (srl_point_detail.isRefreshing) {
                    srl_point_detail.isRefreshing = false
                }
                it ?: return@Observer
                if (adapter.itemCount < 12) {
                    jcv_contain.cleanCache()
                }
                adapter.loadMore(it)
            })
            errorEvent.observe(this@PointDetailActivity, Observer {
                if (srl_point_detail.isRefreshing) {
                    srl_point_detail.isRefreshing = false
                }
                it?:return@Observer
                toast(it)
            })
        }
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
