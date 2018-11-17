package com.mredrock.cyxbs.volunteer

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.volunteer.Network.ApiService
import com.mredrock.cyxbs.volunteer.bean.VolunteerLogin
import kotlinx.android.synthetic.main.activity_login.*
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import android.os.Build
import android.support.annotation.RequiresApi
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.volunteer.Network.VolunteerRetrofit
import com.mredrock.cyxbs.volunteer.bean.PasswordEncode
import java.util.Collections.replaceAll
import java.util.regex.Pattern


class VolunteerLoginActivity : BaseActivity() {
    companion object {
        private const val BIND_SUCCESS: Int = 0
        private const val INVALID_ACCOUNT: Int = -2
        private const val WRONG_PASSWORD: Int = 3
    }

    private var uid: String? = null
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var volunteerSP: VolunteerTimeSP
    private lateinit var dialog: ProgressDialog

    private var publicKey: String = "-----BEGIN PUBLIC KEY-----\nMFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAN9k0wVGURdzhidVsxzwQrwinXAp88gA\nlbSn6UJqAVHeGhy68AvIUvmp1rLZphZxbl+nzcsaatiI3TOXBMQGwJcCAwEAAQ==\n-----END PUBLIC KEY-----^^^"
    override val isFragmentActivity: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initToolbar()
        initOnClickListener()
        useSoftKeyboard()
        initUserInfo()
    }

    private fun initToolbar() {
        if (volunteer_toolbar != null) {
            volunteer_toolbar.title = ""
            setSupportActionBar(volunteer_toolbar)
        }
    }

    private fun initOnClickListener(){
        volunteer_login.setOnClickListener { view: View? ->
            showProgressDialog()
            initUserInfo()
            if (view!!.id == R.id.volunteer_login) passwordEncoding(password)
        }

        volunteer_login_back.setOnClickListener { finish() }
    }

    private fun initUserInfo() {
        account = volunteer_account.text.toString()
        password = volunteer_password.text.toString()

        volunteerSP = VolunteerTimeSP(this)
        if (BaseApp.user != null) {
            uid = BaseApp.user!!.stuNum!!
        } else {
            val regEx = "[^0-9]"
            val p = Pattern.compile(regEx)
            val m = p.matcher(account)
            uid = m.replaceAll("").trim()
            LogUtils.d(TAG, uid!!)
        }

        if ((!(volunteerSP.volunteerUid == "404"
                        || volunteerSP.volunteerUid == "null"
                        || volunteerSP.volunteerUid == ""))) {

            val intent = Intent(this, VolunteerRecordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun useSoftKeyboard() {
        volunteer_password.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                showProgressDialog()
                initUserInfo()
                passwordEncoding(password)
                handled = true

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethodManager.isActive) {
                    inputMethodManager.hideSoftInputFromWindow(this@VolunteerLoginActivity.currentFocus.windowToken, 0)
                }
            }
            handled
        }
    }

    private fun passwordEncoding(password: String) {
        var apiService = ApiGenerator.getApiService(VolunteerRetrofit.encodingRetrofit, ApiService::class.java)
        apiService.getRsaEncode(publicKey + password, "rsapubkey", "pad=1_s=gb2312_t=0")
                .setSchedulers()
                .safeSubscribeBy { passwordEncode: PasswordEncode ->
                    var encodePassword = passwordEncode.data[0]
                    login(account, encodePassword)
                }
    }

    private fun login(account: String, encodingPassword : String) {
        if (uid == null) {

        }
        var apiService = ApiGenerator.getApiService(VolunteerRetrofit.volunteerRetrofit, ApiService::class.java)
        apiService.volunteerLogin("Basic enNjeTpyZWRyb2Nrenk=", account, encodingPassword, uid!!)
                .setSchedulers()
                .safeSubscribeBy { volunteerLogin: VolunteerLogin ->
                    when ((volunteerLogin.code)!!.toInt()) {
                        BIND_SUCCESS -> {
                            volunteerSP.bindVolunteerInfo(account, password, uid!!)
                            val intent = Intent(this@VolunteerLoginActivity, VolunteerRecordActivity::class.java)
                            startActivity(intent)
                            dialog.dismiss()
                            finish()
                        }

                        INVALID_ACCOUNT -> showUnsuccessDialog("亲，输入的账号不存在哦")

                        WRONG_PASSWORD -> showUnsuccessDialog("亲，输入的账号或密码有误哦")
                    }
                }
    }

    private fun showProgressDialog() {
        dialog = ProgressDialog(this@VolunteerLoginActivity)
        dialog.setMessage("登录中...")
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showUnsuccessDialog(text: String) {
        runOnUiThread {
            dialog.dismiss()
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                MaterialDialog.Builder(this@VolunteerLoginActivity)
                        .title("登录失败")
                        .content(text)
                        .positiveText("我知道啦")
                        .callback(object : MaterialDialog.ButtonCallback() {
                            override fun onPositive(dialog: MaterialDialog?) {
                                super.onPositive(dialog)
                                //                                accountView.setText("");
                                volunteer_password.setText("")
                            }
                        }).show()
            }
        }
    }
}
