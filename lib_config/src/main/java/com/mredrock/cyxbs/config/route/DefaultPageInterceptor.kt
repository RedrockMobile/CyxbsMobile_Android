package com.mredrock.cyxbs.config.route

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter

/**
 *@Author:SnowOwlet
 *@Date:2022/10/26 09:27
 *
 */
@Interceptor(name = "defaultPage", priority = 3)
class DefaultPageInterceptor: IInterceptor {
  //这个是需要拦截的内容
  private val defaultPages = arrayOf(
    //教务在线信息
    DISCOVER_NEWS,
    DISCOVER_NEWS_ITEM,
    //体育打卡
    DISCOVER_SPORT,
    DISCOVER_SPORT_FEED,
    //空教室
    DISCOVER_EMPTY_ROOM,
    //我的考试
    DISCOVER_GRADES,
    //志愿服务
    DISCOVER_VOLUNTEER,
    DISCOVER_VOLUNTEER_RECORD,
    DISCOVER_VOLUNTEER_FEED,
    //邮问
    QA_ENTRY,
    QA_QUIZ,
    QA_CIRCLE_DETAIL,
    QA_DYNAMIC_DETAIL,
    QA_DYNAMIC_MINE,
    QA_MY_COMMENT,
    QA_MY_IGNORE,
    QA_MY_PRAISE,
    QA_DYNAMIC_MINE_FRAGMENT,
    //个人中心
    MINE_PERSON_PAGE,
  )
  override fun init(context: Context) {
    println(context)
  }

  override fun process(postcard: Postcard, callback: InterceptorCallback) {
    if (postcard.path in defaultPages) {
      //如果是需要缺省页的
      callback.onInterrupt(null)
      ARouter.getInstance().build(DEFAULT_PAGE).greenChannel().navigation()
    }else{
      //正常跳转
      callback.onContinue(postcard)
    }
  }
}