package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_security.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 */
class SecurityActivity : BaseActivity(){

    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_security)

        mine_ll_change_binding_mail.setOnClickListener {//修改绑定邮箱
            val intent = Intent(this , BindEmailActivity::class.java)
            startActivity(intent)
        }

        mine_ll_change_password.setOnClickListener {//修改密码
            val intent = Intent(this , ChangPasswordActivity::class.java)
            startActivity(intent)
        }

        mine_ll_change_secret_security.setOnClickListener {//修改密保
            val intent = Intent(this , SetPasswordProtectActivity::class.java)
            startActivity(intent)
        }

        mine_iv_security_back.setOnClickListener {//退回界面
            //TODO：没有细看产品设计，也许有其他的跳转需求？
            finish()
        }
    }
}