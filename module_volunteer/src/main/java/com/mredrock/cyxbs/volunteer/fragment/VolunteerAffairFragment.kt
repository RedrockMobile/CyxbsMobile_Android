package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerAffairAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffairDetail
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerAffairViewModel
import com.mredrock.cyxbs.volunteer.widget.VolunteerAffairBottomSheetDialog
import kotlinx.android.synthetic.main.volunteer_fragment_volunteer_affair.*

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerAffairFragment : BaseViewModelFragment<VolunteerAffairViewModel>() {
    private var volunteerAffairBottomSheetDialog: VolunteerAffairBottomSheetDialog? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.volunteer_fragment_volunteer_affair, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val volunteerAffair = VolunteerAffair(1, "荧光夜跑护跑活动",
                "通过帮助和引导同学们参与到夜跑项目中来，充当裁判，判断他们是否合格", 111111, 11111
        )
        swl_volunteer_affair.setOnRefreshListener {
            viewModel.getVolunteerAffair()
        }
        //todo 测试数据
        rl_volunteer_affair.layoutManager = LinearLayoutManager(context)
        rl_volunteer_affair.adapter = VolunteerAffairAdapter(listOf<VolunteerAffair>(volunteerAffair)) {
            viewModel.getVolunteerAffairDetail(it)
            viewModel.volunteerAffairDetail.value = VolunteerAffairDetail("cccc", "荧光夜跑护跑活动",
                    "通过帮助和引导同学们参与到夜跑项目中来，充当裁判，判断他们是否合格", 111111, "111")
        }
        viewModel.volunteerAffairs.observe {
            swl_volunteer_affair.isRefreshing = false
//            rl_volunteer_affair.adapter = VolunteerSchoolAdapter(it ?: return@observe)
        }
        viewModel.volunteerAffairDetail.observe { data ->
            if (context == null) return@observe
            if (volunteerAffairBottomSheetDialog == null) {
                volunteerAffairBottomSheetDialog = VolunteerAffairBottomSheetDialog(context!!).apply {
                    if (data != null) {
                        volunteerAffairBottomSheetDialog?.refresh(data)
                    }
                }
            }
            volunteerAffairBottomSheetDialog?.show()
        }

    }

    override val viewModelClass: Class<VolunteerAffairViewModel>
        get() = VolunteerAffairViewModel::class.java
}