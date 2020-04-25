package com.mredrock.cyxbs.discover.calendar

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.calendar.R
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_CALENDAR
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.calendar_activity_main.*

@Route(path = DISCOVER_CALENDAR)
class CalendarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        val request = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
        Glide.with(this)
                .load("$END_POINT_REDROCK/234/newapi/schoolCalendar")
                .apply(request)
                .into(object : DrawableImageViewTarget(iv_calendar) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        calendar_progress_bar.visibility = View.GONE
                        val width: Int = resource.intrinsicWidth
                        val height: Int = resource.intrinsicHeight
                        var ivWidth: Int = iv_calendar.width
                        if (ivWidth == 0) {
                            ivWidth = iv_calendar.resources.displayMetrics.widthPixels
                        }
                        val ivHeight = (height / width * ivWidth)
                        val lp: ViewGroup.LayoutParams = iv_calendar.layoutParams
                        lp.height = ivHeight
                        iv_calendar.layoutParams = lp
                        val bitmap = Bitmap.createBitmap((resource as BitmapDrawable).bitmap)
                        if (BaseApp.isNightMode) {
                            convertColor(bitmap, resource)
                        }
                        iv_calendar.setImageBitmap(bitmap)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        calendar_progress_bar.visibility = View.GONE
                        calendar_image_view.visibility = View.VISIBLE
                        calendar_text_view_tip.visibility = View.VISIBLE
                    }
                })
    }


    private fun convertColor(bitmap: Bitmap, resource: Drawable) {
        val canvas = Canvas(bitmap)
        val colorMatrix = ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f))
        colorMatrix.preConcat(ColorMatrix(floatArrayOf(
                0.6333f, 0.0333f, 0.3333f, 0f, 0f,
                0.6333f, 0.0333f, 0.3333f, 0f, 0f,
                0.6333f, 0.0333f, 0.3333f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f)))
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(colorMatrix) }
        canvas.drawBitmap((resource as BitmapDrawable).bitmap, 0f, 0f, paint)
    }


    override val isFragmentActivity = false
}