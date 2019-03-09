package com.mredrock.cyxbs.course.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.SP_WIDGET_NEED_FRESH
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.course.database.ScheduleDatabase
import com.mredrock.cyxbs.course.event.AffairFromInternetEvent
import com.mredrock.cyxbs.course.event.RefreshEvent
import com.mredrock.cyxbs.course.network.*
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import io.reactivex.*
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * [CoursesViewModel]获取课表所遇到的坑有必要在这里做一下记录。
 * 1. 请注意使用[android.arch.persistence.room.Room]的时候，如果[io.reactivex.Observer]没有及时的被dispose
 * 掉。在以后数据库中的数据发生变化后。对应的[io.reactivex.Observable]会继续给其[io.reactivex.Observer]发射
 * 数据。为了使[io.reactivex.Observer]使用一次后就是被回收掉，可以使用[ExecuteOnceObserver].
 *
 * 2. 在[android.support.v4.view.ViewPager]中对于那种不是针对单独的Fragment做的事情不要放到ViewPager的Fragment中。
 * 因为ViewPager默认是会加载当前Fragment两边的Fragment的。这里举个遇到问题的例子：之前我将增删事务的之后的数据
 * 刷新放到了Fragment中。在进行操作之后就会进行三次数据的刷新。
 * 再加上当时我的另一个致命的逻辑问题，我将[mDataGetStatus]的状态重置放到了[isGetAllData]中。当时的代码。
 *      private fun isGetAllData(index: Int) {
 *           mDataGetStatus[index] = true
 *           if (mDataGetStatus[0] && mDataGetStatus[1]) {
 *           courses.value = mCourses
 *           mDataGetStatus[0] = false
 *           mDataGetStatus[1] = false
 *           stopRefresh()
 *           }
 *       }
 * 然后，在进行的三次更新请求中[getAffairsDataFromInternet]所使用的时间要比[getCoursesDataFromInternet]少些。
 * 也就是可能[getAffairsDataFromInternet]执行完三次后[getCoursesDataFromInternet]才执行1～2次。这是
 * [mDataGetStatus]的状态就是false、true。之后再进行刷新的时候就会调用[getAffairsDataFromInternet]了之后，
 * 就变为true、true进行数据更新。然后在调用[getCoursesDataFromInternet]又变为true、false的情况了。(调用顺序这样
 * 是因为课程数据要多些，其调用时间长些)。
 *
 * Created by anriku on 2018/8/18.
 */
class CoursesViewModel : ViewModel() {

    companion object {
        private const val TAG = "CoursesViewModel"
    }

    // schoolCalendarUpdated用于表示是否从网络请求到了新的数据并更新了SchoolCalendar，如果是这样就设置为True，
    // 并从新获取课表上的号数
    val schoolCalendarUpdated = MutableLiveData<Boolean>().apply {
        value = false
    }

    val courses = MutableLiveData<MutableList<Course>>()
    // 表示今天是在第几周。
    var nowWeek = MutableLiveData<Int>().apply {
        SchoolCalendar().weekOfTerm.let {
            value = if (it in 1..21) {
                it
            } else {
                0
            }
        }
    }

