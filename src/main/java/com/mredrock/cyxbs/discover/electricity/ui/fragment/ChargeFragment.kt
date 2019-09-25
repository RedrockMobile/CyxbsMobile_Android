package com.mredrock.cyxbs.discover.electricity.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.electricity.config.*
import com.mredrock.cyxbs.discover.electricity.viewmodel.ChargeViewModel
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_fragment_charge.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences

/**
 * Author: Hosigus
 * Date: 2018/9/13 16:41
 * Description: com.mredrock.cyxbs.electricity.ui.fragment
 */
class ChargeFragment : BaseViewModelFragment<ChargeViewModel>() {
    override val viewModelClass: Class<ChargeViewModel> = ChargeViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.electricity_fragment_charge, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pos = defaultSharedPreferences.getInt(SP_BUILDING_HEAD_KEY, -1)
        if (pos == -1) {
            NavHostFragment.findNavController(this).apply {
                popBackStack()
                popBackStack(R.id.electricity_nav_setting_fragment, true)
                navigate(R.id.electricity_nav_setting_fragment)
            }
            return
        }

        viewModel.chargeInfo.observe(this, Observer {
            it ?: return@Observer
            electricity_view.refresh(it.lastmoney, it.elecSpend)
            electricity_info_avg.setValue(it.getAverage())
            electricity_info_free.setValue(it.elecFree)
            electricity_info_start.setValue(it.elecStart)
            electricity_info_end.setValue(it.elecEnd)
            tv_electricity_info_cost.setValue(it.getEleCost())
            val t = "抄表日期： ${it.recordTime}"
            tv_electric_time.text = t
        })

        val id = BUILDING_NAMES.getValue(BUILDING_NAMES_HEADER[pos])[defaultSharedPreferences.getInt(SP_BUILDING_FOOT_KEY, -1)].split("(")[1].split("栋")[0]
        viewModel.getCharge(id, defaultSharedPreferences.getString(SP_ROOM_KEY, "") ?: "")
    }
}