package com.mredrock.cyxbs.discover.schoolcar.widget

import android.app.Activity
import android.graphics.BitmapFactory
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.LOST_SERVICES
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.NO_GPS
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.TIME_OUT
import com.mredrock.cyxbs.schoolcar.R
import kotlinx.android.synthetic.main.schoolcar_notserve_dialog.view.*

/**
 * Created by glossimar on 2018/9/12
 */

object ExploreSchoolCarDialog {

    private var dialog: AlertDialog? = null

    fun show(activity: Activity, type: Int) {

        val inflater = activity.layoutInflater
        val layout = inflater.inflate(R.layout.schoolcar_notserve_dialog, null) as RelativeLayout

        dialog = AlertDialog.Builder(activity).setCancelable(true).setView(layout).create()
        layout.school_car_dialog_dismiss_button.setOnClickListener {
            if (it.id == R.id.school_car_dialog_dismiss_button) {
                dialog!!.dismiss()
            }
        }

//            try {
//                val manager = activity.windowManager
//                val width = manager.defaultDisplay.width
//                val height = manager.defaultDisplay.height
//
//                val dialogWindow = dialog!!.window
//                dialog!!.show()
//                dialogWindow!!.setLayout(width * 4 / 5, height * 7 / 11)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        try {
            when (type) {
                LOST_SERVICES -> {
                    setLayout(activity, R.drawable.dialog_school_car_not_serve)
                    dialog!!.window!!.setBackgroundDrawableResource(R.drawable.dialog_school_car_not_serve)
                    layout.school_car_dialog_dismiss_button.setOnClickListener {
                        dialog?.dismiss()
                        activity.finish()
                    }
                }
                TIME_OUT -> {
                    setLayout(activity, R.drawable.ic_school_car_search_time_out)
                    dialog!!.window!!.setBackgroundDrawableResource(R.drawable.ic_school_car_search_time_out)
                }
                NO_GPS -> {
                    setLayout(activity, R.drawable.ic_school_car_search_no_gps)
                    dialog?.window?.setBackgroundDrawableResource(R.drawable.ic_school_car_search_no_gps)
                    layout.school_car_dialog_negative_button.setOnClickListener { dialog!!.cancel() }
                    layout.school_car_dialog_positive_button.setOnClickListener { dialog!!.cancel() }
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //防止对话框拉伸，直接获取drawable对象的宽高之后进行加载
    private fun setLayout(activity: Activity, drawableId: Int) {
        val op = BitmapFactory.Options()
        op.inJustDecodeBounds = true
        BitmapFactory.decodeResource(activity.resources, drawableId, op)
        val dm = activity.resources.displayMetrics
        val width = dm.widthPixels * 4 / 5
        val multiple = op.outHeight.toFloat() / op.outWidth
        val height = width * multiple
        val dialogWindow = dialog!!.window
        dialog!!.show()
        dialogWindow!!.setLayout(width, height.toInt())
    }

    fun cancelDialog() {
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dialog = null
        }
    }

}