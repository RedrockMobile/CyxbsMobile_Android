package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY_FEED
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedAdapter
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedUnboundAdapter
import com.mredrock.cyxbs.discover.electricity.bean.ElecInf
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.discover.electricity.viewmodel.ChargeViewModel
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_discover_feed_unbound.view.*
import com.mredrock.cyxbs.common.utils.extensions.*


@Route(path = DISCOVER_ELECTRICITY_FEED)
class ElectricityFeedFragment : BaseFeedFragment<ChargeViewModel>() {



    override var hasTopSplitLine = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //主要为了防止，使用游客模式，在此登录，导致的登录后状态未刷新，感觉听不规范的，游客模式之后还得统一管理
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            setAdapter(ElectricityFeedUnboundAdapter())
        }
        ServiceManager.getService(IAccountService::class.java).getVerifyService().addOnStateChangedListener {
            if (it == IUserStateService.UserState.LOGIN) {
                context?.runOnUiThread {
                    setAdapter(ElectricityFeedUnboundAdapter())
                }
            }
        }
        init()
    }


    private fun init() {

        setAdapter(ElectricityFeedUnboundAdapter())
        setTitle(getString(R.string.electricity_inquire_string))
        setOnClickListener {
            context?.doIfLogin(getString(R.string.electricity_inquire_string)) {
                parentFragmentManager.let {
                    ElectricityFeedSettingDialogFragment().apply {
                        refresher = { id, room ->
                            viewModel.getCharge(id, room)
                        }
                    }.show(it, "ElectricityFeedSetting")
                }

            }
        }
        viewModel.chargeInfo.observe {
            handleData(it)
        }
        //首次默认加载失败，说明账号有问题
        viewModel.loadFailed.observe {
            if (it == null) return@observe
            val adapter = getAdapter()
            if (adapter is ElectricityFeedUnboundAdapter) {
                adapter.apply {
                    view?.tv_electricity_no_account?.text = getString(R.string.electricity_unbind)
                }
            }
        }

    }

    private fun handleData(it: ElecInf?) {
        if (it == null || it.isEmpty()) {
            setSubtitle("")
        } else {
            setSubtitle(it.recordTime.plus("抄表"))

        }

        val adapter = getAdapter()
        if (adapter is ElectricityFeedAdapter) {
            adapter.refresh(it)
        } else {
            setAdapter(ElectricityFeedAdapter(it))
        }
    }

    override fun onRefresh() {
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            return
        }
        if (viewModel.chargeInfo.value != null) {
            handleData(viewModel.chargeInfo.value)
        } else {
            val pos = context?.defaultSharedPreferences?.getInt(SP_BUILDING_HEAD_KEY, -1)

            if (pos == -1) {
                viewModel.preGetCharge()
                return
            }
            val id = BUILDING_NAMES.getValue(BUILDING_NAMES_HEADER[pos!!])[context?.defaultSharedPreferences?.getInt(SP_BUILDING_FOOT_KEY, -1)!!].split("(")[1].split("栋")[0]
            val room = context?.defaultSharedPreferences!!.getString(SP_ROOM_KEY, "") ?: ""
            if (id.isEmpty() || room.isEmpty()) {
                viewModel.preGetCharge()
            } else {
                viewModel.getCharge(id, room)
            }
        }
    }

}
