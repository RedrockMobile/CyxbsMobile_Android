package com.mredrock.cyxbs.main.utils

import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.main.ui.MainActivity

/**
 * @author  Jovines
 * @date  2020/4/8 23:37
 * description：用于辅助ManActivity进行一些异常重启的恢复操作的函数
 */


//根据ARouterPath来查询是否已经加载当前Fragment，以此来增强app在Activity异常重启时的稳定性
fun List<Fragment>.entryContains(aRouterPath: String, activity: MainActivity): Fragment? {
    val list = filter {
        //这里这个等号有个坑，记录一下
        //Activity由于内存原因被系统杀死之后，Bundle里面是有数据的，因为app推到后台的时候就进行了保存
        //但是不会调用onRestoreInstanceState,此时,由于我们这里是使用全类名来保证加载的
        //这里的这里的字符串内存地址不是一样的，也就是说字符串常量池中有这两个字符串
        it::class.java.name == activity.mainPageLoadedFragmentClassList[aRouterPath]
    }
    return if (list.isEmpty()) null else list[0]
}

/**
 * 用于Main初始化Fragment
 * @param data ARouter的注册路径
 * @param activity MainActivity
 */
fun getFragment(data: String, activity: MainActivity) =
        activity.supportFragmentManager.fragments.entryContains(data, activity)
                ?: (ServiceManager.getService(data) as Fragment).apply {
                    activity.mainPageLoadedFragmentClassList[data] = this.javaClass.name
                }