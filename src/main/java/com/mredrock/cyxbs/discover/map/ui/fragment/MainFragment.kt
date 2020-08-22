package com.mredrock.cyxbs.discover.map.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.ui.fragment.inner.MapViewFragment
import com.mredrock.cyxbs.discover.map.ui.fragment.inner.SearchFragment
import com.mredrock.cyxbs.discover.map.util.KeyboardController
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_fragment_main.*

//该MainFragment使用FragmentTransaction管理两个Fragment

class MainFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel

    private val manager: FragmentManager?
        get() = childFragmentManager
    private val mapViewFragment = MapViewFragment()
    private val searchFragment = SearchFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_main, container, false)
    }


    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        initMapViewFragment()
        map_iv_back.setOnClickListener {
            if (manager?.backStackEntryCount ?: 0 == 0) {
                //此处填写退出MapActivity的逻辑
                activity?.finish()
            } else {
                closeSearchFragment()
            }
        }
        //当搜索框被点击，打开搜索Fragment
        map_et_search.setOnClickListener {
            viewModel.unCheck.value = true
            openSearchFragment()
        }
        //上面这个事件在editText没有焦点时收不到事件，于是加一个
        map_et_search.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.unCheck.value = true
                openSearchFragment()
            }
        }

        viewModel.closeSearchFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                closeSearchFragment()
            }
        })


        viewModel.mapViewIsInAnimation.observe(viewLifecycleOwner, Observer { isAnimation ->
            if (isAnimation) {
                map_et_search.animate().alpha(0f).duration = 500
                map_et_search.isEnabled = false
                map_iv_back.animate().alpha(0f).duration = 500
                map_iv_back.isEnabled = false
            } else {
                map_et_search.animate().alpha(1f).duration = 500
                map_et_search.isEnabled = true
                map_iv_back.animate().alpha(1f).duration = 500
                map_iv_back.isEnabled = true
            }
        })

        viewModel.mapInfo.observe(viewLifecycleOwner, Observer { t ->
            map_et_search.hintString = BaseApp.context.getString(R.string.map_search_hint) + if (t.hotWord == "") BaseApp.context.getString(R.string.map_search_hint_default_place_name) else t.hotWord
        })

        /**
         * 搜索框的内容发生改变，直接把内容传给viewModel
         */
        map_et_search.doOnTextChanged { text, start, count, after ->
            viewModel.searchText.value = text.toString()
        }

        viewModel.searchHistoryString.observe(viewLifecycleOwner, Observer {
            map_et_search.setText(it)
        })

    }

    private fun initMapViewFragment() {
        val transaction = manager?.beginTransaction()
        if (!mapViewFragment.isAdded) {
            transaction?.add(R.id.map_ll_map_fragment, mapViewFragment)?.commit()
        } else {
            transaction?.show(mapViewFragment)?.commit()
        }
    }

    fun closeSearchFragment() {
        viewModel.isClickSymbol.value = false
        map_et_search.setText("")
        KeyboardController.hideInputKeyboard(requireContext(), map_et_search)
        val transaction = manager?.beginTransaction()?.setCustomAnimations(R.animator.map_slide_from_left, R.animator.map_slide_to_right, R.animator.map_slide_from_right, R.animator.map_slide_to_left)
        transaction?.hide(searchFragment)
        transaction?.show(mapViewFragment)?.commit()
        manager?.popBackStack()
    }


    fun openSearchFragment() {
        viewModel.isClickSymbol.value = true
        viewModel.closeSearchFragment.value = false
        if (manager?.backStackEntryCount ?: 0 != 0) {
            //确保不重复打开搜索框
            return
        }
        val transaction = manager?.beginTransaction()?.setCustomAnimations(
                R.animator.map_slide_from_right,
                R.animator.map_slide_to_left,
                R.animator.map_slide_from_left,
                R.animator.map_slide_to_right)
        transaction?.hide(mapViewFragment)
        if (!searchFragment.isAdded) {
            transaction?.add(R.id.map_ll_map_fragment, searchFragment)
        }
        transaction?.show(searchFragment)
        transaction?.addToBackStack("search")?.commit()
    }


}