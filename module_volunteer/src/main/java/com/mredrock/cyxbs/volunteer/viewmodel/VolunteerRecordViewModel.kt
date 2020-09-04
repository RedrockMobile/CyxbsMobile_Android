package com.mredrock.cyxbs.volunteer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

/**
 * Created by yyfbe, Date on 2020/9/3.
 */
class VolunteerRecordViewModel : BaseViewModel() {


    val volunteerTime = MutableLiveData<VolunteerTime>()
}