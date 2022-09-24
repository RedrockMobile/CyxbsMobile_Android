package com.mredrock.cyxbs.widget.widget.page.oversized

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.widget.repo.bean.Affair
import com.mredrock.cyxbs.widget.repo.bean.Lesson
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase
import com.mredrock.cyxbs.widget.util.SchoolCalendar
import com.mredrock.cyxbs.widget.util.defaultSp
import com.mredrock.cyxbs.widget.widget.oversize.OversizedAppWidget
import kotlin.concurrent.thread

class OversizedAppWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setResult(RESULT_CANCELED)
        val context = this@OversizedAppWidgetConfigureActivity
        val appWidgetManager = AppWidgetManager.getInstance(context)
        OversizedAppWidget.updateAppWidget(context, appWidgetManager, appWidgetId)
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        thread {
            LessonDatabase.getInstance(context).getLessonDao().insertLesson(Lesson(week = SchoolCalendar().weekOfTerm, beginLesson = 1, hashDay = 1, course = "lesson", period = 2))
            AffairDatabase.getInstance(context).getAffairDao().insertAffair(Affair(week = SchoolCalendar().weekOfTerm, beginLesson = 1, day = 1, title = "affair", period = 4))
        }
        finish()
    }

}

private const val PREF_PREFIX_KEY = "appwidget_"
internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = defaultSp.edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}