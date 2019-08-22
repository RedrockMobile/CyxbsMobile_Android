package com.mredrock.cyxbs.course.viewmodels

import android.app.Application
import android.util.SparseArray
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.AddAffairEvent
import com.mredrock.cyxbs.course.event.ModifyAffairEvent
import com.mredrock.cyxbs.course.network.AffairHelper
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.collections.forEach

/**
 * Created by anriku on 2018/9/9.
 */

class EditAffairViewModel(application: Application) : AndroidViewModel(application) {

    val selectedWeekString: MutableLiveData<String> = MutableLiveData()
    val selectedTimeString: MutableLiveData<String> = MutableLiveData()
    val selectedRemindString: MutableLiveData<String> = MutableLiveData()
    // 周一到周日的字符串数组
    val dayOfWeekArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        getApplication<Application>().resources.getStringArray(R.array.course_course_day_of_week_strings)
    }
    // 课程代号字符串数组
    val courseTimeArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        getApplication<Application>().resources.getStringArray(R.array.course_time_select_strings)
    }
    // 周数对应的字符串数组
    val weekArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        getApplication<Application>().resources.getStringArray(R.array.course_course_weeks_strings)
    }
    // 提醒对应的字符串数组
    val remindArray: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        getApplication<Application>().resources.getStringArray(R.array.course_remind_strings)
    }
    // 进行事务提交网络请求网络请求
    private val mCourseApiService by lazy(LazyThreadSafetyMode.NONE) {
        ApiGenerator.getApiService(CourseApiService::class.java)
    }

    // 用于存储被选择的周数。其中key表示选择的周数在weekArray中的位置，value就是对应的CheckBox。
    val isSelectedWeekViews: SparseArray<CheckBox> by lazy(LazyThreadSafetyMode.NONE) { SparseArray<CheckBox>(weekArray.size) }
    // 用于存储被选择的CourseTime。其中key表示选择的时间 在courseTimeArray的位置 * 7 + dayOfWeekArray中的位置,
    // value是对应的CheckBox。
    val isSelectedTimeViews: SparseArray<CheckBox> by lazy(LazyThreadSafetyMode.NONE) { SparseArray<CheckBox>(7 * 6) }

    // 事务标题
    var title: MutableLiveData<String> = MutableLiveData()
    // 事务内容
    var content: MutableLiveData<String> = MutableLiveData()

    // 记录要被post的课程时间和日期对
    private val mPostClassAndDays: MutableList<Pair<Int, Int>> by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Pair<Int, Int>>() }
    // 记录要被post的选择的周数
    private val mPostWeeks: MutableList<Int> by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Int>() }
    // 记录要被post的选择的提醒时间
    var postRemind: Int = 0
        private set

    // 存储传给EditAffairActivity的课程信息。就是由在课表中显示的事务跳转过来进行修改的事务信息。作为一个字符，
    // 在后面好对修改还新建事务进行判断
    var passedAffairInfo: Course? = null


    /**
     * 此方用于对[com.mredrock.cyxbs.course.ui.EditAffairActivity]进行周数选择、课程时间选择等相关内容的初始化。
     * 比如说从[com.mredrock.cyxbs.common.component.ScheduleView]中通过点击touchView、已有的事务进来要对其
     * 信息显示初始化。
     *
     * @param activity [com.mredrock.cyxbs.course.ui.EditAffairActivity]
     */
    fun initData(activity: FragmentActivity) {
        val intent = activity.intent
        //通过点击事务进行修改获取的数据
        passedAffairInfo = intent.getParcelableExtra(EditAffairActivity.AFFAIR_INFO)
        //通过点击touchView进来获取的位置信息
        val passedWeekPosition = intent.getIntExtra(EditAffairActivity.WEEK_NUM, -1)
        val passedTimePosition = intent.getIntExtra(EditAffairActivity.TIME_NUM, -1)

        if (passedWeekPosition != -1 && passedTimePosition != -1) {
            setWeeksSelectString(mutableListOf(passedWeekPosition))
            setTimeSelectString(mutableListOf(passedTimePosition))
            selectedRemindString.value = getApplication<Application>().resources.getString(R.string.course_remind_select)
        } else if (passedAffairInfo != null) {
            setPassedAffairInfo()
        } else {
            setDefaultInitValue()
        }
    }

    /**
     * 直接点右上添加事务的菜单选项后的初始化
     */
    private fun setDefaultInitValue() {
        selectedWeekString.value = getApplication<Application>().resources.getString(R.string.course_week_select)
        selectedTimeString.value = getApplication<Application>().resources.getString(R.string.course_time_select)
        selectedRemindString.value = getApplication<Application>().resources.getString(R.string.course_remind_select)
    }

    /**
     * 进行绑定设置，在周数、时间和提醒改变后将其存储到对应将要被上传的属性中。
     *
     * @param activity 该ViewModel所依赖的Activity。这里只是将FragmentActivity作为参数，因此不用担心内存泄漏的问题。
     */
    fun observeWork(activity: FragmentActivity) {
        // 周数确定后真正要上传的数据存储
        selectedWeekString.observe(activity, Observer {
            // 将旧的存储的数据清理掉
            mPostWeeks.clear()
            isSelectedWeekViews.forEach { key, _ ->
                //key == 0也就是全学期的情况
                if (key == 0) {
                    for (i in 1..(weekArray.size - 1)) {
                        mPostWeeks.add(i)
                    }
                    return@Observer
                }
                mPostWeeks.add(key)
            }
        })

        // 时间确定后真正要上传的数据的存储
        selectedTimeString.observe(activity, Observer {
            // 将旧的存储的数据清理掉
            mPostClassAndDays.clear()
            isSelectedTimeViews.forEach { key, _ ->
                val classX: Int = key / 7
                val day: Int = key - classX * 7
                mPostClassAndDays.add(Pair(classX, day))
            }
        })

        // 提醒确定后真正要上传的数据的存储
        selectedRemindString.observe(activity, Observer {
            val remindStrings = activity.resources.getStringArray(R.array.course_remind_strings)
            val position = remindStrings.indexOf(it)

            setThePostRemind(position)
        })
    }


    /**
     * 此方法用于提交新的事务或者是修改已有事务
     *
     * @param activity [com.mredrock.cyxbs.course.ui.EditAffairActivity]用于在完成后将其finish掉。
     * @param title 事务标题
     * @param content 事务内容
     */
    fun postOrModifyAffair(activity: FragmentActivity, title: String, content: String) {
        BaseApp.user ?: return

        val stuNum = BaseApp.user!!.stuNum ?: return
        val idNum = BaseApp.user!!.idNum ?: return
        val date = AffairHelper.generateAffairDateString(mPostClassAndDays, mPostWeeks)

        if (passedAffairInfo == null) {
            val id = AffairHelper.generateAffairId()

            mCourseApiService.addAffair(id, stuNum, idNum, date, postRemind, title, content)
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                        activity.finish()
                        // 更新课表
                        EventBus.getDefault().post(AddAffairEvent())
                    }))
        } else {
            mCourseApiService.modifyAffair(passedAffairInfo!!.courseId.toString(), stuNum, idNum, date,
                    postRemind, title, content)
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                        activity.finish()
                        EventBus.getDefault().post(ModifyAffairEvent())
                    }))
        }
    }


    /**
     * 此方法用于根据提醒的的位置来设置对应的将要post的提醒值
     * @param position 对应的[remindArray]的位置
     */
    private fun setThePostRemind(position: Int) {
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
        val passedWeekPositions = mutableListOf<Int>()
        // 对Week数据重现
        passedAffairInfo?.week?.let {
            for (week in it) {
                passedWeekPositions.add(week)
            }
        }
        setWeeksSelectString(passedWeekPositions)

        // 对CourseTime数据重现
        val passedTimePositions = mutableListOf<Int>()
        passedAffairInfo?.affairDates?.let {
            for (date in it) {
                passedTimePositions.add(date.classX * 7 + date.day)
            }
        }
        setTimeSelectString(passedTimePositions)

        // 对提醒时间重现
        val remindTime = passedAffairInfo?.affairTime
        if (remindTime == null){
            selectedRemindString.value = getApplication<Application>().resources.getString(R.string.course_remind_select)
        }else{
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
     * 此方法用于在[com.mredrock.cyxbs.course.ui.WeekSelectDialogFragment]完成选择后设置[selectedWeekString]
     */
    fun setWeekSelectStringFromFragment() {
        val weekNumbers = mutableListOf<Int>()
        isSelectedWeekViews.forEach { key, _ ->
            weekNumbers.add(key)
        }
        setWeeksSelectString(weekNumbers)
    }


    /**
     * 此方法用于在[com.mredrock.cyxbs.course.ui.TimeSelectDialogFragment]完成选择后调用设置[selectedTimeString]
     */
    fun setTimeSelectStringFromFragment() {
        val timeNumbers = mutableListOf<Int>()
        isSelectedTimeViews.forEach { key, _ ->
            timeNumbers.add(key)
        }
        setTimeSelectString(timeNumbers)
    }


    /**
     * 此方法用于根据传入的课程字符串的的position List来生成对应的字符串
     *
     * @param weeksPositions 传入的一系列课程字符串的position
     */
    private fun setWeeksSelectString(weeksPositions: List<Int>) {
        // 添加字符串头
        val selectedWeekStringBuilder = StringBuilder().apply {
            append("第")
        }

        for ((index, position) in weeksPositions.withIndex()) {
            // key == 0也就是整学期的情况
            if (position == 0) {
                // 在进行选择周数完成、进行事务修改初始化的时候将isSelectedWeekViews位置记下。但是checkBox此时
                // 不用了因此传入null。特别是在周数选择完成的时候就对其CheckBox进行释放了。
                isSelectedWeekViews.put(position, null)
                // 设置周数显示的字符串
                selectedWeekString.value = weekArray[position]
                return
            }
            isSelectedWeekViews.put(position, null)

            selectedWeekStringBuilder.append(position)
            if (index != weeksPositions.size - 1) {
                selectedWeekStringBuilder.append(",")
            }
        }
        // 添加字符串尾
        selectedWeekStringBuilder.append("周")
        selectedWeekString.value = selectedWeekStringBuilder.toString()
    }


    /**
     * 此方法用于根据传入的选择的课程时间来生成对应的课程选择的字符串。
     *
     * @param timeSelectPositions 对应的课程字符串的position
     */
    private fun setTimeSelectString(timeSelectPositions: List<Int>) {
        val timeStringBuilder = StringBuilder()

        for ((index, position) in timeSelectPositions.withIndex()) {
            // 在进行选择课程时间完成、进行事务修改初始化的时候将isSelectedTimeViews位置记下。但是checkBox此时
            // 不用了因此传入null。特别是在课程时间选择完成的时候就对其CheckBox进行释放了。
            isSelectedTimeViews.put(position, null)
            // 获取选择的课程时间的行列位置
            val row: Int = position / 7
            val column: Int = position - row * 7

            timeStringBuilder.append("${dayOfWeekArray[column]}${courseTimeArray[row]}")

            if (index != timeSelectPositions.size - 1) {
                timeStringBuilder.append(",")
            }
        }

        selectedTimeString.value = timeStringBuilder.toString()
    }

    /**
     * 此方法用于设定提醒字符串。
     * @param position 在[remindArray]中的索引位置
     */
    fun setRemindSelectString(position: Int) {
        selectedRemindString.value = remindArray[position]
    }
}