    private val mCoursesDatabase: ScheduleDatabase? by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDatabase.getDatabase(BaseApp.context)
    }
    private val mCourseApiService: CourseApiService  by lazy(LazyThreadSafetyMode.NONE) {
        ApiGenerator.getApiService(CourseApiService::class.java)
    }
    private lateinit var mCourses: MutableList<Course>

    // 第一个值表示课程是否获取，第二个表示是否获取事务。
    private val mDataGetStatus = arrayOf(false, false)
    // 表示说是否用SwipeRefreshLayout进行课程的刷新
    private var isUseSwipeRefreshLayout: Boolean = false
    // 表示现在是否正在获取数据
    private var mIsGettingData: Boolean = false

    /**
     * 此方法用于获取Course和Affair数据
     */
    fun getSchedulesData(context: Context) {
        if (mIsGettingData) {
            return
        }
        mIsGettingData = true

        resetGetStatus()
        EventBus.getDefault().post(RefreshEvent(true))
        isUseSwipeRefreshLayout = true
        getCoursesDataFromDatabase()
        getAffairsDataFromDatabase()
        getNowWeek(context)
    }

    /**
     * 此方法用于对重新从服务器上获取数据
     */
    fun refreshScheduleData(isUseSwipeRefreshLayout: Boolean) {
        if (mIsGettingData) {
            return
        }
        mIsGettingData = true

        resetGetStatus()
        this.isUseSwipeRefreshLayout = isUseSwipeRefreshLayout
        getCoursesDataFromInternet(true)
        getAffairsDataFromInternet()
    }

    /**
     * 此方法用于获取数据库中的课程数据，如果数据库中的数据为空，就从服务器中获取。
     */
    private fun getCoursesDataFromDatabase() {
        mCoursesDatabase ?: return

        mCoursesDatabase!!.courseDao()
                .queryAllCourses()
                .toObservable()
                .setSchedulers()
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { coursesFromDatabase ->
                    if (coursesFromDatabase != null && coursesFromDatabase.isNotEmpty()) {
                        mCourses.addAll(coursesFromDatabase)
                        isGetAllData(0)
                    } else {
                        getCoursesDataFromInternet()
                    }
                }, onExecuteOnceError = {
                    stopRefresh()
                }))
    }

    /**
     * 此方法用于获取数据库中的事务数据，如果数据库中的数据为空，就从服务器中获取。
     */
    private fun getAffairsDataFromDatabase() {
        mCoursesDatabase ?: return

        mCoursesDatabase!!.affairDao()
                .queryAllAffairs()
                .toObservable()
                .setSchedulers()
                .map(AffairMapToCourse())
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { affairsFromDatabase ->
                    if (affairsFromDatabase != null && affairsFromDatabase.isNotEmpty()) {
                        mCourses.addAll(affairsFromDatabase)
                        isGetAllData(1)
                    } else {
                        getAffairsDataFromInternet()
                    }
                }, onExecuteOnceError = {
                    stopRefresh()
                }))
    }

    /**
     * 此方法用于从服务器中获取课程数据
     */
    private fun getCoursesDataFromInternet(isForceFetch: Boolean = false) {
        mCourseApiService.getCourse(stuNum = BaseApp.user!!.stuNum!!, isForceFetch = isForceFetch)
                .setSchedulers()
                .errorHandler()
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { coursesFromInternet ->
                    coursesFromInternet.data?.let { notNullCourses ->
                        mCourses.addAll(notNullCourses)
                        isGetAllData(0)
                        LogUtils.d(TAG, notNullCourses.toString())

                        //将从服务器中获取的课程数据存入数据库中
                        Thread {
                            //从网络中获取数据后先对数据库中的数据进行清除，再向其中加入数据
                            mCoursesDatabase?.courseDao()?.deleteAllCourses()
                            mCoursesDatabase?.courseDao()?.insertCourses(notNullCourses)
                        }.start()
                    }

                    // 存储窗口小部件需要的Json数据
                    if (coursesFromInternet.data?.isEmpty() != true) {
                        BaseApp.context.defaultSharedPreferences.editor {
                            putBoolean(SP_WIDGET_NEED_FRESH, true)
                            putString(WIDGET_COURSE, Gson().toJson(coursesFromInternet))
                        }
                    }
                }, onExecuteOnceError = {
                    stopRefresh()
                }))
    }


    /**
     * 此方法用于从服务器上获取事务数据
     */
    private fun getAffairsDataFromInternet() {
        mCourseApiService.getAffair(stuNum = BaseApp.user!!.stuNum!!, idNum = BaseApp.user!!.idNum!!)
                .setSchedulers()
                .errorHandler()
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { affairsFromInternet ->
                    affairsFromInternet.data?.let { notNullAffairs ->
                        //将从服务器上获取的事务映射为课程信息。
                        Observable.create(ObservableOnSubscribe<List<Affair>> {
                            it.onNext(notNullAffairs)
                        }).setSchedulers()
                                .errorHandler()
                                .map(AffairMapToCourse())
                                .subscribe {
                                    EventBus.getDefault().post(AffairFromInternetEvent(it))
                                    mCourses.addAll(it)
                                    isGetAllData(1)
                                }

                        //在子线程中将事务数据存储到数据库中
                        Thread {
                            //从网络中获取数据后先对数据库中的数据进行清除，再向其中加入数据
                            mCoursesDatabase?.affairDao()?.deleteAllAffairs()
                            mCoursesDatabase?.affairDao()?.insertAffairs(notNullAffairs)
                        }.start()
                    }
                }, onExecuteOnceError = {
                    stopRefresh()
                }))
    }

    /**
     * 这个方法用于判断是尝试获取了课程和事务
     */
    private fun isGetAllData(index: Int) {
        mDataGetStatus[index] = true
        if (mDataGetStatus[0] && mDataGetStatus[1]) {
            courses.value = mCourses
            stopRefresh()
        }
    }

    /**
     * 此方法用于重载课程获取状态
     */
    private fun resetGetStatus() {
        mCourses = mutableListOf()
        mDataGetStatus[0] = false
        mDataGetStatus[1] = false
    }


    /**
     * 这个方法用于今天是哪一周
     */
    private fun getNowWeek(context: Context) {
        mCourseApiService.getCourse("2016215039")
                .setSchedulers()
                .errorHandler()
                .map {
                    it.nowWeek
                }.subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                    val now = Calendar.getInstance()
                    // 下面一行用于获取当前学期的第一天。nowWeek表示的是今天是第几周，然后整个过程就是今天前去前面的整周
                    // 再减去这周过了几天。减去本周的算法是使用了一种源码、补码的思想。也就是通过取余。比如说当前是周一，
                    // 然后now.get(Calendar.DAY_OF_WEEK)对应的值为2，再+5 % 7得到0，因此就不需要减，其它的计算
                    // 也依次类推。
                    now.add(Calendar.DATE, -((it - 1) * 7 + (now.get(Calendar.DAY_OF_WEEK) + 5) % 7))
                    // 更新第一天
                    context.defaultSharedPreferences.editor {
                        putLong(SchoolCalendar.FIRST_DAY, now.timeInMillis)
                    }
                    schoolCalendarUpdated.value = true

                    if (nowWeek.value != it && it >= 1 && it <= 18) {
                        nowWeek.value = it
                    }
                }))
    }

    /**
     * 如果[android.support.v4.widget.SwipeRefreshLayout]的正在旋转，调用此方法用于停止更新旋转标志。
     * 这个方法无论是在获取数据出错还是在没有出错的情况下最终都会被调用因此这里对[mIsGettingData]进行状态设置。
     */
    private fun stopRefresh() {
        if (isUseSwipeRefreshLayout) {
            EventBus.getDefault().post(RefreshEvent(false))
        }
        mIsGettingData = false
    }
}