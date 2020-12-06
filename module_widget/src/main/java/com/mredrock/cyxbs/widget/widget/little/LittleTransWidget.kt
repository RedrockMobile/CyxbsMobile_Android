package com.mredrock.cyxbs.widget.widget.little

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.CourseStatus
import com.mredrock.cyxbs.widget.widget.page.trans.TransConfig
import com.mredrock.cyxbs.widget.util.filterClassRoom
import com.mredrock.cyxbs.widget.util.getClickPendingIntent
import com.mredrock.cyxbs.widget.util.getWeekDayChineseName
import java.util.*

/**
 * Created by zia on 2018/10/10.
 * 小型部件，不透明
 */
class LittleTransWidget : BaseLittleWidget() {

    override fun getLayoutResId(): Int {//获取当前小工具适配的ID
        return R.layout.widget_little_trans
    }

    override fun getShareName(): String {
        return "widget_share_little_trans"
    }

    override fun getUpResId(): Int {
        return 0
    }

    override fun getDownResId(): Int {
        return 0
    }

    override fun getTitleResId(): Int {
        return R.id.widget_little_title_trans
    }

    override fun getCourseNameResId(): Int {
        return R.id.widget_little_trans_courseName
    }

    override fun getRoomResId(): Int {
        return R.id.widget_little_trans_room
    }

    override fun getRefreshResId(): Int {
        return R.id.widget_little_title_trans
    }

    override fun getRemoteViews(context: Context, courseStatus: CourseStatus.Course?, timeTv: String): RemoteViews {
        val rv = RemoteViews(context.packageName, getLayoutResId())

        if (courseStatus == null) {
            rv.setTextViewText(getTitleResId(), getWeekDayChineseName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))
            rv.setTextViewText(getCourseNameResId(), "今天没有课~")
            rv.setTextViewText(getRoomResId(), "")
        } else {
            rv.setTextViewText(getTitleResId(), timeTv)
            rv.setTextViewText(getCourseNameResId(), courseStatus.course)
            rv.setTextViewText(getRoomResId(), filterClassRoom(courseStatus.classroom!!))
            //course和room设置点击事件到Activity，title设置刷新，没课才设置点击事件
            rv.setOnClickPendingIntent(getCourseNameResId(),
                    getClickPendingIntent(context,getCourseNameResId(),"btn.start.com",javaClass))
            rv.setOnClickPendingIntent(getRoomResId(),
                    getClickPendingIntent(context,getRoomResId(),"btn.start.com",javaClass))
            rv.setOnClickPendingIntent(getTitleResId(),
                    getClickPendingIntent(context,getTitleResId(),"btn.text.com",javaClass))
        }


        //设置用户自定义定义配置

        try {//这个tryCatch防止用户输入的颜色有误,parseColor报错
            //标题（时间）
            val config = TransConfig.getUserConfig(context)
            rv.setTextColor(getTitleResId(), Color.parseColor(config.timeTextColor))
            rv.setTextViewTextSize(getTitleResId(), TypedValue.COMPLEX_UNIT_SP, config.timeTextSize.toFloat())

            //课程名
            rv.setTextColor(getCourseNameResId(), Color.parseColor(config.courseTextColor))
            rv.setTextViewTextSize(getCourseNameResId(), TypedValue.COMPLEX_UNIT_SP, config.courseTextSize.toFloat())

            //教室
            rv.setTextColor(getRoomResId(), Color.parseColor(config.roomTextColor))
            rv.setTextViewTextSize(getRoomResId(), TypedValue.COMPLEX_UNIT_SP, config.roomTextSize.toFloat())

            rv.setViewVisibility(R.id.widget_little_trans_holder, View.VISIBLE)

            //装饰，用ARGB_8888是为了设置透明度
            val holderBm = Bitmap.createBitmap(300, 1, Bitmap.Config.ARGB_8888)
            holderBm.eraseColor(Color.parseColor(config.holderColor))
            rv.setImageViewBitmap(R.id.widget_little_trans_holder, holderBm)

            //这个方法不能运行，上面的代码性能不行，来个人优化下？
//        rv.setInt(R.id.widget_little_trans_holder,"setBackgroundColor",Color.parseColor(config.holderColor))

            if (config.holderColor.length == 9) {
                if (config.holderColor.subSequence(1, 3) == "00") {
                    rv.setViewVisibility(R.id.widget_little_trans_holder, View.GONE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rv
    }
}