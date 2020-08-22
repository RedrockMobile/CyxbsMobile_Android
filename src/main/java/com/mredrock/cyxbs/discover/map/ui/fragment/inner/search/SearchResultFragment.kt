package com.mredrock.cyxbs.discover.map.ui.fragment.inner.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.ui.adapter.SearchResultAdapter
import com.mredrock.cyxbs.discover.map.util.ThreadPool
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_fragment_search_result.*
import java.util.regex.Pattern

class SearchResultFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var observer: Observer<String>
    private var isSearching = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)


        val searchResultAdapter = SearchResultAdapter(requireContext(), viewModel)
        map_rv_search_result.adapter = searchResultAdapter
        map_rv_search_result.layoutManager = LinearLayoutManager(requireContext())



        observer = Observer { t ->
            if (t == "") {
                viewModel.searchResult.clear()
                return@Observer
            }
            if (isSearching) {
                return@Observer
            }
            isSearching = true
            //searchResultAdapter.notifyDataSetChanged()
            ThreadPool.execute(Runnable { //输入每次变化都执行下列搜索
                val searchResultArrayList = ArrayList<PlaceItem>()
                val pattern: Pattern?
                try {
                    pattern = Pattern.compile(t)
                } catch (e: Exception) {
                    //正则表达式有错误，不搜索
                    isSearching = false
                    return@Runnable
                }
                for (placeItem: PlaceItem in viewModel.mapInfo.value?.placeList ?: listOf()) {
                    val matcher = pattern.matcher(placeItem.placeName)
                    if (matcher.find()) {
                        searchResultArrayList.add(placeItem)
                    }
                }
                //以上是搜索到的结果
                if (searchResultArrayList.size > 20 || viewModel.searchResult.size > 30) {
                    //如果数据大于10就不动态效果了
                    if (map_rv_search_result.isComputingLayout) {
                        map_rv_search_result.post {
                            viewModel.searchResult.clear()
                            viewModel.searchResult.addAll(searchResultArrayList)
                        }
                    } else {
                        context?.runOnUiThread {
                            viewModel.searchResult.clear()
                            viewModel.searchResult.addAll(searchResultArrayList)
                        }
                    }
                } else {
                    /**
                     * 动态搜索效果
                     */
                    //如果现存列表有多余的结果，则减少
                    for (placeItemOrigin: PlaceItem in viewModel.searchResult.reversed().toList()) {
                        var flag = false
                        for (placeItemResult: PlaceItem in searchResultArrayList) {
                            if (placeItemOrigin === placeItemResult) {
                                flag = true
                            }
                        }
                        if (!flag) {
                            Thread.sleep(50)
                            context?.runOnUiThread {
                                viewModel.searchResult.remove(placeItemOrigin)
                            }
                        }
                    }

                    //如果现存列表没有搜索到的结果，则添加
                    for (placeItemResult: PlaceItem in searchResultArrayList) {
                        var flag = false
                        for (placeItemOrigin: PlaceItem in viewModel.searchResult.toList()) {
                            if (placeItemOrigin === placeItemResult) {
                                flag = true
                            }
                        }
                        if (!flag) {
                            Thread.sleep(50)
                            context?.runOnUiThread {
                                viewModel.searchResult.add(placeItemResult)
                            }
                        }
                    }
                }
                isSearching = false
            })
        }
        viewModel.searchText.observe(viewLifecycleOwner, observer)

    }


}