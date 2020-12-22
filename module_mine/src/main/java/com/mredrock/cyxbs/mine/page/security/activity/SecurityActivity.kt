package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.ChangePasswordActivity.Companion.TYPE_START_FROM_MINE
import com.mredrock.cyxbs.mine.page.security.viewmodel.SecurityActivityViewModel
import com.mredrock.cyxbs.mine.util.ui.DoubleChooseDialog
import kotlinx.android.synthetic.main.mine_activity_security.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 */
class SecurityActivity : BaseViewModelActivity<SecurityActivityViewModel>() {

    //在网络请求返回绑定结果之前不允许进行点击

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_security)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel.checkBinding {
            if (!viewModel.netRequestSuccess)
                BaseApp.context.toast("绑定信息请求失败")
        }
        mine_ll_change_binding_mail.setOnSingleClickListener {//绑定邮箱
            if (viewModel.canClick) {
                val intent = Intent(this, BindEmailActivity::class.java)
                startActivity(intent)
            } else {
                showRequestResult()
            }
        }

        mine_ll_change_secret_security.setOnSingleClickListener {//密保
            if (viewModel.canClick) {
                SetPasswordProtectActivity.actionStart(this)
            } else {
                showRequestResult()
            }
        }

        mine_ll_change_password.setOnSingleClickListener {//修改密码
            if (viewModel.canClick) {
                if (!viewModel.isBindingEmail && !viewModel.isSetProtect) {
                    DoubleChooseDialog.show(this)
                } else {
                    ChangePasswordActivity.actionStart(this, TYPE_START_FROM_MINE)
                }
            } else {
                showRequestResult()
            }
        }

        mine_iv_security_back.setOnSingleClickListener {//退回界面
            finish()
        }
    }

    private fun showRequestResult() {
        if (viewModel.netRequestSuccess)
            BaseApp.context.toast("正在请求是否绑定信息，请稍候")
        else
            BaseApp.context.toast("绑定信息请求失败，无法使用此功能")
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkBinding { }//刷新数据
    }
}