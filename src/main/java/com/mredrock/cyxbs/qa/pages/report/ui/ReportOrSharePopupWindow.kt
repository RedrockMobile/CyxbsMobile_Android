package com.mredrock.cyxbs.qa.pages.report.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.android.synthetic.main.qa_popup_window_report_or_share.view.*

/**
 * Created By jay68 on 2018/12/9.
 */
class ReportOrSharePopupWindow(activity: Activity,
                               private val question: Question,
                               private val anchor: View,
                               private val frame: View) : PopupWindow(activity) {
    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        contentView = LayoutInflater.from(activity).inflate(R.layout.qa_popup_window_report_or_share, null, false)
        contentView.tv_report.setOnClickListener {
            ReportActivity.activityStart(activity, question.id)
            dismiss()
        }
        contentView.tv_share.setOnClickListener {
            // 分享 url
            ShareAction(activity)
                    .withMedia(UMWeb("$END_POINT_REDROCK/app/index.php/QA/Question/share?id%3D${question.id}").apply {
                        title = question.title
                        description = question.description
                        if (!question.photoUrl.isNullOrEmpty()) {
                            this.setThumb(UMImage(activity, question.photoUrl[0]))
                        }
                    }).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
                    .open()
        }
        animationStyle = R.style.PopupAnimation
        isTouchable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setOnDismissListener { frame.gone() }
    }

    fun show() {
        frame.visible()
        showAtLocation(anchor, Gravity.END or Gravity.TOP, 0, anchor.height)
    }
}