package com.mredrock.cyxbs.course.viewmodels

import android.view.View
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.COURSE_VERSION
import com.mredrock.cyxbs.common.config.SP_WIDGET_NEED_FRESH
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.event.CurrentDateInformationEvent
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.ClassRoomParse
import com.mredrock.cyxbs.common.utils.Num2CN
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.database.ScheduleDatabase
import com.mredrock.cyxbs.course.event.AffairFromInternetEvent
import com.mredrock.cyxbs.course.network.AffairMapToCourse
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.network.CourseApiWrapper
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment
import com.mredrock.cyxbs.course.utils.CourseTimeParse
import com.mredrock.cyxbs.course.utils.getNowCourse
import com.mredrock.cyxbs.course.utils.getTodayCourse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * [CoursesViewModel]获取课表所遇到的坑有必要在这里做一下记录。
 * 请注意使用Room的时候，如果[io.reactivex.Observer]没有及时的被dispose
 * 掉。在以后数据库中的数据发生变化后。对应的[io.reactivex.Observable]会继续给其[io.reactivex.Observer]发射
 * 数据。为了使[io.reactivex.Observer]使用一次后就是被回收掉，可以使用[ExecuteOnceObserver].
 *
 * Created by anriku on 2018/8/18.
 */
class CoursesViewModel : BaseViewModel() {

    companion object {
        const val COURSE_TAG = 0
        const val AFFAIR_TAG = 1
    }

    //当前课表Fragment的显示的状态
    var courseState = CourseContainerEntryFragment.CourseState.OrdinaryCourse


    // schoolCalendarUpdated用于表示是否从网络请求到了新的数据并更新了SchoolCalendar，如果是这样就设置为True，
    // 并从新获取课表上的号数
    val schoolCalendarUpdated = MutableLiveData<Boolean>().apply { value = false }

    // 用于记录帐号
    private lateinit var mUserNum: String

    // 用于记录用户的真实名字，现在只在复用为老师课表的时候会用到，默认为空
    var mUserName: String = ""

