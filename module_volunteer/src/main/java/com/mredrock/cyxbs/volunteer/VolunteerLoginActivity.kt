package com.mredrock.cyxbs.volunteer

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.iterator
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerLoginViewModel
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus

@Route(path = DISCOVER_VOLUNTEER)
class VolunteerLoginActivity : BaseViewModelActivity<VolunteerLoginViewModel>() {
    companion object {
        const val BIND_SUCCESS: Int = 0
        const val FAILED: Int = -1
        const val INVALID_ACCOUNT: Int = -2
        const val WRONG_PASSWORD: Int = 3
    }

    private var uid: String = ""
    private var account: String = ""
    private var password: String = ""

    //判断是否需要存入sp
    private var userInfoChanged = false
    private lateinit var volunteerSP: VolunteerTimeSP

    override val isFragmentActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initObserve()
        volunteerSP = VolunteerTimeSP(this)
        val user = ServiceManager.getService(IAccountService::class.java).getUserService()
        uid = user.getStuNum()
        if (volunteerSP.isBind()) {
            displayLottie()
            account = volunteerSP.volunteerAccount
            password = volunteerSP.volunteerPassword
            uid = volunteerSP.volunteerUid
            viewModel.login(volunteerSP.volunteerAccount, EncryptPassword.encrypt(volunteerSP.volunteerPassword), volunteerSP.volunteerUid) {
                CyxbsToast.makeText(this, "服务暂时不可使用~", Toast.LENGTH_SHORT).show()
                stopLottie()
            }
        }
        et_volunteer_account.setText("unbelievable3")
        et_volunteer_password.setText("xE3L3RG7.Wugaad")
        btn_volunteer_login.setOnClickListener {
            loginAction()
        }
        useSoftKeyboard()
    }

    private fun initUserInfo() {
        account = et_volunteer_account.text.toString()
        password = et_volunteer_password.text.toString()
        userInfoChanged = true
    }

    private fun useSoftKeyboard() {
        et_volunteer_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loginAction()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethodManager.isActive) {
                    inputMethodManager.hideSoftInputFromWindow(this@VolunteerLoginActivity.currentFocus?.windowToken
                            ?: return@setOnEditorActionListener false, 0)
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }


    private fun loginAction() {
        if (et_volunteer_account.text.toString().isEmpty() || et_volunteer_password.text.toString().isEmpty()) {
            CyxbsToast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        displayLottie()
        initUserInfo()
        viewModel.login(account, EncryptPassword.encrypt(password), uid) {
            CyxbsToast.makeText(this, "服务暂时不可使用~", Toast.LENGTH_SHORT).show()
            stopLottie()
        }
    }


    private fun failedAction(text: String) {
        stopLottie()
        CyxbsToast.makeText(this, text, Toast.LENGTH_SHORT).show()
        userInfoChanged = false
    }

    private fun initObserve() {
        viewModel.loginCode.observe {
            it ?: return@observe
            when (it) {
                VolunteerLoginActivity.BIND_SUCCESS -> {
                    if (userInfoChanged) {
                        volunteerSP.bindVolunteerInfo(account, password, uid)
                    }
                }

                INVALID_ACCOUNT, WRONG_PASSWORD -> failedAction("亲，输入的账号或密码有误哦")


                FAILED -> failedAction("亲，登录失败哦")

            }
        }
        viewModel.volunteerTime.observe {
            it ?: return@observe
            VolunteerRecordActivity.startActivity(this)
            EventBus.getDefault().postSticky(VolunteerLoginEvent(it))
            finish()
        }
    }

    private fun displayLottie() {
        for (i in fl_root) {
            if (i.id != R.id.lav_login) {
                i.invisible()
            } else {
                i.visible()
            }
        }
    }

    private fun stopLottie() {
        for (i in fl_root) {
            if (i.id != R.id.lav_login) {
                i.visible()
            } else {
                i.gone()
            }
        }
    }

    override val viewModelClass: Class<VolunteerLoginViewModel>
        get() = VolunteerLoginViewModel::class.java

}
