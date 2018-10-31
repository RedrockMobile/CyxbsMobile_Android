package com.mredrock.cyxbs.discover.schoolcar.widget

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.widget.RelativeLayout
import com.mredrock.cyxbs.schoolcar.R
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.LOST_SERVICES
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.NO_GPS
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.TIME_OUT
import kotlinx.android.synthetic.main.explore_schoolcar_notserve_dialog.view.*

/**
 * Created by glossimar on 2018/9/12
 */

class  ExploreSchoolCarDialog(){

    companion object {
        private var dialog: AlertDialog? = null

        fun show(activity: Activity, type: Int){

            val inflater = activity.layoutInflater
            val layout = inflater.inflate(R.layout.explore_schoolcar_notserve_dialog, null) as RelativeLayout

            dialog = AlertDialog.Builder(activity).
                    setCancelable(true).
                    setView(layout).
                    create()
            layout.school_car_dialog_dismiss_button.setOnClickListener {
                if (it.getId() == R.id.school_car_dialog_dismiss_button) {
                    dialog!!.dismiss()
                }
            }

            try {
                val manager = activity.windowManager
                val width = manager.defaultDisplay.width
                val height = manager.defaultDisplay.height

                val dialogWindow = dialog!!.window
                dialog!!.show()
                dialogWindow!!.setLayout(width * 4 / 5, height * 7 / 11)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                when (type) {
                    LOST_SERVICES -> dialog!!.window!!.setBackgroundDrawableResource(R.drawable.dialog_school_car_not_serve)
                    TIME_OUT -> dialog!!.window!!.setBackgroundDrawableResource(R.drawable.ic_school_car_search_time_out)
                    NO_GPS -> {
                        dialog!!.window.setBackgroundDrawableResource(R.drawable.ic_school_car_search_no_gps)
                        layout.school_car_dialog_negative_button.setOnClickListener { dialog!!.cancel() }
                        layout.school_car_dialog_positive_button.setOnClickListener { dialog!!.cancel()  }
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cancleDialog(){
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
}