    // 表明是否是在获取他人课表
    val isGetOthers: ObservableField<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        ObservableField<Boolean>(true)
    }

    //全部课程数据,如果加载了事务，事务也在其中
    val allCoursesData = mutableListOf<Course>()

    //这个变量只是用来通知课表发生了改变
    val notifyCourseDataChange = MutableLiveData<Unit>()

    /**
     * 用于单独储存真正的用于展示的课表数据
     *
     * 下面是一些无关紧要的话可以不看，只是解释这个值的重要性
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

    //用于显示在当前或者下一课程对象
    val nowCourse = ObservableField<Course?>()
    val nowCourseTime = object : ObservableField<String>(nowCourse) {
        override fun get(): String {
            val s = CourseTimeParse((nowCourse.get()?.hashLesson ?: 0) * 2,
                    nowCourse.get()?.period ?: 2)
            return "${s.parseStartCourseTime()}-${s.parseEndCourseTime()}"
        }
    }
    val nowCoursePlace = object : ObservableField<String>(nowCourse) {
        override fun get(): String? {
            return ClassRoomParse.parseClassRoom(nowCourse.get()?.classroom ?: "")
        }
    }
    val isAffair = object : ObservableField<Int>(nowCourse) {
        override fun get(): Int? {
            val isShow = nowCourse.get() != null
            return if (nowCourse.get() == null || nowCourse.get()?.courseNum == null && isShow)
                View.GONE
            else
                View.VISIBLE
        }
    }

    //是否是明天的课表，显示提示
    val tomorrowTips = ObservableField("")

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

    var isFirstLoadItemAnim = true

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


    /**
     * 此方法用于加载数据
     * 优先从数据库中获取Course和Affair数据，等待数据库中获取完毕之后
     * 再从网络上获取
     *
     * @param userNum 当显示他人课表的时候就传入对应的的学号。默认为空，之后会为其赋值对应的帐号。
     * @param direct 如果有需要的时候，可以传入true，跳过从数据库加载，直接从网络上加载
     */
    fun getSchedulesDataFromLocalThenNetwork(userNum: String, isGetOther: Boolean = false, direct: Boolean = false) {
        if (isContinueExecution()) return

        //重载获取状态
        resetGetStatus()

        mUserNum = userNum
        isGetOthers.set(isGetOther)

        if (direct) {
            getCoursesDataFromInternet()
        } else {//从数据库中获取课表数据
            mIsGottenFromInternet = true
            getCoursesDataFromDatabase()
        }

        // 如果是在查他人课表(mIsGetOthers为true)，就不进行备忘查询。
        if (isGetOthers.get() == true) {
            //直接将备忘获取状态变为已经获取
            isGetAllData(AFFAIR_TAG)
        } else {
            if (direct) {
                getAffairsDataFromInternet()
            } else {//从数据库中获取课表数据
                getAffairsDataFromDatabase()
            }
        }
    }

    /**
     * 是否可以继续获取数据
     * @return true 表示不可以，正在获取数据
     */
    private fun isContinueExecution(): Boolean {
        //如果现在正在获取数据，这次获取就失效，防止重复多次调用这个方法
        if (mIsGettingData) {
            return true
        }
        mIsGettingData = true
        return false
    }


    /**
     * 当对事务进行增删改的时候所调用的，可直接只更新事务不更新课表
     * 其实这里也没有更新课表的必要，在用户打开app之后课表发生改变这种事几率太小
     */
    fun refreshAffairFromInternet() {
        resetGetStatus()
        isContinueExecution()
        isGetAllData(COURSE_TAG)
        getAffairsDataFromInternet()
    }

    /**
     * 此方法用于对重新从服务器上获取数据，
     * 这个方法只可以在第一次获取数据时在获取途中调用，不可用于公用方法直接调用然后通过网络更新数据
     * 如果需要跳过数据库直接通过网络更新数据请使用[getSchedulesDataFromLocalThenNetwork]传入合适的参数
     *
     */
    private fun getSchedulesFromInternet() {
        resetGetStatus()

        getCoursesDataFromInternet()

        /**
         * 如果mIsGetOthers为true，就说明是他人课表查询pass掉备忘查询。
         * 反之就是用户在进行课表查询，这时就进行备忘的查询。
         */
        if (isGetOthers.get() == true) {
            isGetAllData(AFFAIR_TAG)
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
                            courses.addAll(coursesFromDatabase)
                        }
                    }, onExecuteOnFinal = { isGetAllData(COURSE_TAG) }))
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
                            affairs.addAll(affairsFromDatabase)
                        }
                    }, onExecuteOnFinal = { isGetAllData(AFFAIR_TAG) }))
        }
    }

    /**
     * 此方法用于从服务器中获取课程数据
     */
    private fun getCoursesDataFromInternet(isForceFetch: Boolean = false) {
        (if (isTeaCourse) mCourseApiService.getTeaCourse(mUserNum, mUserName) else mCourseApiService.getCourse(stuNum = mUserNum, isForceFetch = isForceFetch))
                .setSchedulers()
                .errorHandler()
                .doOnNext {
                    updateNowWeek(it.nowWeek)//涉及到UI操作，所以在UI线程
                }
                .observeOn(Schedulers.io())
                //课表容错处理
                .filter { if (courseAbnormalErrorHandling(it) && it.data != null) true else stopIntercept() }
                .doOnNext {
                    courses.addAll(it.data!!)//上面已经判断了
                    //将从服务器中获取的课程数据存入数据库中
                    //从网络中获取数据后先对数据库中的数据进行清除，再向其中加入数据
                    mCoursesDatabase?.courseDao()?.deleteAllCourses()
                    mCoursesDatabase?.courseDao()?.insertCourses(courses)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { coursesFromInternet ->
                    coursesFromInternet?.data?.let {
                        if (it.isNotEmpty() && isGetOthers.get() == false) {
                            toastEvent.value = R.string.course_course_update_tips
                            context.defaultSharedPreferences.editor {
                                //小部件缓存课表
                                putString(WIDGET_COURSE, Gson().toJson(coursesFromInternet))
                                putBoolean(SP_WIDGET_NEED_FRESH, true)
                                //储存课表版本
                                putString("${COURSE_VERSION}${mUserNum}", coursesFromInternet.version)
                            }
                        }
                        if (coursesFromInternet.status == 233) {
                            //错误码233是指教务在线无法获取课表了，现在用的课表是缓存在红岩服务器里面的
                            longToastEvent.value = R.string.course_use_cache
                        }
                    }
                }, onExecuteOnFinal = { isGetAllData(COURSE_TAG) }))
    }

    /**
     * 课表的容错处理
     *
     * @param coursesFromInternet 直接从网络上拉取的课表数据
     * 因为有一个list的序列化和字符串对比，不建议在主线程调用这个方法，所以加上这个注解
     * 当然，你非要主线程调用那也没办法，你把注解去掉吧,其实退一步说也没啥大的计算操作
     */
    @WorkerThread
    private fun courseAbnormalErrorHandling(coursesFromInternet: CourseApiWrapper<List<Course>>) =
            coursesFromInternet.data?.let { notNullCourses ->
                val courseVersion = context.defaultSharedPreferences.getString("${COURSE_VERSION}${mUserNum}", "")
                //写这个主要是为了优化下，不至于每次都要序列化两个list的course
                val compareClassScheduleStrings = fun(a: List<Course>, b: List<Course>): Boolean {
                    //对使用@Expose进行标记了的字段进行序列化对比
                    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    //因为我这里arrayList重写过，Gson判断类型的时候会出错，所以必须指明type
                    val aJson = gson.toJson(a, object : TypeToken<List<Course>>() {}.type)
                    val bJson = gson.toJson(b, object : TypeToken<List<Course>>() {}.type)
                    return aJson == bJson
                }

                /**防止服务器里面的课表抽风,所以这个弄了这么多条件，只有满足以下条件才会去替换数据库的课表
                 * 课表版本发生了变化或者从数据库中取出的课表与网络上的课表课数不一样或者原来数据库中没有课表现在取有课表了*/
                (coursesFromInternet.status == 200 || coursesFromInternet.status == 233)
                        && (
                        //  版本号不为空且版本号不同则更新
                        //（版本号都更新了，如果没有具体课表那我也是没办法，如果这里再做其他的对比有可能造成显示上学期的课，所以不能做其他判断）
                        (coursesFromInternet.version?.isNotBlank() == true && courseVersion != coursesFromInternet.version)
                                //数据库课表不为空，网络课表不为空那么如果这两个数量不相等就直接更新，如果数量还相等那就只有比较字符串了
                                //这么是为了防止上学期有课但是这学期就真没课了（大四）
                                || (courses.isNotEmpty() && notNullCourses.isNotEmpty() && (courses.size != notNullCourses.size || !compareClassScheduleStrings(courses as ArrayList<Course>, notNullCourses)))
                                //如果数据库里面的课表为空，但是网络上获取的不为空那么就更新
                                || (courses.isEmpty() && notNullCourses.isNotEmpty())
                        )
            } ?: false


    /**
     * 此方法用于从服务器上获取事务数据
     */
    private fun getAffairsDataFromInternet() {
        mCourseApiService.getAffair()
                .setSchedulers(observeOn = Schedulers.io())
                .errorHandler()
                .doOnNext {
                    it.data?.let { affairs ->
                        mCoursesDatabase?.affairDao()?.deleteAllAffairs()
                        mCoursesDatabase?.affairDao()?.insertAffairs(affairs)
                    }
                }
                .map { it.data?.let { it1 -> AffairMapToCourse().apply(it1) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { affairsCourse ->
                    affairsCourse ?: return@ExecuteOnceObserver
                    EventBus.getDefault().post(AffairFromInternetEvent(affairsCourse))
                    affairs.addAll(affairsCourse)
                }, onExecuteOnFinal = { isGetAllData(AFFAIR_TAG) }))
    }

    /**
     * 这个方法用于判断是尝试获取了课程和事务,之所以要这样是因为事务和课表分成了两个接口，同时请求
     * @param index 0代表获取了课表，1代表获取了事务
     *
     * 这里解释一下为什么这里要加上【同步】，虽然浪费一丢丢性能，但是这是很有必要的，
     * 这个方法会分别在异步【获取到事务和获取到课程】后调用，[mDataGetStatus]变量中的内容所有线程都可以更改的
     * 但此时就可能出现同步问题，最明显的影响就是获取了所有的数据但是没有进入第一个判断语句，从而导致不能显示课表
     * 实测，出现这种问题的概率很大，在我这几天尽百次的打开当中其中有4次出现了未进入第一个判断语句从到导致
     * [courses]有数据，但是[allCoursesData]没有，课表无法显示
     */
    @Synchronized
    private fun isGetAllData(index: Int) {
        if (!mIsGettingData) return
        mDataGetStatus[index] = true
        if (mDataGetStatus[0] && mDataGetStatus[1]) {
            //第一种情况没啥好讲的，第二种是为了规避那种本身就没有课的大四学生，
            // 没课也可以正常显示课表，如果有课也出现了第二种情况那么说明后端出啥问题了或者上面的容错机制还不够完善
            if (courses.isNotEmpty() || (courses.isEmpty() && affairs.isNotEmpty())) {
                //这里没有直接set，所以不能引起所绑定的监听
                allCoursesData.clear()
                allCoursesData.addAll(mutableListOf<Course>().apply {
                    addAll(courses)
                    addAll(affairs)
                })
                notifyCourseDataChange.value = Unit
                buildHeadData()
            }
            if (mIsGottenFromInternet) {
                mIsGottenFromInternet = false
                getSchedulesFromInternet()
            } else {
                stopIntercept()
            }
        }
    }

    fun buildHeadData() {
        val nowWeek = nowWeek.value
        if (nowWeek != null) {
            //获取当前的课程显示在上拉课表的头部
            getTodayCourse(allCoursesData, nowWeek)?.let { todayCourse ->
                val pair = getNowCourse(todayCourse, allCoursesData, nowWeek)
                nowCourse.set(pair.first)
                tomorrowTips.set(pair.second)
            }
        }
    }


    /**
     * 此方法用于重置课程获取状态
     */
    private fun resetGetStatus() {
        mDataGetStatus[COURSE_TAG] = false
        mDataGetStatus[AFFAIR_TAG] = false
    }

    /**
     * 用来更新周数和开学第一天service
     */
    private fun updateNowWeek(networkNowWeek: Int) {
        val firstDay = Calendar.getInstance()
        // 下面一行用于获取当前学期的第一天。nowWeek表示的是今天是第几周，然后整个过程就是今天前去前面的整周
        // 再减去这周过了几天。减去本周的算法是使用了一种源码、补码的思想。也就是通过取余。比如说当前是周一，
        // 然后now.get(Calendar.DAY_OF_WEEK)对应的值为2，再+5 % 7得到0，因此就不需要减，其它的计算
        // 也依次类推。
        firstDay.add(Calendar.DATE, -((networkNowWeek - 1) * 7 + (firstDay.get(Calendar.DAY_OF_WEEK) + 5) % 7))
        // 更新第一天
        context.defaultSharedPreferences.editor {
            putLong(SchoolCalendar.FIRST_DAY, firstDay.timeInMillis)
        }
        schoolCalendarUpdated.value = true

        if (this.nowWeek.value != networkNowWeek && networkNowWeek >= 1 && networkNowWeek <= 18) {
            this.nowWeek.value = networkNowWeek
        }else{
            //比如要进行一次赋值，因为考虑到寒假暑假周数超出限制
            //头部就要显示整学期字样
            this.nowWeek.value = 0
        }

        nowWeek.value?.let { nowWeek ->
            val now = Calendar.getInstance()
            //这个用来判断是不是可能处于是暑假的那段时间除非大变动应该暑假绝对是5，6，7，8，9，10月当中
            val isProbablySummerVacation: (Int) -> Boolean = { listOf(5, 6, 7, 8, 9, 10).contains(it) }
            val time = when {
                now.timeInMillis >= firstDay.timeInMillis && nowWeek != 0 ->
                    "第${Num2CN.number2ChineseNumber(nowWeek.toLong())}周 " +
                            "周${
                            if (now[Calendar.DAY_OF_WEEK] != 1)
                                Num2CN.number2ChineseNumber(now[Calendar.DAY_OF_WEEK] - 1.toLong())
                            else
                                "日"
                            }"
                nowWeek == 0 && isProbablySummerVacation(now[Calendar.MONTH] + 1) -> "暑假快乐鸭"
                nowWeek == 0 && !isProbablySummerVacation(now[Calendar.MONTH] + 1) -> "寒假快乐鸭"
                else -> "呜呼～,发生了意料之外的错误呀"
            }
            EventBus.getDefault().postSticky(CurrentDateInformationEvent(time))
        }
    }

    /**
     * 获取数据完毕，不再拦截
     * @return 返回值没啥意思，这里是被用来表示不拦截
     */
    private fun stopIntercept(): Boolean {
        mIsGettingData = false
        return false
    }

    fun clearCache() {
        mCoursesDatabase?.courseDao()?.deleteAllCourses()
        mCoursesDatabase?.affairDao()?.deleteAllAffairs()
    }

    //课程头部小条的动画方法，暂时不启用，没找到和BottomSheet滑动和其他动画共存并不会影响性能的
//    internal fun setTipsState(state: Float, course_tip: RedRockTipsView) {
//        if (state == 0f || state == 1f) {
//            course_tip.state = RedRockTipsView.CENTER
//            isNotTipOpen = true
//        } else {
//            if (isNotTipOpen) {
//                course_tip.state = RedRockTipsView.BOTTOM
//                isNotTipOpen = false
//            }
//        }
//    }
}