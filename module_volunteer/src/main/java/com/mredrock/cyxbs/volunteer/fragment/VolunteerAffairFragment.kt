package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerAffairAdapter
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
        viewModel.getVolunteerAffair()
        swl_volunteer_affair.setOnRefreshListener {
            viewModel.getVolunteerAffair()
        }
        rl_volunteer_affair.layoutManager = LinearLayoutManager(context)
        viewModel.volunteerAffairs.observe {
            swl_volunteer_affair.isRefreshing = false
            val adapter = rl_volunteer_affair.adapter
            if (adapter == null) {
                rl_volunteer_affair.adapter = VolunteerAffairAdapter(it ?: return@observe) { id ->
                    viewModel.getVolunteerAffairDetail(id)
                }
            } else {
                it?.let { list -> (adapter as VolunteerAffairAdapter).refreshData(list) }
            }
        }
        viewModel.volunteerAffairDetail.observe { data ->
            if (context == null) return@observe
            if (volunteerAffairBottomSheetDialog == null) {
                volunteerAffairBottomSheetDialog = VolunteerAffairBottomSheetDialog(context!!).apply {
                    if (data != null) {
                        volunteerAffairBottomSheetDialog?.refresh(data)
                    }
                }
            } else {
                data?.let { volunteerAffairBottomSheetDialog?.refresh(it) }
            }
            volunteerAffairBottomSheetDialog?.show()
        }

    }

    override val viewModelClass: Class<VolunteerAffairViewModel>
        get() = VolunteerAffairViewModel::class.java
}