package com.mredrock.cyxbs.widget.widget.page.oversized

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.widget.util.defaultSp
import com.mredrock.cyxbs.widget.widget.oversize.OversizedAppWidget

class OversizedAppWidgetConfigureActivity : Activity() {

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setResult(RESULT_CANCELED)
        val context = this@OversizedAppWidgetConfigureActivity
        val appWidgetManager = AppWidgetManager.getInstance(context)
        OversizedAppWidget.updateAppWidget(context, appWidgetManager, intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID))
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID))
        setResult(RESULT_OK, resultValue)
        finish()
    }

}

private const val PREF_PREFIX_KEY = "appwidget_"
internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = defaultSp.edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}