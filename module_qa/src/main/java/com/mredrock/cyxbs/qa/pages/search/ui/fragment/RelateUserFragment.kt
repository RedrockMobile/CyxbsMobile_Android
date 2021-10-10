package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.UserBrief
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.DataBindingAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.binder.BaseDataBinder
import com.mredrock.cyxbs.qa.pages.search.ui.binder.RelateNoUserBinder
import com.mredrock.cyxbs.qa.pages.search.ui.binder.RelateUserBinder
import com.mredrock.cyxbs.qa.pages.search.ui.callback.IKeyProvider
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import kotlinx.android.synthetic.main.qa_fragment_question_search_result_user.*

/**
 * @class RelateDynamicFragment
 * @author YYQF
 * @data 2021/9/26
 * @description 搜索用户结果页
 **/
class RelateUserFragment : BaseResultFragment() {

    //搜索内容
    private var searchKey = ""

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)

    private lateinit var userAdapter: DataBindingAdapter

    override fun initData() {
        //获取搜索内容
        searchKey = (requireActivity() as IKeyProvider).getKey()
        initObserve()
        initRecycler()
    }

    private fun initObserve() {
        viewModel.userList.observe(viewLifecycleOwner, {
            userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                when (it.size) {
                    //如果size为0展示缺省页
                    0 -> add(RelateNoUserBinder())
                    else -> {
                        for (user in it) {
                            add(RelateUserBinder(user,onFocusClick = { view, user ->
                                //请求关注/取关接口
                                viewModel.changeFocusStatus(user.redid)
                                //改变关注按钮的样式
                                (view as TextView).apply {
                                    if (text == "+关注") {
                                        background = ContextCompat
                                            .getDrawable(
                                                requireContext(),
                                                R.drawable.qa_shape_tv_search_focused
                                            )
                                        text = if (user.isFocus) "互相关注" else " 已关注"
                                    }else{
                                        background = ContextCompat
                                            .getDrawable(
                                                requireContext(),
                                                R.drawable.qa_shape_tv_search_unfocused
                                            )
                                        text = "+关注"
                                    }
                                }
                            }))
                        }
                    }
                }
            })
        })

        viewModel.userNetworkState.observe(viewLifecycleOwner, {
            //请求失败展示缺省页
            if (it == NetworkState.FAILED) {
                userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                    add(RelateNoUserBinder())
                })
            }

            //请求成功或失败都隐藏刷新控件
            if (it != NetworkState.LOADING) {
                qa_srl_search_user.isRefreshing = false
            }
        })

        qa_srl_search_user.setOnRefreshListener {
            viewModel.getUsers(searchKey)
        }
    }

    private fun initRecycler() {
        userAdapter = DataBindingAdapter(LinearLayoutManager(requireContext()))

        qa_rv_search_user.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getUsers(searchKey)
    }

    override fun getLayoutId() = R.layout.qa_fragment_question_search_result_user
}