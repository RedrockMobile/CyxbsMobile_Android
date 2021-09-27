package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.pages.search.model.SearchQuestionDataSource
import com.mredrock.cyxbs.qa.pages.search.ui.adapter.DataBindingAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.binder.BaseDataBinder
import com.mredrock.cyxbs.qa.pages.search.ui.binder.RelateUserBinder
import com.mredrock.cyxbs.qa.pages.search.ui.extention.createBindingAdapter
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel
import com.tencent.tauth.Tencent
import kotlinx.android.synthetic.main.qa_fragment_question_search_result_dynamic.*
import kotlinx.android.synthetic.main.qa_fragment_question_search_result_user.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/26
 * @description
 **/
class RelateUserFragment :BaseResultFragment() {

    private var searchKey = ""

    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)

    private lateinit var userAdapter: DataBindingAdapter
    override fun initData() {
        arguments?.let {
            searchKey = it.getString("searchKey", "")
        }
        initObserve()
        initRecycler()
    }


    private fun initObserve(){
        viewModel.userList.observe(viewLifecycleOwner, {
            userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                for (user in it){
                    add(RelateUserBinder(user))
                }
            })
        })
    }

    private fun initRecycler(){
        userAdapter = createBindingAdapter(qa_rv_search_user,LinearLayoutManager(requireContext()))
//        viewModel.getUsers(searchKey)
    }

    override fun getLayoutId() = R.layout.qa_fragment_question_search_result_user
}