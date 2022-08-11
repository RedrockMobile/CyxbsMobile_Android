package com.mredrock.cyxbs.widget.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.widget.IWidgetService
import com.mredrock.cyxbs.api.widget.WIDGET_SERVICE
import com.mredrock.cyxbs.api.widget.bean.Affair
import com.mredrock.cyxbs.api.widget.bean.Lesson
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase.Companion.MY_STU_NUM
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase.Companion.OTHERS_STU_NUM
import com.mredrock.cyxbs.widget.util.getMyLessons
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * description ： IWidgetService接口的实现类，通过发送延时广播通知小组件刷新
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/3 15:22
 */
@Route(path = WIDGET_SERVICE, name = WIDGET_SERVICE)
class WidgetService : IWidgetService {
    private var mContext: Context? = null

    override fun notifyWidgetRefresh(
        myLessons: List<Lesson>,
        otherStuLessons: List<Lesson>,
        affairs: List<Affair>,
    ) {
        //设置两者的学号，用于数据库查询
        if (myLessons.isNotEmpty()) {
            myLessons[0].stuNum.let {
                mContext?.getSharedPreferences(MY_STU_NUM, Context.MODE_PRIVATE)
                    ?.edit { putString(MY_STU_NUM, it) }
            }
        }
        if (otherStuLessons.isNotEmpty()) {
            mContext?.getSharedPreferences(OTHERS_STU_NUM, Context.MODE_PRIVATE)?.edit {
                putString(OTHERS_STU_NUM, otherStuLessons[0].stuNum)
            }
        }
        Observable.create<Int> {
            //将传入的来自api模块数据转化为该模块的对应数据并存入数据库
            getMyLessons(mContext!!,3).size
            LessonDatabase.getInstance(mContext!!).getLessonDao()
                .insertLessons(com.mredrock.cyxbs.widget.repo.bean.Lesson.convertFromApi(myLessons))
            LessonDatabase.getInstance(mContext!!).getLessonDao()
                .insertLessons(com.mredrock.cyxbs.widget.repo.bean.Lesson.convertFromApi(
                    otherStuLessons))
            AffairDatabase.getInstance(mContext!!).getAffairDao()
                .insertAffairs(com.mredrock.cyxbs.widget.repo.bean.Affair.convert(affairs))
            it.onNext(1)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            //延迟100ms,确保发送广播时已经将数据插入数据库
            .delay(100, TimeUnit.MILLISECONDS).subscribe {
                Log.d("testTag", "(WidgetService.kt:64) -> 2")
                widgetList.forEach { pkg ->
                    mContext?.sendBroadcast(Intent(actionFlush).apply {
                        component = ComponentName(mContext!!, pkg)
                    })
                }
            }
    }

    override fun init(context: Context?) {
        mContext = context
    }
}

const val actionFlush = "flush"
const val littleWidgetPkg = "com.mredrock.cyxbs.widget.widget.little.LittleWidget"
const val littleWidgetTransPkg = "com.mredrock.cyxbs.widget.widget.little.LittleTransWidget"
const val normalWidget = "com.mredrock.cyxbs.widget.widget.normal.NormalWidget"
const val oversizedAppWidget = "com.mredrock.cyxbs.widget.widget.oversize.OversizedAppWidget"
val widgetList = listOf(
    littleWidgetPkg,
    littleWidgetTransPkg,
    normalWidget,
    oversizedAppWidget
)