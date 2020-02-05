package com.mredrock.cyxbs.discover.electricity.ui.fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedAdapter
import com.mredrock.cyxbs.discover.electricity.adapter.ElectricityFeedUnboundAdapter
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.discover.electricity.viewmodel.ChargeViewModel
import org.jetbrains.anko.support.v4.defaultSharedPreferences

@Route(path = DISCOVER_ELECTRICITY_FEED)
class ElectricityFeedFragment : BaseFeedFragment<ChargeViewModel>() {


    override val viewModelClass: Class<ChargeViewModel> = ChargeViewModel::class.java

    override fun onResume() {
        super.onResume()
        val pos = defaultSharedPreferences.getInt(SP_BUILDING_HEAD_KEY, -1)
        setAdapter(ElectricityFeedUnboundAdapter())
        setTitle("电费查询")
        setOnClickListener {
            fragmentManager?.let {
                ElectricityFeedSettingDialogFragment { id, room ->
                    refreshCharge(id, room)
                }.show(it, "ElectricityFeedSetting")
            }

        }
        viewModel.chargeInfo.observe {
            it?.let {
                setAdapter(ElectricityFeedAdapter(it))
                setSubtitle(it.recordTime.plus("抄表"))
            }
        }
        if (pos == -1) {
            return
        }
        val id = BUILDING_NAMES.getValue(BUILDING_NAMES_HEADER[pos])[defaultSharedPreferences.getInt(SP_BUILDING_FOOT_KEY, -1)].split("(")[1].split("栋")[0]
        val room = defaultSharedPreferences.getString(SP_ROOM_KEY, "") ?: ""


        refreshCharge(id, room)




    }

    private fun refreshCharge(id: String, room: String) {
        viewModel.getCharge(id, room)
    }
}
