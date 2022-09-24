package com.mredrock.cyxbs.api.widget

import com.alibaba.android.arouter.facade.template.IProvider
import com.mredrock.cyxbs.api.widget.bean.Affair
import com.mredrock.cyxbs.api.widget.bean.Lesson

/**
 * description ： 小组件模块对外部模块暴露的接口
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/3 15:18
 */
interface IWidgetService:IProvider {
    /**通知小组件刷新
     * @param myLessons 当前用户的课表
     * @param otherStuLessons 双人课表中另一个人的课表
     * @param affairs 事务
     * */
    fun notifyWidgetRefresh(myLessons:List<Lesson>, otherStuLessons:List<Lesson>, affairs:List<Affair>)
}