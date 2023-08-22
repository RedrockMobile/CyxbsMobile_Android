package com.mredrock.cyxbs.ufield.lyt.fragment.checkfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.adapter.DoneRvAdapter
import com.mredrock.cyxbs.ufield.lyt.bean.DoneBean
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment.DoneViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * description ：已经审核过的活动
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class DoneFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_done_rv.view()

    private val mViewModel by viewModels<DoneViewModel>()
    private val mAdapter: DoneRvAdapter by lazy { DoneRvAdapter() }
    private val mRefresh: SmartRefreshLayout by R.id.uField_check_refresh_done.view()
    private lateinit var mDataList: MutableList<DoneBean>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
        iniRefresh()
    }

    /**
     * 展示已经处理过的活动数据
     */
    private fun iniRv() {

        mViewModel.apply {
            doneList.observe {
                mAdapter.submitList(it)
                mDataList = it as MutableList<DoneBean>
            }
        }
        mRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }


    }

    /**
     * 处理刷新和加载
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun iniRefresh() {
        mRefresh.apply {
            setRefreshHeader(ClassicsHeader(requireContext()))
            setRefreshFooter(ClassicsFooter(requireContext()))
            //下拉刷新
            setOnRefreshListener {
                mViewModel.getViewedData(mDataList.lastOrNull()?.activity_id!!)
                mRefresh.finishRefresh(1000)
            }
            //上拉加载
            setOnLoadMoreListener {
                mViewModel.apply {
                    getViewedData(mDataList.lastOrNull()?.activity_id?.plus(2) ?: 0)
                }
                mAdapter.notifyDataSetChanged()
                mRefresh.finishLoadMore(500)
            }
        }
    }
}