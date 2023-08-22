package com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.adapter.SearchRvAdapter
import com.mredrock.cyxbs.ufield.lyt.adapter.UfieldRvAdapter
import com.mredrock.cyxbs.ufield.lyt.ui.helper.GridSpacingItemDecoration
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.SearchViewModel
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.UFieldViewModel

class AllSearchFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_all_rv_search.view()
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchViewModel::class.java]
    }
    private val mAdapter: SearchRvAdapter by lazy { SearchRvAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_all_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
    }

    private fun iniRv() {
        mViewModel.apply {
            allSearchList.observe(requireActivity()) {
                mAdapter.submitList(it)
            }
        }
        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = mAdapter
            addItemDecoration(GridSpacingItemDecoration(1, 20, true))
        }


    }


}