package com.mredrock.cyxbs.ufield.ui.fragment.ufieldfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.adapter.UfieldRvAdapter
import com.mredrock.cyxbs.ufield.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.helper.GridSpacingItemDecoration
import com.mredrock.cyxbs.ufield.ui.activity.DetailActivity
import com.mredrock.cyxbs.ufield.viewmodel.UFieldViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class EducationFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_education_rv.view()
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity())[UFieldViewModel::class.java]
    }
    private val mAdapter: UfieldRvAdapter by lazy { UfieldRvAdapter() }

    private lateinit var mDataList: MutableList<ItemActivityBean.ItemAll>
    private val mRefresh: SmartRefreshLayout by R.id.uField_education_refresh.view()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_education, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
        iniRefresh()

    }

    private fun iniRv() {
        mViewModel.apply {
            educationList.observe(requireActivity()) {
                mAdapter.submitList(it)
                mDataList = it as MutableList<ItemActivityBean.ItemAll>
            }
        }

        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mAdapter.apply {
                setOnActivityClick {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("actID", mDataList[it].activityId)
                    startActivity(intent)
                }
            }
            addItemDecoration(GridSpacingItemDecoration(2))

        }

    }
    /**
     * 处理下拉刷新
     */
    private fun iniRefresh() {
        mRefresh.apply {
            setRefreshHeader(ClassicsHeader(requireContext()))
            setEnableLoadMore(false)
            //下拉刷新
            setOnRefreshListener {
                mViewModel.apply {
                  getEducationActivityList()
                }
                finishRefresh(500)
            }
        }


    }


}