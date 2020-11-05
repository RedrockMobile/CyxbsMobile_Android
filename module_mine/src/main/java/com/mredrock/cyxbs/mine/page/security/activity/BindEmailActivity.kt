package com.mredrock.cyxbs.mine.page.security.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.BindEmailViewModel
import kotlinx.android.synthetic.main.mine_activity_bind_email.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class BindEmailActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(BindEmailViewModel::class.java) }

    override val isFragmentActivity = false

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

        iv_bind_email_back.setOnSingleClickListener {
            finish()
        }

        tv_bind_email_contact_us.setOnSingleClickListener {
            onFeedBackClick()
        }

        tv_bind_email_send_code.setOnSingleClickListener {
            sendCode()
        }

        btn_bind_email_next.setOnSingleClickListener {
            if (btn_bind_email_next.text == getString(R.string.mine_security_bind_email_next)) {
                sendCode()
            } else if (btn_bind_email_next.text == getString(R.string.mine_security_bind_email_yes)) {
                if (et_bind_email.text.toString() == "") {
                    CyxbsToast.makeText(this, getString(R.string.mine_security_bind_email_please_input_code), Toast.LENGTH_SHORT).show()
                } else if (et_bind_email.text.toString() == viewModel.code.toString() &&
                        System.currentTimeMillis() / 1000 <= viewModel.expireTime) {
                    viewModel.confirmCode(email, viewModel.code.toString())
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
            viewModel.getCode(email)
            val result: Toast = CyxbsToast.makeText(this, getString(R.string.mine_security_bind_email_sent_to_your_email), Toast.LENGTH_SHORT)
            result.show()
            val atLocation = showUserEmail.indexOf("@")
            if (atLocation in 2..4) {
                showUserEmail = showUserEmail.substring(0, 1) + "*" + showUserEmail.substring(2, showUserEmail.length)
            } else if (atLocation == 5) {
                showUserEmail = showUserEmail.substring(0, 2) + "**" + showUserEmail.substring(4, showUserEmail.length)
            } else if (atLocation > 5) {
                var starString = ""
                for (i in 0 until atLocation - 4) starString += "*"
                showUserEmail = showUserEmail.substring(0, 2) + starString + showUserEmail.substring(atLocation - 2, showUserEmail.length)
            }
            tv_bind_email_top_tips.text = "掌邮向你的邮箱${showUserEmail}发送了验证码"
            tv_bind_email_top_tips.setTextColor(Color.parseColor("#15315B"))
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
                    tv_bind_email_send_code.text = getString(R.string.mine_security_bind_email_resend)
                }
            }.start()
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

    /****************
     *
     * 发起添加群流程。群号：掌上重邮反馈群(570919844) 的 key 为： DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf
     * 调用 joinQQGroup(DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf) 即可发起手Q客户端申请加群 掌上重邮反馈群(570919844)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }

    private fun onFeedBackClick() {
        if (!joinQQGroup("DXvamN9Ox1Kthaab1N_0w7s5N3aUYVIf")) {
            val clipboard = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val data = ClipData.newPlainText("QQ Group", "570919844")
            clipboard.primaryClip = data
            CyxbsToast.makeText(this, "抱歉，由于您未安装手机QQ或版本不支持，无法跳转至掌邮bug反馈群。" + "已将群号复制至您的手机剪贴板，请您手动添加", Toast.LENGTH_SHORT).show()
        }
    }
}