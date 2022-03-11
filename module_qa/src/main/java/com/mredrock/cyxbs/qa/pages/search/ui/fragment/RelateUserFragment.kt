package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.config.MINE_PERSON_PAGE
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.qa.R
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
class RelateUserFragment private constructor() : BaseResultFragment() {
    /**
     * 声明fragment为局部单例，主要原因是Vp2的加载缓存的问题，详细可见 @{QuestionSearchedFragment}
     */
    companion object {
        @Volatile
        private var _INSTANCE: RelateUserFragment? = null

        @Synchronized
        @JvmStatic
        fun getInstance(): RelateUserFragment {
            if (_INSTANCE == null) {
                _INSTANCE = RelateUserFragment()
            }
            return _INSTANCE!!
        }

    }

    //搜索内容
    private var searchKey = ""
    private lateinit var myRedid: String
    override fun getViewModelFactory() = QuestionSearchedViewModel.Factory(searchKey)
    private lateinit var userAdapter: DataBindingAdapter
    //该fragment是否与activity进行了联动
    private var initialed = false

    fun refreshKey() {
        //以此为判断标准的原因是，如果没有初始化fragment刷新数据会报错
        if (initialed) {
            searchKey = (requireActivity() as IKeyProvider).getKey()
            viewModel.getUsers(searchKey)
        }
    }

    override fun initData() {
        //获取搜索内容
        searchKey = (requireActivity() as IKeyProvider).getKey()
        myRedid = ServiceManager.getService(IAccountService::class.java).getUserService().getRedid()
        initObserve()
        initRecycler()
        initialed = true
    }

    private fun initObserve() {
        viewModel.userList.observe(viewLifecycleOwner, Observer {
            userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                when (it.size) {
                    //如果size为0展示缺省页
                    0 -> add(RelateNoUserBinder())
                    else -> {
                        for (user in it) {
                            add(
                                RelateUserBinder(user, user.redid == myRedid,
                                    onFocusClick = { view, user ->
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
                                                text = if (user.isBeFocused) "互相关注" else " 已关注"
                                                context.toast(R.string.qa_person_focus_success)
                                            } else {
                                                background = ContextCompat
                                                    .getDrawable(
                                                        requireContext(),
                                                        R.drawable.qa_shape_tv_search_unfocused
                                                    )
                                                text = "+关注"
                                                context.toast(R.string.qa_person_unfocus_success)
                                            }
                                        }
                                    },
                                    onAvatarClick = {
                                        ARouter.getInstance().build(MINE_PERSON_PAGE)
                                            .withString("redid", it)
                                            .navigation()
                                    })
                            )
                        }
                    }
                }
            })
        })

        viewModel.userNetworkState.observe(viewLifecycleOwner, Observer {
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
        userAdapter = DataBindingAdapter()

        qa_rv_search_user.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getUsers(searchKey)
    }

    override fun getLayoutId() = R.layout.qa_fragment_question_search_result_user

    override fun onDestroy() {
        super.onDestroy()
        _INSTANCE = null
    }
}