package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.MINE_FORGET_PASSWORD
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.ForgetPasswordViewModel
import com.mredrock.cyxbs.mine.util.ui.ChooseFindTypeDialog
import com.mredrock.cyxbs.mine.util.ui.DefaultPasswordHintDialog
import kotlinx.android.synthetic.main.mine_activity_forget_password.*

/**
 * Author: SpreadWater
 * Time: 2020-10-29 15:06
 * describe: 在登陆界面点击忘记密码跳转到的界面，
 * 执行输入学号检测是否是原始密码的功能，
 * 剩余的找回密码的逻辑由FindPasswordActivity执行
 */
@Route(path = MINE_FORGET_PASSWORD)
class ForgetPasswordActivity : BaseViewModelActivity<ForgetPasswordViewModel>() {
    private var stuNumber = ""
    private var canClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_forget_password)
        mine_pb_security_forget.visibility = View.GONE
        //配置toolBar
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@ForgetPasswordActivity, R.color.common_white_background))
            initWithSplitLine("忘记密码",
                    false,
                    R.drawable.mine_ic_arrow_left,
                    View.OnClickListener {
                        finishAfterTransition()
                    })
            setTitleLocationAtLeft(true)
        }
        //监听是否为默认密码
        viewModel.defaultPassword.observe(this, Observer {
            canClick = true//允许进行点击事件
            if (it) {
                //展示为默认密码的dialog
                DefaultPasswordHintDialog.show(this, this)
            } else {
                viewModel.checkBinding(stuNumber) {
                    mine_pb_security_forget.visibility = View.GONE
                    //展示不同的找回密码方式的dialog
                    viewModel.bindingEmail.value?.let { bindEmailValue ->
                        viewModel.bindingPasswordProtect.value?.let { bindingPasswordValue ->
                            ChooseFindTypeDialog.showDialog(this, bindEmailValue, bindingPasswordValue, this, true, stuNumber)
                        }
                    }
                }
            }
        })
        mine_security_bt_forget_password_confirm.setOnSingleClickListener {
            if (canClick) {
                stuNumber = mine_security_et_forget_password.text.toString()
                if (stuNumber != "" && stuNumber != null) {
                    mine_pb_security_forget.visibility = View.VISIBLE
                    viewModel.checkDefaultPassword(stuNumber) {
                        mine_pb_security_forget.visibility = View.GONE
                    }
                    canClick = false//网络请求结束之前不允许进行新的请求
                }
            }
        }
    }
}