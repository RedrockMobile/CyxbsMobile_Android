package com.mredrock.cyxbs.course.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import java.util.*

/**
 * Created by anriku on 2018/8/21.
 */

class CoursePageViewModel(private val mWeek: Int) : ViewModel() {
    
    
    var month = ""
    val daysOfMonth = Array(7) {""}
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
        if (mWeek != 0) {
            val firstCalendar = SchoolCalendar.getFirstMonDayOfTerm()
            if (firstCalendar != null) {
                firstCalendar.add(Calendar.DATE, (mWeek - 1) * 7)
                month = "${firstCalendar.get(Calendar.MONTH) + 1}月"
            }
        }
    }
    
    /**
     * 此方法用于获取当前周各天的号数。
     */
    private fun getDaysOfMonth() {
        if (mWeek == 0) {
            return
        }
        // 获取这一周的各个在月份中的号数
        val calendar = SchoolCalendar.getFirstMonDayOfTerm()
        if (calendar != null) {
            calendar.add(Calendar.DATE, (mWeek - 1) * 7)
            for (i in 0 until 7) {
                daysOfMonth[i] = "${calendar.get(Calendar.DATE)}日"
                calendar.add(Calendar.DATE, 1)
            }
        }
    }


    class DateViewModelFactory(private val mWeek: Int) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CoursePageViewModel(mWeek) as T
        }
    }
}