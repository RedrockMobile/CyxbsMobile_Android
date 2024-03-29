package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.NetworkState
import com.mredrock.cyxbs.mine.page.mine.adapter.DataBindingAdapter
import com.mredrock.cyxbs.mine.page.mine.binder.BaseDataBinder
import com.mredrock.cyxbs.mine.page.mine.binder.EmptyFanBinder
import com.mredrock.cyxbs.mine.page.mine.binder.FanBinder
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.mine.viewmodel.FanViewModel

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanFragment : BaseViewModelFragment<FanViewModel>() {

    private var redId = ""
    private lateinit var userAdapter: DataBindingAdapter
    private var isSelf: Boolean = false

    private val mine_fan_srl by R.id.mine_fan_srl.view<SwipeRefreshLayout>()
    private val mine_fan_rv by R.id.mine_fan_rv.view<RecyclerView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            redId = it.getString("redid").orEmpty()
        }

        if (redId ==
            ServiceManager(IAccountService::class).getUserService().getRedid()){
            isSelf = true
        }

        return inflater.inflate(R.layout.mine_fragment_fan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserve()
        initRecycler()
    }

    private fun initObserve() {
        viewModel.fanList.observe(viewLifecycleOwner, Observer{
            userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                when (it.size) {
                    0 -> add(EmptyFanBinder(isSelf))
                    else -> for (fan in it) {
                        add(FanBinder(fan,isSelf,
                            onFocusClick = { view, user ->
                                viewModel.changeFocusStatus(user.redid)
                                (view as TextView).apply {
                                    if (text == "+关注") {
                                        background = ContextCompat
                                            .getDrawable(
                                                requireContext(),
                                                R.drawable.mine_shape_tv_focused
                                            )
                                        text = "互相关注"
                                        toast(R.string.mine_person_focus_success)

                                    }else{
                                        background = ContextCompat
                                            .getDrawable(
                                                requireContext(),
                                                R.drawable.mine_shape_tv_unfocus
                                            )
                                        text = "+关注"
                                        toast(R.string.mine_person_unfocus_success)

                                    }
                                }
                            },
                            onAvatarClick = {
                               HomepageActivity.startHomePageActivity(it,requireActivity())
                            }
                        ))
                    }
                }
            })
        })

        viewModel.fanNetWorkState.observe(viewLifecycleOwner, Observer{
            if (it == NetworkState.FAILED){
                userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                    add(EmptyFanBinder(isSelf))
                })
            }

            if (it != NetworkState.LOADING){
                mine_fan_srl.isRefreshing = false
            }
        })

        mine_fan_srl.setOnRefreshListener {
            viewModel.getFans(redId)
        }
    }

    private fun initRecycler() {
        userAdapter = DataBindingAdapter(LinearLayoutManager(requireContext()))

        mine_fan_rv.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getFans(redId)
    }
}