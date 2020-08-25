package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedAdapter
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedUnboundAdapter
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.discover.electricity.viewmodel.ChargeViewModel
import com.mredrock.cyxbs.electricity.R

@Route(path = DISCOVER_ELECTRICITY_FEED)
class ElectricityFeedFragment : BaseFeedFragment<ChargeViewModel>() {


    override val viewModelClass: Class<ChargeViewModel> = ChargeViewModel::class.java

    override var hasTopSplitLine = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                            refreshCharge(id, room)
                        }
                    }.show(it, "ElectricityFeedSetting")
                }

            }
        }
        viewModel.chargeInfo.observe {
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
        viewModel.getCharge(id, room)
    }


}
