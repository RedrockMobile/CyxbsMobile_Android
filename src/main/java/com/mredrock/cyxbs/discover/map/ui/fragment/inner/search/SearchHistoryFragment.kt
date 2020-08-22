package com.mredrock.cyxbs.discover.map.ui.fragment.inner.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.ui.adapter.SearchHistoryAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.MapDialog
import com.mredrock.cyxbs.discover.map.widget.OnSelectListener
import kotlinx.android.synthetic.main.map_fragment_search_history.*


class SearchHistoryFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var adapter: SearchHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_search_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        map_rv_search_history.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchHistoryAdapter(requireContext(), viewModel)
        map_rv_search_history.adapter = adapter





        map_tv_search_clear.setOnClickListener {
            MapDialog.show(requireContext(), "提示", resources.getString(R.string.map_search_history_clear_tip), object : OnSelectListener {
                override fun onDeny() {
                }

                override fun onPositive() {
                    DataSet.clearSearchHistory()
                    viewModel.notifySearchHistoryChange()
                    adapter.notifyDataSetChanged()
                }
            })
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.notifySearchHistoryChange()
        adapter.notifyDataSetChanged()
    }


}