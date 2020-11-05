package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.ChangePasswordActivity.Companion.TYPE_START_FROM_MINE
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.ui.DoubleChooseDialog
import kotlinx.android.synthetic.main.mine_activity_security.*
/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 */
class SecurityActivity : BaseActivity() {

    override val isFragmentActivity = false
    private var isBindingEmail = false
    private var isSetProtect = false
    //在网络请求返回绑定结果之前不允许进行点击
    private var canClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_security)
        setSupportActionBar(findViewById(R.id.toolbar))
        //首先判断用户的邮箱绑定情况
        LogUtils.d("SecurityActivity" , ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum())
        apiService.checkBinding(
                "2019210432"
        ).safeSubscribeBy(
                onError = {
                    BaseApp.context.toast("对不起，获取是否绑定邮箱和密保失败")
                },
                onNext = {
                    val bindingResponse = it.data
                    isBindingEmail = bindingResponse.email_is != 0
                    isSetProtect = bindingResponse.question_is != 0
                    canClick = true
                }
        )

        if (canClick){
            mine_ll_change_binding_mail.setOnClickListener {//修改绑定邮箱
                if (isBindingEmail){
                    BaseApp.context.toast("您已经绑定过邮箱了")
                } else {
                    val intent = Intent(this , BindEmailActivity::class.java)
                    startActivity(intent)
                }
            }

            mine_ll_change_secret_security.setOnClickListener {//修改密保
                if (isSetProtect){
                    BaseApp.context.toast("您已经设置过密保了")
                } else {
                    val intent = Intent(this , SetPasswordProtectActivity::class.java)
                    startActivity(intent)
                }
            }

            mine_ll_change_password.setOnClickListener {//修改密码
                if (!isBindingEmail&&!isSetProtect){
                    DoubleChooseDialog.show(this)
                } else {
                    ChangePasswordActivity.actionStart(this , TYPE_START_FROM_MINE)
                }
            }

            mine_iv_security_back.setOnClickListener {//退回界面
                finish()
            }
        }
    }
}