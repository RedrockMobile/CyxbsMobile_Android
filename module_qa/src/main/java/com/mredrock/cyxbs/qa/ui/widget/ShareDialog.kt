package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment.DynamicFragment
import kotlinx.android.synthetic.main.qa_dialog_share.*

/**
 * @Author: xgl
 * @ClassName: ShareDialog
 * @Description:
 * @Date: 2020/12/19 13:10
 */
class ShareDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_dialog_share)
        window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            val lp: WindowManager.LayoutParams = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.windowAnimations = R.style.BottomInAndOutStyle
            lp.gravity = Gravity.BOTTOM
            attributes = lp
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun initView(
            qqshare: View.OnClickListener,
            qqZoneShare: View.OnClickListener,
            weChatShare: View.OnClickListener,
            friendShipCircle: View.OnClickListener,
            copylink: View.OnClickListener,
            onCancelListener: View.OnClickListener
    ) {
        qa_ll_share_qq.setOnClickListener(qqshare)
        qa_ll_share_qq_zone.setOnClickListener(qqZoneShare)
        qa_ll_share_wechat.setOnClickListener(weChatShare)
        qa_ll_share_friend_circle.setOnClickListener(friendShipCircle)
        qa_ll_share_copy_link.setOnClickListener(copylink)
        qa_tv_cancel.setOnClickListener(onCancelListener)
    }
}