package com.mredrock.cyxbs.common.bean

/**
 * @author  Jovines
 * @date  2020/4/7 17:39
 * @param isCheckLogin 是否需要登录检查,如果设置为false，可以自行调用[com.mredrock.cyxbs.common.ui.BaseActivity.checkIsLogin],方法进行登录检查
 * @param isWarnUser 如果没有登录，是否需要提示
 * @param warnMessage 提示内容
 * @param isFinish 登录成功之后是直接销毁登录页面返回到用户所想要打开的Activity，还是重新打开一个新的，
 *                 配置为true，那么登录完成之后会重新创建一个当前Activity进行显示
 *                 配置为false，那么登录完成之后会finish掉登录页返回到跳转前的activity
 *                 主要是为了那些必须要登录之后才能正确获取数据的activity需要设置，
 * description：用于activity对登录检查的配置
 */

data class LoginConfig(
        val isCheckLogin: Boolean = true,
        val isWarnUser: Boolean = true,
        val warnMessage: String = "掌友，你还没有登录哦",
        val isFinish: Boolean = false
)