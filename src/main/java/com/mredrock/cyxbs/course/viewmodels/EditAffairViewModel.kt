package com.mredrock.cyxbs.course.viewmodels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.AddAffairEvent
import com.mredrock.cyxbs.course.event.ModifyAffairEvent
import com.mredrock.cyxbs.course.network.AffairHelper
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.activity.AffairEditActivity
import org.greenrobot.eventbus.EventBus

/**
 * Created by anriku on 2018/9/9.
 */
@Suppress("RemoveExplicitTypeArguments")
class EditAffairViewModel : BaseViewModel() {

    //显示的字符串，提醒
    val selectedRemindString: MutableLiveData<String> = MutableLiveData()

    // 周一到周日的字符串数组
    val dayOfWeekArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_course_day_of_week_strings)
    }

    // 周数对应的字符串数组
    val weekArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_course_weeks_strings)
    }

    //具体上课时间字符串
    val timeArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_time_select_strings)
    }

    // 提醒对应的字符串数组
    val remindArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_remind_strings)
    }

    // 进行事务提交网络请求网络请求
    private val mCourseApiService by lazy(LazyThreadSafetyMode.NONE) {
        ApiGenerator.getApiService(CourseApiService::class.java)
    }

    // 事务标题
    var title: MutableLiveData<String> = MutableLiveData()

    // 事务内容
    var content: MutableLiveData<String> = MutableLiveData()

    // 记录要被post的课程时间和日期对,这个有个Lint检查Kotlin的坑，所以在类上面添加了忽略检查
    val mPostClassAndDays: MutableList<Pair<Int, Int>> by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Pair<Int, Int>>() }

    // 记录要被post的选择的周数
    val mPostWeeks: MutableList<Int> by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Int>() }

    // 记录要被post的选择的提醒时间
    var postRemind: Int = 0
        private set

    // 存储传给EditAffairActivity的课程信息。就是由在课表中显示的事务跳转过来进行修改的事务信息。作为一个字符，
    // 在后面好对修改还新建事务进行判断
    var passedAffairInfo: Course? = null


    //当前事务activity的状态，分三个状态，设置标题，内容和时间
    var status: Status = Status.TitleStatus

    var titleCandidateList = MutableLiveData<List<String>>()


    /**
     * 此方用于对[com.mredrock.cyxbs.course.ui.EditAffairActivity]进行周数选择、课程时间选择等相关内容的初始化。
     * 比如说从[com.mredrock.cyxbs.common.component.ScheduleView]中通过点击touchView、已有的事务进来要对其
     * 信息显示初始化。
     *
     * @param editActivity [com.mredrock.cyxbs.course.ui.EditAffairActivity]
     */
    fun initData(editActivity: AffairEditActivity) {
        val intent = editActivity.intent
        //通过点击事务进行修改获取的数据
        passedAffairInfo = intent.getParcelableExtra(AffairEditActivity.AFFAIR_INFO)
        //通过点击touchView进来获取的位置信息
        val passedWeekPosition = intent.getIntExtra(AffairEditActivity.WEEK_NUM, -1)
        val passedTimePosition = intent.getIntExtra(AffairEditActivity.TIME_NUM, -1)
        getTitleCandidate()

        if (passedWeekPosition != -1 && passedTimePosition != -1) {
            if (passedWeekPosition == 0) {
                for (i in 1..21) {
                    mPostWeeks.add(i)
                }
            } else {
                mPostWeeks.add(passedWeekPosition)
            }
            setTimeSelected(mutableListOf(passedTimePosition))
            selectedRemindString.value = context.resources.getString(R.string.course_remind_select)
        } else if (passedAffairInfo != null) {
            setPassedAffairInfo()
        } else {
            selectedRemindString.value = context.resources.getString(R.string.course_remind_select)
        }
    }


    /**
     * 此方法用于提交新的事务或者是修改已有事务
     *
     * @param activity [com.mredrock.cyxbs.course.ui.EditAffairActivity]用于在完成后将其finish掉。
     * @param title 事务标题
     * @param content 事务内容
     */
    fun postOrModifyAffair(title: String, content: String) {
        ServiceManager.getService(IAccountService::class.java).getUserService()
        val date = AffairHelper.generateAffairDateString(mPostClassAndDays, mPostWeeks)

        if (passedAffairInfo == null) {
            val id = AffairHelper.generateAffairId()

            mCourseApiService.addAffair(id, date, postRemind, title, content)
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                        it.apply {
                            if (info == "success" && status == 200) {
                                //这里没有直接使用BaseViewModel里面的toastEvent
                                //因为在弹出时有可能toastEvent中持有的context已经回收，会出现无法弹出的情况
                                CyxbsToast.makeText(context, R.string.course_add_transaction_as_a_reminder, Toast.LENGTH_SHORT).show()
                                // 更新课表
                                EventBus.getDefault().post(AddAffairEvent())
                            } else {
                                CyxbsToast.makeText(context, R.string.course_add_transaction, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }))
        } else {
            mCourseApiService.modifyAffair(passedAffairInfo!!.courseId.toString(), date,
                    postRemind, title, content)
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                        it.apply {
                            if (info == "success" && status == 200) {
                                // 更新课表
                                CyxbsToast.makeText(context, R.string.course_modify_transaction_successful, Toast.LENGTH_SHORT).show()
                                EventBus.getDefault().post(ModifyAffairEvent())
                            } else {
                                CyxbsToast.makeText(context, R.string.course_network_error_modification_failed, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }))
        }
    }


    /**
     * 获取事务标题候选
     */
    private fun getTitleCandidate() {
        mCourseApiService.getTitleCandidate()
                .setSchedulers()
                .errorHandler()
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { candidate ->
                    if (candidate.status == 200) {
                        titleCandidateList.value = candidate.data
                    }
                }))
    }


    /**
     * 此方法用于根据提醒的的位置来设置对应的将要post的提醒值
     * @param position 对应的[remindArray]的位置
     */
    fun setThePostRemind(position: Int) {
        when (position) {
            0 -> {
                postRemind = 0
            }
            1 -> {
                postRemind = 5
            }
            2 -> {
                postRemind = 10
            }
            3 -> {
                postRemind = 20
            }
            4 -> {
                postRemind = 30
            }
            5 -> {
                postRemind = 60
            }
        }
    }

    /**
     * 此方法用于对要被修改的事务的信息进行重现显示。
     */
    private fun setPassedAffairInfo() {
        // 对Week数据重现
        passedAffairInfo?.week?.let {
            for (week in it) {
                mPostWeeks.add(week)
            }
        }

        // 对CourseTime数据重现
        val passedTimePositions = mutableListOf<Int>()
        passedAffairInfo?.affairDates?.let {
            for (date in it) {
                passedTimePositions.add(date.classX * 7 + date.day)
            }
        }
        setTimeSelected(passedTimePositions)

        // 对提醒时间重现
        val remindTime = passedAffairInfo?.affairTime
        if (remindTime == null) {
            selectedRemindString.value = context.resources.getString(R.string.course_remind_select)
        } else {
            setRemindSelectString(getRemindPosition(remindTime.toInt()))
        }

        // 对事务题目、内容重现
        passedAffairInfo?.let { title.value = it.course }
        passedAffairInfo?.let { content.value = it.classroom }
    }

    /**
     * 此方法用于根据具体的提醒时间获取在[remindArray]中的位置。
     */
    private fun getRemindPosition(remindValue: Int): Int {
        return when (remindValue) {
            5 -> 1
            10 -> 2
            20 -> 3
            30 -> 4
            60 -> 5
            else -> 0
        }
    }

    /**
     *
     * @param timeSelectPositions 对应的课程字符串的position
     */
    private fun setTimeSelected(timeSelectPositions: List<Int>) {
        for (position in timeSelectPositions) {
            // 获取选择的课程时间的行列位置
            val row: Int = position / 7
            val column: Int = position % 7
            mPostClassAndDays.add(Pair(row, column))
        }
    }

    /**
     * 此方法用于设定提醒字符串。
     * @param position 在[remindArray]中的索引位置
     */
    fun setRemindSelectString(position: Int) {
        selectedRemindString.value = remindArray[position]
    }


    enum class Status {
        TitleStatus, ContentStatus, AllDoneStatus
    }
}