package com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.gyd.ui.DetailActivity
import com.mredrock.cyxbs.ufield.lyt.adapter.SearchRvAdapter
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.lyt.helper.GridSpacingItemDecoration
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.SearchViewModel


class CultureSearchFragment : BaseFragment() {
    private val mRv: RecyclerView by R.id.uField_all_rv_culture_search.view()
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchViewModel::class.java]
    }
    private val mAdapter: SearchRvAdapter by lazy { SearchRvAdapter() }
    private lateinit var mDataList: MutableList<ItemActivityBean.ItemAll>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_culture_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
    }

    private fun iniRv() {
        mViewModel.apply {
            cultureSearchList.observe(requireActivity()) {
                mDataList = it as MutableList<ItemActivityBean.ItemAll>

                mAdapter.submitList(it)
            }
        }
        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = mAdapter.apply {
                setOnActivityClick {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("actID", mDataList[it].activity_id)
                    Log.d("595995", "测试结果-->> ${mDataList[it].activity_id}");
                    startActivity(intent)
                }
            }
        //    addItemDecoration(GridSpacingItemDecoration(1, 10, false))
        }

    }


}