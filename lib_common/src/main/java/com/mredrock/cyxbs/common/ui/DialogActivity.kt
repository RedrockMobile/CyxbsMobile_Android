package com.mredrock.cyxbs.common.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.config.MAIN_REQUEST_LOGIN_DIALOG
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.main.MAIN_LOGIN

@Route(path = MAIN_REQUEST_LOGIN_DIALOG)
class DialogActivity : BaseActivity() {

    companion object {
        private var mDialog: MaterialDialog? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        window.setWindowAnimations(0)
        val functionName = intent.getStringExtra("functionName") ?: "此功能"

//        mDialog = MaterialDialog(this).show {
//            title(text = "是否登陆")
//            message(text = "请先登录才能使用${functionName}哦~")
//            positiveButton(text = "马上去登录") {
//                if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
//                    ARouter.getInstance().build(MAIN_LOGIN).navigation()
//                }
//            }
//            negativeButton(text = "我再看看") { dismiss() }
//            cancelOnTouchOutside(false)
//            setOnDismissListener {
//                mDialog = null
//                finish()
//            }
//            cornerRadius(res = R.dimen.common_corner_radius)
//        }
    }
}