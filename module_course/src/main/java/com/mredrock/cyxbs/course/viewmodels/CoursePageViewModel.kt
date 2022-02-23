package com.mredrock.cyxbs.course.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.utils.SchoolCalendar

/**
 * Created by anriku on 2018/8/21.
 */

class CoursePageViewModel(private val mWeek: Int) : ViewModel() {


    val month = MutableLiveData<String>()
    var daysOfMonth = Array(7) {""}
    var nowWeek: Int = 0

    init {
        getDate()
    }

    /**
     * 此方法用于通过[SchoolCalendar]来进行日期的获取
     */
    fun getDate() {
        getMonth()
        getDaysOfMonth()
    }

    /**
     * 获取当前月份
     */
    private fun getMonth() {
        month.value = "${SchoolCalendar(mWeek, 1).month}月"
    }

    /**
     * 此方法用于获取当前周各天的号数。
     */
    private fun getDaysOfMonth() {
        // 获取这一周的各个在月份中的号数
        val daysOfMonth = Array(7) { "" }
        for (i in 1..7) {
            val schoolCalendar = SchoolCalendar(mWeek, i)
            daysOfMonth[i - 1] = "${schoolCalendar.day}日"
        }
        this.daysOfMonth = daysOfMonth
    }


    class DateViewModelFactory(private val mWeek: Int) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CoursePageViewModel(mWeek) as T
        }
    }
}