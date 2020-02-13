package com.mredrock.cyxbs.course.viewmodels

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.COURSE_VERSION
import com.mredrock.cyxbs.common.config.SP_WIDGET_NEED_FRESH
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.database.ScheduleDatabase
import com.mredrock.cyxbs.course.event.AffairFromInternetEvent
import com.mredrock.cyxbs.course.network.Affair
import com.mredrock.cyxbs.course.network.AffairMapToCourse
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.utils.CourseTimeParse
import com.mredrock.cyxbs.course.utils.getNowCourse
import com.mredrock.cyxbs.course.utils.getTodayCourse
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
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
class CoursesViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "CoursesViewModel"
    }


    // schoolCalendarUpdated用于表示是否从网络请求到了新的数据并更新了SchoolCalendar，如果是这样就设置为True，
    // 并从新获取课表上的号数
    val schoolCalendarUpdated = MutableLiveData<Boolean>().apply { value = false }
    // 用于记录帐号
    lateinit var mUserNum: String
    // 用于记录用户的真实名字，现在只在复用为老师课表的时候会用到，默认为空
    var mUserName: String = ""
    // 表明是否是在获取他人课表
    val isGetOthers: ObservableField<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        ObservableField<Boolean>(true)
    }

    //全部课程数据,如果加载了事务，事务也在其中
    val allCoursesData = ObservableField<MutableList<Course>>()

    /**
     * 用于储存真正的用于展示的数据,下面是一些无关紧要的话可以不看，只是解释这个值的重要性
     *
     * 为什么有了上面这个值还需要这个呢
     * 因为后端和教务在线总是时不时各种抽风，跟服务端提需求我又怕打架，但是每次抽风之后是用户遭殃,
     * 也是客户端人员首先被喷，为了防止客户端被喷的次数，尽管我做了课表拉取错误处理，
     * 我还是要做一个当从服务端拉取的数据突然变为0但是本地是有课而且课表版本没有变化状态码正常时的处理
     * 这样子可以避免那些本来就没课的同学
     */
    private val courses = object : ArrayList<Course>() {
        override fun addAll(elements: Collection<Course>): Boolean {
            clear()
            return super.addAll(elements)
        }
    }

    private val affairs = object : ArrayList<Course>() {
        override fun addAll(elements: Collection<Course>): Boolean {
            clear()
            return super.addAll(elements)
        }
    }


    /**在获取中用于组装事务和课程用的list，上面[allCoursesData]才是真正用于显示的*/
    private lateinit var mReceiveCourses: MutableList<Course>


    //用于显示在当前或者下一课程对象
    val nowCourse = ObservableField<Course?>()
    val nowCourseTime = object : ObservableField<String>(nowCourse) {
        override fun get(): String {
            val s = CourseTimeParse(nowCourse.get()?.hashLesson ?: 0 * 2, nowCourse.get()?.period
                    ?: 2)
            return "${s.parseStartCourseTime()}-${s.parseEndCourseTime()}"
        }
    }

    //是否展示当前或者下一课程，用于DataBinDing绑定
    val isShowCurrentSchedule = object : ObservableField<Int>(nowCourse) {
        override fun get(): Int? {
            return if (nowCourse.get() == null) View.GONE else View.VISIBLE
        }
    }

    //是否展示当前或者下一课程无课展示提示，用于DataBinDing绑定
    val isShowCurrentNoCourseTip = object : ObservableField<Int>(nowCourse) {
        override fun get(): Int? {
            return if (nowCourse.get() == null) View.VISIBLE else View.GONE
        }
    }

    //是否展示周数中的本周提示
    val isShowPresentTips: ObservableField<Int> = ObservableField(View.GONE)
    //回到本周是否显示
    val isShowBackPresentWeek: MutableLiveData<Int> by lazy(LazyThreadSafetyMode.NONE) {
        MutableLiveData<Int>().apply { value = View.GONE }
    }

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
        ScheduleDatabase.getDatabase(context, isGetOthers.get()!!, mUserNum)
    }
    private val mCourseApiService: CourseApiService by lazy(LazyThreadSafetyMode.NONE) {
        ApiGenerator.getApiService(CourseApiService::class.java)
    }

    // 第一个值表示课程是否获取，第二个表示是否获取事务。
    private val mDataGetStatus = arrayOf(false, false)
    // 表示现在是否正在获取数据
    private var mIsGettingData: Boolean = false
    // 用于记录是否时第一次因为数据库中拉取不到数据，通过网络请求进行数据的拉取。
    private var mIsGottenFromInternet = false

    // 用于记录当前Toolbar要显示的字符串
    var mWeekTitle: ObservableField<String> = ObservableField("")

    //是否是老师课表
    var isTeaCourse: Boolean = false

    private val accountService: IAccountService by lazy(LazyThreadSafetyMode.NONE) {
        ServiceManager.getService(IAccountService::class.java)
    }

    /**
     * 此方法用于加载数据
     * 优先从数据库中获取Course和Affair数据，等待数据库中获取完毕之后
     * 再从网络上获取
     *
     * @param context [Context]
     * @param userNum 当显示他人课表的时候就传入对应的的学号。默认为空，之后会为其赋值对应的帐号。
     * @param direct 如果有需要的时候，可以传入true，跳过从数据库加载，直接从网络上加载
     */
    fun getSchedulesDataFromLocalThenNetwork(context: Context, userNum: String? = null, direct: Boolean = false) {
        //如果现在正在获取数据，这次获取就失效，防止重复多次调用这个方法
        if (mIsGettingData) {
            return
        }
        mIsGettingData = true

        //重载获取状态
        resetGetStatus()

        // 如果stuNum为null，就说明是用户在进行课表查询。此时BaseApp.user!!.stuNum!!一定不为空
        mUserNum = if (userNum == null) {
            isGetOthers.set(false)
            accountService.getUserService().getStuNum()
        } else {
            isGetOthers.set(true)
            userNum
        }

        //获取本周是多少周
        getNowWeek(context)

        if (direct) {
            getCoursesDataFromInternet()
        } else {//从数据库中获取课表数据
            getCoursesDataFromDatabase()
        }

        // 如果是在查他人课表(mIsGetOthers为true)，就不进行备忘查询。
        if (isGetOthers.get() == true) {
            //直接将备忘获取状态变为已经获取
            isGetAllData(1)
        } else {
            if (direct) {
                getAffairsDataFromInternet()
            } else {//从数据库中获取课表数据
                getAffairsDataFromDatabase()
            }
        }
    }


    /**
     * 当对事务进行增删改的时候所调用的，可直接只更新事务不更新课表
     * 其实这里也没有更新课表的必要，在用户打开app之后课表发生改变这种事几率太小
     */
    fun refreshAffairFromInternet() {

        resetGetStatus()

        mReceiveCourses.addAll(courses)
        isGetAllData(0)

        getAffairsDataFromInternet()
    }

    /**
     * 此方法用于对重新从服务器上获取数据，
     * 这个方法只可以在第一次获取数据时在获取途中调用，不可用于公用方法直接调用然后通过网络更新数据
     * 如果需要跳过数据库直接通过网络更新数据请使用[getSchedulesDataFromLocalThenNetwork]传入合适的参数
     *
     * @param context [Context]
     */
    private fun getSchedulesFromInternet(context: Context) {
        resetGetStatus()

        getNowWeek(context)
        getCoursesDataFromInternet()

        /**
         * 如果mIsGetOthers为true，就说明是他人课表查询pass掉备忘查询。
         * 反之就是用户在进行课表查询，这时就进行备忘的查询。
         */
        if (isGetOthers.get() == true) {
            isGetAllData(1)
        } else {
            getAffairsDataFromInternet()
        }
    }

    /**
     * 此方法用于获取数据库中的课程数据。
     */
    private fun getCoursesDataFromDatabase() {
        mCoursesDatabase?.let { database ->
            database.courseDao()
                    .queryAllCourses()
                    .toObservable()
                    .setSchedulers()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { coursesFromDatabase ->
                        if (coursesFromDatabase != null && coursesFromDatabase.isNotEmpty()) {
                            mReceiveCourses.addAll(coursesFromDatabase)
                            courses.addAll(coursesFromDatabase)
                            isGetAllData(0)
                            getSchedulesFromInternet(context)
                        } else {
                            isGetAllData(0)
                        }
                    }, onExecuteOnceError = {
                        isGetAllData(0)
                    }))
        }
    }

    /**
     * 此方法用于获取数据库中的事务数据。
     */
    private fun getAffairsDataFromDatabase() {
        mCoursesDatabase?.let { database ->
            database.affairDao()
                    .queryAllAffairs()
                    .toObservable()
                    .setSchedulers()
                    .map(AffairMapToCourse())
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { affairsFromDatabase ->

                        if (affairsFromDatabase != null && affairsFromDatabase.isNotEmpty()) {
                            val tag = TreeSet<String>()
                            for (c in affairsFromDatabase) {
                                tag.add("${c.affairDates}+${c.course}+${c.classroom}")
                            }
                            mReceiveCourses.addAll(affairsFromDatabase)
                        }
                        isGetAllData(1)
                    }, onExecuteOnceError = {
                        isGetAllData(1)
                    }))
        }
    }

    /**
     * 此方法用于从服务器中获取课程数据
     */
    private fun getCoursesDataFromInternet(isForceFetch: Boolean = false) {
        (if (isTeaCourse) mCourseApiService.getTeaCourse(mUserNum, mUserName) else mCourseApiService.getCourse(stuNum = mUserNum, isForceFetch = isForceFetch))
                .setSchedulers()
                .errorHandler()
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { coursesFromInternet ->
                    if (coursesFromInternet.status == 200 || coursesFromInternet.status == 233) {
                        coursesFromInternet.data?.let { notNullCourses ->
                            mReceiveCourses.addAll(notNullCourses)
                            val courseVersion = context.defaultSharedPreferences.getString("${COURSE_VERSION}${mUserNum}", "")
                            /**防止服务器里面的课表抽风,所以这个弄了这么多条件，只有满足以下条件才会去替换数据库的课表
                             * 课表版本发生了变化或者从数据库中取出的课表与网络上的课表课数不一样或者原来数据库中没有课表现在取有课表了*/
                            if (courseVersion != coursesFromInternet.version
                                    || (courses.isNotEmpty() && notNullCourses.isNotEmpty() && courses.size != notNullCourses.size)
                                    || (courses.isEmpty() && notNullCourses.isNotEmpty())) {

                                isGetAllData(0)
                                //将从服务器中获取的课程数据存入数据库中
                                Thread {
                                    //从网络中获取数据后先对数据库中的数据进行清除，再向其中加入数据
                                    mCoursesDatabase?.courseDao()?.deleteAllCourses()
                                    mCoursesDatabase?.courseDao()?.insertCourses(notNullCourses)
                                }.start()
                                if (!coursesFromInternet.data?.isEmpty()!! && isGetOthers.get() == false) {
                                    toastEvent.value = R.string.course_course_update_tips
                                    context.defaultSharedPreferences.editor {
                                        //小部件缓存课表
                                        putString(WIDGET_COURSE, Gson().toJson(coursesFromInternet))
                                        putBoolean(SP_WIDGET_NEED_FRESH, true)
                                    }
                                }
                                //储存课表版本
                                context.defaultSharedPreferences.editor {
                                    putString("${COURSE_VERSION}${mUserNum}", coursesFromInternet.version)
                                }
                            }
                        }
                        if (coursesFromInternet.status == 233) {
                            //错误码233是指教务在线无法获取课表了，现在用的课表是缓存在红岩服务器里面的
                            longToastEvent.value = R.string.course_use_cache
                        }
                    }
                }, onExecuteOnceError = {
                    isGetAllData(0)
                }))
    }


    /**
     * 此方法用于从服务器上获取事务数据
     */
    private fun getAffairsDataFromInternet() {
        val stuNum = accountService.getUserService().getStuNum()
        val idNum = context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
        mCourseApiService.getAffair(stuNum = stuNum, idNum = idNum)
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
                                    mReceiveCourses.addAll(it)
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
                    isGetAllData(1)
                }))
    }

    /**
     * 这个方法用于判断是尝试获取了课程和事务,之所以要这样是因为事务和课表分成了两个接口，同时请求
     * @param index 0代表获取了课表，1代表获取了事务
     */
    private fun isGetAllData(index: Int) {
        mDataGetStatus[index] = true
        if (mDataGetStatus[0] && mDataGetStatus[1]) {
            // 如果mCourses为空的话就不用赋值给courses。防止由于网络请求有问题而导致刷新数据为空。
            if (mReceiveCourses.isNotEmpty()) {
                allCoursesData.set(mReceiveCourses)
                //获取当前的课程显示在上拉课表的头部
                nowWeek.value?.let { nowWeek ->
                    getTodayCourse(mReceiveCourses, nowWeek)?.let { todayCourse ->
                        allCoursesData.get()?.let {
                            nowCourse.set(getNowCourse(todayCourse, it, nowWeek))
                        }
                    }
                }
            } else {
                // 加个标志，防止因为没有课程以及备忘的情况进行无限循环拉取。
                if (!mIsGottenFromInternet) {
                    mIsGottenFromInternet = true
                    getSchedulesFromInternet(context)
                }
            }
            stopIntercept()
        }
    }


    /**
     * 此方法用于重置课程获取状态
     */
    private fun resetGetStatus() {
        mReceiveCourses = mutableListOf()
        mDataGetStatus[0] = false
        mDataGetStatus[1] = false
    }


    /**
     * 这个方法用于今天是哪一周
     *
     * @param context [Context]
     */
    private fun getNowWeek(context: Context) {
        mCourseApiService.getCourse(accountService.getUserService().getStuNum())
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
     * 获取数据完毕，不再拦截
     */
    private fun stopIntercept() {
        mIsGettingData = false
    }

    fun clearCache() {
        mCoursesDatabase?.courseDao()?.deleteAllCourses()
        mCoursesDatabase?.affairDao()?.deleteAllAffairs()
    }
}