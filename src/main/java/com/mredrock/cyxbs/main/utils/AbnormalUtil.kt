package com.mredrock.cyxbs.main.utils

import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.main.ui.MainActivity

/**
 * @author  Jon
 * @date  2020/4/8 23:37
 * description：用于辅助ManActivity进行一些异常重启的恢复操作的函数
 */


//根据ARouterPath来查询是否已经加载当前Fragment，以此来增强app在Activity异常重启时的稳定性
fun List<Fragment>.entryContains(aRouterPath: String, activity: MainActivity): Fragment? {
    val list = filter { it::class.java.name === activity.mainPageLoadedFragmentClassList[aRouterPath] }
    return if (list.isEmpty()) null else list[0]
}

/**
 * 用于Main初始化Fragment
 * @param data ARouter的注册路径
 * @param activity MainActivity
 * @param viewModel
 */
fun getFragment(data: String, activity: MainActivity) =
        activity.supportFragmentManager.fragments.entryContains(data, activity)
                ?: (ServiceManager.getService(data) as Fragment).apply {
                    activity.mainPageLoadedFragmentClassList[data] = this.javaClass.name
                }