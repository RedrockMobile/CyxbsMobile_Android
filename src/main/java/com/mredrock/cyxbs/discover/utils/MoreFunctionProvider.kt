package com.mredrock.cyxbs.discover.utils

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.discover.R
import java.lang.ref.SoftReference

/**
 * @author zixuan
 * 2019/11/20
 */
object MoreFunctionProvider {
    const val HOME_PAGE_FUNCTION_1 = "homePageFunction1"
    const val HOME_PAGE_FUNCTION_2 = "homePageFunction2"
    const val HOME_PAGE_FUNCTION_3 = "homePageFunction3"
    private var homeFunctions: SoftReference<MutableList<Function>> = SoftReference(mutableListOf())
    val functions = listOf(
            Function(R.drawable.discover_ic_empty_classroom, R.string.discover_title_empty_classroom, R.string.discover_detail_empty_classroom, StartActivityImpl(DISCOVER_EMPTY_ROOM)),
            Function(R.drawable.discover_ic_map, R.string.discover_title_map, R.string.discover_detail_map, StartActivityImpl(DISCOVER_MAP)),
            Function(R.drawable.discover_ic_bus_track, R.string.discover_title_bus_track, R.string.discover_detail_bus_track, StartActivityImpl(DISCOVER_SCHOOL_CAR)),
            Function(R.drawable.discover_ic_my_exam, R.string.discover_title_my_exam, R.string.discover_detail_my_exam, StartActivityAfterLogin("我的考试", DISCOVER_GRADES)),
            Function(R.drawable.discover_ic_no_class, R.string.discover_title_no_class, R.string.discover_detail_no_class, StartActivityAfterLogin("没课约", DISCOVER_NO_CLASS)),
            Function(R.drawable.discover_ic_other_course, R.string.discover_title_other_course, R.string.discover_detail_other_course, StartActivityImpl(DISCOVER_OTHER_COURSE)),
            Function(R.drawable.discover_ic_school_calendar, R.string.discover_title_school_calendar, R.string.discover_detail_school_calendar, StartActivityImpl(DISCOVER_CALENDAR)),
            Function(R.drawable.discover_ic_more_function, R.string.discover_title_more_function, R.string.discover_detail_more_function, StartActivityImpl(DISCOVER_MORE_FUNCTION)))

    //当有缓存时直接从缓存中获取，没有时从sp中拿
    fun getHomePageFunctions(): List<Function> {
        var func = homeFunctions.get()
        if (func == null) {
            homeFunctions = SoftReference(mutableListOf())
            func = homeFunctions.get()
        }
        if (func != null && func.size != 3) {
            val indexes: List<Int> = getHomePageFunctionsFromSp()
            for (index in indexes) {
                func.add(this.functions[index])
            }
            return func
        } else {
            if (func?.size == 3)
                return func
        }

        return listOf()
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


    class Function(var resource: Int, val title: Int, val detail: Int, val activityStarter: StartActivityAble)
    interface StartActivityAble {
        fun startActivity(context: Context)
    }

    class StartActivityImpl(private val routing: String) : StartActivityAble {
        override fun startActivity(context: Context) {
            ARouter.getInstance().build(routing).navigation(context)
        }
    }

    class StartActivityAfterLogin(private val msg: String, private val routing: String) : StartActivityAble {
        override fun startActivity(context: Context) {
            if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
                ARouter.getInstance().build(routing).navigation()
            } else {
                ServiceManager.getService(IAccountService::class.java).getVerifyService().askLogin(context, "请先登录才能使用${msg}哦~")
            }
        }

    }

}