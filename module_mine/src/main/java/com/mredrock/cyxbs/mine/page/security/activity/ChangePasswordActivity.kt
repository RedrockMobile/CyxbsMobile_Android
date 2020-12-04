package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.ChangePasswordViewModel
import com.mredrock.cyxbs.mine.util.ui.ChooseFindTypeDialog
import kotlinx.android.synthetic.main.mine_activity_change_password.*

/**
 * Author: SpreadWater
 * Time: 2020-10-29 15:06
 */
class ChangePasswordActivity : BaseViewModelActivity<ChangePasswordViewModel>() {

    companion object {
        //下面的常量是转化页面形态的标识量
        const val TYPE_OLD_PASSWORDS = 0 //仅一行的输入旧密码模式
        const val TYPE_NEW_PASSWORD = 1 //两行的输入新密码模式
        const val TYPE_COLOR_LIGHT_BUTTON = 2//没输入满时按钮颜色
        const val TYPE_COLOR_NIGHT_BUTTON = 3//深色按钮
        const val TYPE_START_FROM_MINE = 4//从个人页面跳转到修改密码页面
        const val TYPE_START_FROM_OTHERS = 5//从密保和邮箱界面跳转
        const val TYPE_FORM_LOGIN_ACTIVITY_WITH_STU_NUMBER = 6//从登陆界面跳转过来，并且传递了学号进来
        const val INPUT_NEW_PASSWORD_FORMAT_IS_CORRECT = 10002
        fun actionStart(context: Context, startType: Int) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            intent.putExtra("startType", startType)
            context.startActivity(intent)
        }

