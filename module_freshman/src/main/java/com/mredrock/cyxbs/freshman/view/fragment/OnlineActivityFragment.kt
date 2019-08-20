package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.bean.OnlineActivityText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentOnlineActivityModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentOnlineActivityPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentOnlineActivityView
import com.mredrock.cyxbs.freshman.presenter.FragmentOnlineActivityPresenter
import com.mredrock.cyxbs.freshman.view.adapter.OnlineActivityAdapter

/**
 * Create by yuanbing
 * on 2019/8/3
 */
class OnlineActivityFragment :
        BaseFragment<IFragmentOnlineActivityView, IFragmentOnlineActivityPresenter,
                IFragmentOnlineActivityModel>(), IFragmentOnlineActivityView {
    private lateinit var mActivities: RecyclerView
    private lateinit var mAdapter: OnlineActivityAdapter

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mActivities = view.findViewById(R.id.rv_online_communication_online_activity)
        mActivities.layoutManager = LinearLayoutManager(context)
        mAdapter = OnlineActivityAdapter()
        mActivities.adapter = mAdapter

        presenter?.requestOnlineActivityData()
    }

    override fun showOnlineActivities(activities: List<OnlineActivityText>) {
        mAdapter.refreshData(activities)
    }

    override fun getLayoutRes() = R.layout.freshman_fragment_online_communication_online_activity

    override fun getViewToAttach() = this

    override fun createPresenter() = FragmentOnlineActivityPresenter()
}