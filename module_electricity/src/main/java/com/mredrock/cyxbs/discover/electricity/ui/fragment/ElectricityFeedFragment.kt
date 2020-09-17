package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY_FEED
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserStateService
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

@Route(path = DISCOVER_ELECTRICITY_FEED)
class ElectricityFeedFragment : BaseFeedFragment<ChargeViewModel>() {


    override val viewModelClass: Class<ChargeViewModel> = ChargeViewModel::class.java

    override var hasTopSplitLine = false//在志愿时长之上，故没有顶部分割线
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //主要为了防止，使用游客模式，在此登录，导致的登录后状态未刷新，感觉听不规范的，游客模式之后还得统一管理
        //即使用游客模式进入MainActivity，结果在查询电费的时候进行了登录
        //在游客模式之中点击电费会弹出登录界面，而在这个界面中登录将引起用户状态的改变
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
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            //首先无参数请求电费，后端返回上一次绑定的数据
            viewModel.preGetCharge()
        }
        setAdapter(ElectricityFeedUnboundAdapter())
        setTitle(getString(R.string.electricity_inquire_string))
        setSecondTitle(getString(R.string.electricity_free_count))
        setOnClickListener {//点击之后展示设置楼栋的画面
            context?.doIfLogin(getString(R.string.electricity_inquire_string)) {
                //此处的parentFragmentManager为DiscoverFragment对应的Manager
                parentFragmentManager.let {
                    //此处的方法为已经登录之后的操作
                    //此处的DialogFragment为设置宿舍位置的Fragment
                    ElectricityFeedSettingDialogFragment().apply {
                        refresher = { id, room ->
                            refreshCharge(id, room)
                        }
                    }.show(it, "ElectricityFeedSetting")//dialogFragment的show方法
                }

            }
        }
        viewModel.chargeInfo.observe {
            handleData(it)
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
            adapter.refresh(it)//重新刷新
        } else {
            setAdapter(ElectricityFeedAdapter(it))
        }
    }

    override fun onRefresh() {
        val pos = context?.defaultSharedPreferences?.getInt(SP_BUILDING_HEAD_KEY, -1)

        if (pos == -1) {
            return
        }
        val id = BUILDING_NAMES.getValue(BUILDING_NAMES_HEADER[pos!!])[context?.defaultSharedPreferences?.getInt(SP_BUILDING_FOOT_KEY, -1)!!].split("(")[1].split("栋")[0]
        val room = context?.defaultSharedPreferences!!.getString(SP_ROOM_KEY, "") ?: ""

        refreshCharge(id, room)
    }

    private fun refreshCharge(id: String, room: String) {
        viewModel.getCharge(id, room)//viewModel获取电费，通过mvvm架构进行刷新
    }


}
