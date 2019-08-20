package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentDataDisclosureModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentDataDisclosurePresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentDataDisclosureView
import com.mredrock.cyxbs.freshman.presenter.FragmentDataDisclosurePresenter
import com.mredrock.cyxbs.freshman.util.decoration.CollegeItemDecoration
import com.mredrock.cyxbs.freshman.view.adapter.CollegeDataAdapter

/**
 * Create by yuanbing
 * on 2019/8/7
 */
class DataDisclosureFragment : BaseFragment<IFragmentDataDisclosureView, IFragmentDataDisclosurePresenter,
        IFragmentDataDisclosureModel>(), IFragmentDataDisclosureView {
    private lateinit var mAdapter: CollegeDataAdapter

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        val college: RecyclerView = view.findViewById(R.id.rv_campus_guidelines_data_disclosure_college)
        college.layoutManager = LinearLayoutManager(context)
        mAdapter = CollegeDataAdapter()
        college.adapter = mAdapter
        college.addItemDecoration(CollegeItemDecoration(15))

        presenter?.getCollege()
    }

    override fun getLayoutRes() = R.layout.freshman_fragment_campus_guidelines_data_closure

    override fun getViewToAttach() = this

    override fun createPresenter() = FragmentDataDisclosurePresenter()

    override fun showCollege(college: List<String>) {
        mAdapter.refreshData(college)
    }
}