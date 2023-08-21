package com.mredrock.cyxbs.ufield.lyt.fragment.ufieldfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.adapter.UfieldRvAdapter
import com.mredrock.cyxbs.ufield.lyt.ui.helper.GridSpacingItemDecoration
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.UFieldViewModel

class EducationFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_education_rv.view()
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity())[UFieldViewModel::class.java]
    }
    private val mAdapter: UfieldRvAdapter by lazy { UfieldRvAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_education, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()

    }

    private fun iniRv() {
        mViewModel.apply {
            educationList.observe(requireActivity()) {
                mAdapter.submitList(it)
            }
        }

        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 20, true))
        }

    }


}