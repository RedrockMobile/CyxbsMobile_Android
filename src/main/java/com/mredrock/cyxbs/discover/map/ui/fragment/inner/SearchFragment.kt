package com.mredrock.cyxbs.discover.map.ui.fragment.inner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.ui.fragment.inner.search.SearchHistoryFragment
import com.mredrock.cyxbs.discover.map.ui.fragment.inner.search.SearchResultFragment
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel

/**
 * 因为多fragment在transaction的多层嵌套会出现内部fragment被回收的情况，于是每次都手动创建新的fragment并回收原来的fragment
 * 注意，要手动取消对viewModel的mutablelivedata的注册
 */

class SearchFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private val manager: FragmentManager?
        get() = childFragmentManager

    private var searchHistoryFragment = SearchHistoryFragment()
    private var searchResultFragment = SearchResultFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        viewModel.searchText.observe(
                viewLifecycleOwner,
                Observer {
                    if (it.isEmpty()) {
                        openSearchHistoryFragment()
                    } else {
                        openSearchResultFragment()
                    }
                }
        )


    }

    override fun onResume() {
        super.onResume()
        openSearchHistoryFragment()
    }



    private fun openSearchHistoryFragment() {
        val transaction = manager?.beginTransaction()
        if (!searchHistoryFragment.isAdded) {
            transaction?.add(R.id.map_fl_search_fragment, searchHistoryFragment)
        }
        transaction?.hide(searchResultFragment)
        transaction?.show(searchHistoryFragment)?.commit()
    }


    private fun openSearchResultFragment() {
        val transaction = manager?.beginTransaction()
        if (!searchResultFragment.isAdded) {
            transaction?.add(R.id.map_fl_search_fragment, searchResultFragment)
        }
        transaction?.hide(searchHistoryFragment)
        transaction?.show(searchResultFragment)?.commit()
    }

}