        fun startFormLogin(context: Context, stuNumber: String, code: Int) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            intent.putExtra("startType", TYPE_FORM_LOGIN_ACTIVITY_WITH_STU_NUMBER)
            intent.putExtra("stuNumber", stuNumber)
            intent.putExtra("code", code)
            context.startActivity(intent)
        }
    }

    private var isLine2ShowPassword = false//是否显示密码

    private var isLine1ShowPassword = false//是否显示密码

    var isClick = false//按钮是否能点击

    var isOriginView = true//是否是在第一个界面

    private var originPassword = ""//用来保存旧密码

    private var stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()//获取当前用户的学号

    private var isFromLogin = false//是否来自登陆界面

    private var code = -1//验证用的随机数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getIntExtra("startType", 4)
        initView(type)
    }

    private fun initView(type: Int) {
        when (type) {
            TYPE_START_FROM_MINE -> {
                setContentView(R.layout.mine_activity_change_password)
                setToolBar(TYPE_OLD_PASSWORDS)
                changePageType(TYPE_OLD_PASSWORDS)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                initEvent()
            }
            TYPE_START_FROM_OTHERS -> {
                isOriginView = false
                setContentView(R.layout.mine_activity_change_password)
                changePageType(TYPE_NEW_PASSWORD)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                setToolBar(TYPE_NEW_PASSWORD)
                LogUtils.d("zt", stuNum)
                initEvent()
            }
            //表示自登陆界面跳转到这里，由于没有登陆，所以需要自intent中获取
            TYPE_FORM_LOGIN_ACTIVITY_WITH_STU_NUMBER -> {
                isOriginView = false
                setContentView(R.layout.mine_activity_change_password)
                changePageType(TYPE_NEW_PASSWORD)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                setToolBar(TYPE_NEW_PASSWORD)
                //将此处的学号更新为自外界传递进来的学号
                stuNum = intent.getStringExtra("stuNumber")
                code = intent.getIntExtra("code", -1)
                isFromLogin = true
                initEvent()
            }
        }
    }

    private fun changePageType(type: Int) {
        when (type) {
            TYPE_OLD_PASSWORDS -> {
                mine_security_secondinput_password.visibility = View.GONE
                mine_iv_security_change_paswword_line1_eye.visibility = View.GONE
                mine_iv_security_front_ic_line2.visibility = View.GONE
                mine_tv_security_tip_line2.visibility = View.GONE
                mine_tv_security_tip_line1.visibility = View.GONE
                mine_divider2.visibility = View.GONE
                mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                mine_security_firstinput_password.hint = getString(R.string.mine_security_type_in_old_password)
            }
            TYPE_NEW_PASSWORD -> {
                mine_security_secondinput_password.visibility = View.VISIBLE
                mine_iv_security_change_paswword_line1_eye.visibility = View.GONE
                mine_iv_security_front_ic_line2.visibility = View.VISIBLE
                mine_divider2.visibility = View.VISIBLE
                mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                mine_tv_security_tip_line1.visibility = View.GONE
                mine_tv_security_tip_line2.visibility = View.GONE
                mine_security_firstinput_password.hint = getString(R.string.mine_security_please_type_new_words)
                mine_security_tv_forget_password.visibility = View.GONE
            }
        }
    }

    fun changeButtonColorType(type: Int) {
        when (type) {
            TYPE_COLOR_LIGHT_BUTTON -> {
                mine_bt_security_change_password_confirm.background = ContextCompat.getDrawable(this, R.drawable.mine_shape_security_next_btn)
            }
            TYPE_COLOR_NIGHT_BUTTON -> {
                mine_bt_security_change_password_confirm.background = ContextCompat.getDrawable(this, R.drawable.mine_shape_round_cornor_purple_blue)
            }
        }
    }

    private fun setToolBar(type: Int) {
        when (type) {
            TYPE_OLD_PASSWORDS -> {
                common_toolbar.apply {
                    setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity, R.color.common_white_background))
                    initWithSplitLine("修改密码",
                            false,
                            R.drawable.mine_ic_arrow_left,
                            View.OnClickListener {
                                finishAfterTransition()
                            })
                    setTitleLocationAtLeft(true)
                }
            }
            TYPE_NEW_PASSWORD -> {
                common_toolbar.apply {
                    setBackgroundColor(ContextCompat.getColor(this@ChangePasswordActivity, R.color.common_white_background))
                    initWithSplitLine("重设密码",
                            false,
                            R.drawable.mine_ic_arrow_left,
                            View.OnClickListener {
                                finishAfterTransition()
                            })
                    setTitleLocationAtLeft(true)
                }
            }
        }
    }

    private fun initEvent() {
        mine_security_secondinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
        mine_security_firstinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
        mine_iv_security_change_paswword_line2_eye.setOnSingleClickListener {
            if (isLine2ShowPassword) {
                mine_iv_security_change_paswword_line2_eye.setImageResource(R.drawable.mine_ic_close_eye)
                mine_security_firstinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
                mine_security_firstinput_password.text?.let {
                    mine_security_firstinput_password.setSelection(it.length)
                }
                isLine2ShowPassword = false
            } else {
                mine_iv_security_change_paswword_line2_eye.setImageResource(R.drawable.mine_ic_open_eye)
                mine_security_firstinput_password.transformationMethod = HideReturnsTransformationMethod.getInstance()//显示密码
                mine_security_firstinput_password.text?.let {
                    mine_security_firstinput_password.setSelection(it.length)
                }
                isLine2ShowPassword = true
            }
        }
        mine_iv_security_change_paswword_line1_eye.setOnSingleClickListener {
            if (isLine1ShowPassword) {
                mine_iv_security_change_paswword_line1_eye.setImageResource(R.drawable.mine_ic_close_eye)
                mine_security_secondinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
                mine_security_firstinput_password.text?.let {
                    mine_security_secondinput_password.setSelection(it.length)
                }
                isLine1ShowPassword = false
            } else {
                mine_iv_security_change_paswword_line1_eye.setImageResource(R.drawable.mine_ic_open_eye)
                mine_security_secondinput_password.transformationMethod = HideReturnsTransformationMethod.getInstance()//显示密码
                mine_security_firstinput_password.text?.let {
                    mine_security_secondinput_password.setSelection(it.length)
                }
                isLine1ShowPassword = true
            }
        }
        mine_security_firstinput_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                mine_tv_security_tip_line1.visibility = View.GONE
                p0?.length?.let {
                    if (isOriginView) {
                        if (p0.isNotEmpty()) {
                            changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                            isClick = true
                            mine_iv_security_change_paswword_line2_eye.visibility = View.VISIBLE
                        } else {
                            mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                            changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                            isClick = false
                        }
                    } else {
                        //第二个界面的逻辑
                        if (p0.isNotEmpty()) {
                            if (p0.length in 6..16) {
                                mine_tv_security_tip_line1.visibility = View.GONE
                            }
                            mine_security_secondinput_password.text?.isNotEmpty()?.let {
                                if (it) {
                                    //第二个不为空才去检查
                                    if (mine_security_secondinput_password.text.toString() != mine_security_firstinput_password.text.toString()) {
                                        //用于防止第二个输入框输入了，用于再次修改第一个输入框
                                        mine_tv_security_tip_line2.visibility = View.VISIBLE
                                        changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                                        isClick = false
                                    } else {
                                        mine_tv_security_tip_line2.visibility = View.GONE
                                        changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                                        isClick = true
                                    }
                                }
                            }
                            mine_iv_security_change_paswword_line2_eye.visibility = View.VISIBLE
                        } else {
                            mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                            changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                            isClick = false
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        mine_security_secondinput_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                mine_tv_security_tip_line1.visibility = View.GONE
                //判断两次输入的字符串是否相同
                p0?.length?.let {
                    if (!isOriginView) {
                        if (p0.length > 0) {
                            if (mine_security_secondinput_password.text.toString() != mine_security_firstinput_password.text.toString()) {
                                mine_tv_security_tip_line2.visibility = View.VISIBLE
                                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                                isClick = false
                            } else {
                                mine_tv_security_tip_line2.visibility = View.GONE
                                changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                                isClick = true
                            }
                            mine_iv_security_change_paswword_line1_eye.visibility = View.VISIBLE
                        } else {
                            mine_tv_security_tip_line2.visibility = View.GONE
                            mine_iv_security_change_paswword_line1_eye.visibility = View.GONE
                            changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                            isClick = false
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        //检查新密码是否上传成功！
        viewModel.inputNewPasswordCorrect.observe(this, Observer {
            if (it) {
                CyxbsToast.makeText(this, "重置密码成功！由于账号互通，重邮帮小程序的密码也会一起更改哦~", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                if (viewModel.inputNewPasswordFormat == INPUT_NEW_PASSWORD_FORMAT_IS_CORRECT) {
                    mine_tv_security_tip_line1.visibility = View.VISIBLE
                }
            }
        })
        //观察旧密码是否正确， 若正确就切换到下一个界面
        viewModel.originPassWordIsCorrect.observe(this, Observer {
            if (it) {
                //如果旧密码正确
                originPassword = mine_security_firstinput_password.text.toString()
                changePageType(TYPE_NEW_PASSWORD)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                setToolBar(TYPE_NEW_PASSWORD)
                mine_security_firstinput_password.setText("")
                isOriginView = false
            } else {
                this.toast("旧密码错误!")
            }
        })
        mine_bt_security_change_password_confirm.setOnSingleClickListener {
            if (isClick) {
                if (mine_security_firstinput_password.text == null) {
                    return@setOnSingleClickListener
                }
                if (isOriginView) {
                    //旧密码检测的网络请求
                    viewModel.originPassWordCheck(mine_security_firstinput_password.text.toString())
                } else {
                    //填写新密码的界面，跳转到上传新密码
                    mine_security_firstinput_password.text?.let {
                        if (isFromLogin) {
                            if (code != -1) {
                                if (it.length in 6.0..16.0) {
                                    //字数格式要求满足的情况下进行网络请求
                                    viewModel.resetPasswordFromLogin(stuNum, mine_security_firstinput_password.text.toString(), code)
                                    mine_tv_security_tip_line1.visibility = View.GONE
                                } else {
                                    mine_tv_security_tip_line1.visibility = View.VISIBLE
                                }
                            } else {
                                BaseApp.context.toast("后端返回的认证码存在问题，修改失败")
                            }
                        } else {
                            if (it.length in 6.0..16.0) {
                                //字数格式要求满足的情况下进行网络请求
                                viewModel.newPassWordInput(originPassword, mine_security_firstinput_password.text.toString())
                                mine_tv_security_tip_line1.visibility = View.GONE
                            } else {
                                mine_tv_security_tip_line1.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        mine_security_tv_forget_password.setOnSingleClickListener {
            mine_pb_security_change_password.visibility = View.VISIBLE
            viewModel.checkDefaultPassword(stuNum) {
                mine_pb_security_change_password.visibility = View.GONE
                if (viewModel.isDefaultPassword) {
                    //如果是默认密码
                    this.toast(getString(R.string.mine_security_default_password_hint))
                } else {
                    viewModel.checkBinding(stuNum) {
                        //此处的dialog需要传递来源，是来自登陆界面还是来自个人界面
                        ChooseFindTypeDialog.showDialog(this, viewModel.bindingEmail, viewModel.bindingPasswordProtect, this, isFromLogin, stuNum)
                    }
                }
            }
        }
    }
}