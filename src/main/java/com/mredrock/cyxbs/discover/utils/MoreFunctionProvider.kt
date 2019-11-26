package com.mredrock.cyxbs.discover.utils

import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.TextUtils
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.pages.morefunction.MoreFunctionActivity
import org.greenrobot.eventbus.EventBus

/**
 * @author zixuan
 * 2019/11/20
 */
object MoreFunctionProvider {
    public const val HOME_PAGE_FUNCTION_1 = "homePageFunction1"
    public const val HOME_PAGE_FUNCTION_2 = "homePageFunction2"
    public const val HOME_PAGE_FUNCTION_3 = "homePageFunction3"
    val functions = listOf<Function>(Function(R.drawable.discover_ic_no_class, R.string.discover_title_no_class, R.string.discover_detail_no_class, StartActivityAfterLogin("没课约", DISCOVER_NO_CLASS)),
            Function(R.drawable.discover_ic_bus_track, R.string.discover_title_bus_track, R.string.discover_detail_bus_track, StartActivityImpl(DISCOVER_SCHOOL_CAR)),
            Function(R.drawable.diacover_ic_empty_classroom, R.string.discover_title_empty_classroom, R.string.discover_detail_empty_classroom, StartActivityImpl(DISCOVER_EMPTY_ROOM)),
            Function(R.drawable.discover_ic_my_exam, R.string.discover_title_my_exam, R.string.discover_detail_my_exam, StartActivityAfterLogin("我的考试", DISCOVER_GRADES)),
            Function(R.drawable.discover_ic_empty_stu_schedule, R.string.discover_title_empty_stu_schedule, R.string.discover_detail_empty_stu_schedule, StartActivityImpl(DISCOVER_OTHER_COURSE)),
            Function(R.drawable.discover_ic_school_calendar, R.string.discover_title_school_calendar, R.string.discover_detail_school_calendar, StartActivityImpl(DISCOVER_CALENDAR)),
            Function(R.drawable.discover_ic_map, R.string.discover_title_map, R.string.discover_detail_map, StartActivityImpl(DISCOVER_MAP)),
            Function(R.drawable.discover_ic_more_function, R.string.discover_title_more_function, R.string.discover_detail_more_function, StartActivityFromIntent(Intent(BaseApp.context, MoreFunctionActivity::class.java))))

    fun getHomePageFunctions(): List<Function> {
        val functions = mutableListOf<Function>()
        val indexes: List<Int> = getHomePageFunctionsFromSp()
        for (index in indexes) {
            functions.add(this.functions[index])
        }
        return functions
    }

    private fun getHomePageFunctionsFromSp(): List<Int> {
        val context = BaseApp.context
        val list = mutableListOf<Int>()
        context.defaultSharedPreferences.apply {
            list.add(getInt(HOME_PAGE_FUNCTION_1, 2))
            list.add(getInt(HOME_PAGE_FUNCTION_2, 1))
            list.add(getInt(HOME_PAGE_FUNCTION_3, 4))
        }
        list.add(functions.lastIndex)
        return list
    }

    fun saveHomePageFunctionsToSp(list: List<Int>) {
        val context = BaseApp.context
        context.defaultSharedPreferences.editor {
            putInt(HOME_PAGE_FUNCTION_1, list[0])
            putInt(HOME_PAGE_FUNCTION_2, list[1])
            putInt(HOME_PAGE_FUNCTION_3, list[2])
        }
    }

    class Function(var resource: Int, val title: Int, val detail: Int, val startActivityAble: StartActivityAble)
    public interface StartActivityAble {
        fun startActivity()
    }

    class StartActivityImpl(private val routing: String) : StartActivityAble {
        override fun startActivity() {
            ARouter.getInstance().build(routing).navigation()
        }
    }

    class StartActivityAfterLogin(private val msg: String, private val routing: String) : StartActivityAble {
        override fun startActivity() {
            if (BaseApp.isLogin) {
                LogUtils.e("11", "routing:$routing,${TextUtils.isEmpty(routing)},${routing.startsWith("/")}")
                ARouter.getInstance().build(routing).navigation()
            } else {
                EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用${msg}哦~"))
            }
        }

    }

    class StartActivityFromIntent(private val intent: Intent) : StartActivityAble {
        override fun startActivity() {
            BaseApp.context.startActivity(intent)
        }

    }

}