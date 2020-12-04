package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.MINE_BIND_EMAIL
import com.mredrock.cyxbs.common.config.MINE_FORGET_PASSWORD
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.util.Jump2QQHelper
import com.mredrock.cyxbs.mine.page.security.viewmodel.BindEmailViewModel
import kotlinx.android.synthetic.main.mine_activity_bind_email.*
import java.util.regex.Matcher
import java.util.regex.Pattern
@Route(path = MINE_BIND_EMAIL)
class BindEmailActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(BindEmailViewModel::class.java) }
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_bind_email)

        viewModel.mldConfirmIsSucceed.observe(this, Observer<Boolean> {
            if (it) {
                CyxbsToast.makeText(this, getString(R.string.mine_security_bind_email_bind_succeed), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                CyxbsToast.makeText(this, getString(R.string.mine_security_bind_email_bind_failed), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.mldCode.observe(this, Observer<Int> {
            btn_bind_email_next.isEnabled = true
        })

        common_toolbar.apply {
            initWithSplitLine("绑定邮箱")
        }

        tv_bind_email_contact_us.setOnSingleClickListener {
            Jump2QQHelper.onFeedBackClick(this)
        }

        tv_bind_email_send_code.setOnSingleClickListener {
            sendCode()
        }

        btn_bind_email_next.setOnSingleClickListener {
            if (btn_bind_email_next.text == getString(R.string.mine_security_bind_email_next)) {
                sendCode()
            } else if (btn_bind_email_next.text == getString(R.string.mine_security_confirm)) {
                if (et_bind_email.text.toString() == "") {
                    CyxbsToast.makeText(this, getString(R.string.mine_security_please_type_new_words), Toast.LENGTH_SHORT).show()
                } else if (System.currentTimeMillis() / 1000 <= viewModel.expireTime) {
                    viewModel.confirmCode(email, et_bind_email.text.toString())
                } else {
                    CyxbsToast.makeText(this, getString(R.string.mine_security_bind_email_code_expire), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendCode() {
        val userEmail: String = et_bind_email.text.toString()
        var showUserEmail = userEmail
        if (isEmail(userEmail) || email != "") {
            if (email == "") email = userEmail
            else showUserEmail = email
            viewModel.getCode(email) {
                BaseApp.context.toast("已向你的邮箱发送了一条验证码")
                val atLocation = showUserEmail.indexOf("@")
                when {
                    atLocation in 2..4 -> {
                        showUserEmail = showUserEmail.substring(0, 1) + "*" + showUserEmail.substring(2, showUserEmail.length)
                    }
                    atLocation == 5 -> {
                        showUserEmail = showUserEmail.substring(0, 2) + "**" + showUserEmail.substring(4, showUserEmail.length)
                    }
                    atLocation > 5 -> {
                        var starString = ""
                        for (i in 0 until atLocation - 4) starString += "*"
                        showUserEmail = showUserEmail.substring(0, 2) + starString + showUserEmail.substring(atLocation - 2, showUserEmail.length)
                    }
                }
                tv_bind_email_top_tips.text = "掌邮向你的邮箱${showUserEmail}发送了验证码"
                btn_bind_email_next.text = "确定"
                et_bind_email.setText("")
                tv_bind_email_send_code.visible()
                tv_bind_email_tips.gone()
                tv_bind_email_send_code.isEnabled = false
                btn_bind_email_next.isEnabled = false
                Thread {
                    var timeSecond = 60
                    while (timeSecond > 0) {
                        runOnUiThread {
                            tv_bind_email_send_code.text = "正在发送(${timeSecond})"
                        }
                        timeSecond--
                        Thread.sleep(1000)
                    }
                    runOnUiThread {
                        tv_bind_email_send_code.isEnabled = true
                        tv_bind_email_send_code.text = getString(R.string.mine_security_resend)
                    }
                }.start()
            }
        } else {
            tv_bind_email_tips.visible()
        }
    }

    private fun isEmail(strEmail: String): Boolean {
        val strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$"
        val p: Pattern = Pattern.compile(strPattern)
        val m: Matcher = p.matcher(strEmail)
        return m.matches()
    }
}