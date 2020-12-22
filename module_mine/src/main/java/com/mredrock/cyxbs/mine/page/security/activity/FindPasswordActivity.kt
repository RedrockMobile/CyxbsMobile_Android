package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFindPasswordBinding
import com.mredrock.cyxbs.mine.page.security.util.AnswerTextWatcher
import com.mredrock.cyxbs.mine.page.security.util.Jump2QQHelper
import com.mredrock.cyxbs.mine.page.security.viewmodel.FindPasswordViewModel
import kotlinx.android.synthetic.main.mine_activity_find_password.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 找回密码的活动
 */
class FindPasswordActivity : BaseViewModelActivity<FindPasswordViewModel>() {
    //在此activity以及ViewModel中统一使用这个stuNumber来获取学号，以方便整体修改
    private var stuNumber = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()

    //是否来自登陆界面
    private var isFromLogin = false

    companion object {
        //自登陆界面而来
        fun actionStartFromLogin(context: Context, type: Int, stuNumber: String) {
            val intent = Intent(context, FindPasswordActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("stu_number", stuNumber)
            //是否是从登陆界面过来的（是否已经登陆，将影响学号的获取）
            intent.putExtra("is_from_login", true)
            context.startActivity(intent)
        }

        //从个人界面而来（已经登陆的状态）
        fun actionStartFromMine(context: Context, type: Int) {
            val intent = Intent(context, FindPasswordActivity::class.java)
            intent.putExtra("type", type)
            //是否是从登陆界面过来的（是否已经登陆，将影响学号的获取）
            intent.putExtra("is_from_login", false)
            context.startActivity(intent)
        }

        const val FIND_PASSWORD_BY_EMAIL = 0//找回密码的方式为邮箱找回
        const val FIND_PASSWORD_BY_SECURITY_QUESTION = 1//找回密码的方式为密保问题找回
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dataBinding
        val binding = DataBindingUtil.inflate<MineActivityFindPasswordBinding>(
                LayoutInflater.from(this),
                R.layout.mine_activity_find_password, null, false
        )
        binding.viewModel = viewModel
        setContentView(binding.root)
        //设置toolBar
        common_toolbar.apply {
            this.initWithSplitLine(
                    context.getString(R.string.mine_security_find_password)
            )
        }

        //首先判断是否是自登陆界面来到的这里，如果是，就刷新当前的stuNumber
        isFromLogin = intent.getBooleanExtra("is_from_login", false)
        if (isFromLogin) {
            stuNumber = intent.getStringExtra("stu_number")
        }
        val type = intent.getIntExtra("type", FIND_PASSWORD_BY_SECURITY_QUESTION)//如果出错，则默认展示为按照密保问题进行找回密码
        //配置viewModel内部的学号
        viewModel.stuNumber = stuNumber
        //更改页面样式
        turnPageType(type)
        //联系我们的点击事件
        mine_tv_security_find_contract_us.setOnSingleClickListener {
            Jump2QQHelper.onFeedBackClick(this)
        }
    }

    private fun turnPageType(type: Int) {
        when (type) {
            FIND_PASSWORD_BY_EMAIL -> {
                //首先请求以获取当前用户的邮箱，展示给用户
                viewModel.getBindingEmail()
                //将页面变更为为按照邮箱进行查找
                //首先设置inputBox(ll)的高度
                mine_ll_securoty_find_password_input_box.apply {
                    this.layoutParams.height = context.dp2px(41f)
                }
                //更改title和hint的提示字符
                mine_et_security_find.hint = getString(R.string.mine_security_please_type_in_confirm_code)
                mine_tv_security_find_first_title.text = getString(R.string.mine_security_click_to_get_confirm_code)
                mine_tv_security_second_title.visibility = View.GONE
                //设置点击获取验证码的text
                viewModel.timerText.set(getString(R.string.mine_security_get_confirm_code))
                //接下来配置页面的点击事件以及相关逻辑
                //点击获取验证码(内部含有倒计时)
                mine_tv_security_find_send_confirm_code.setOnSingleClickListener {
                    viewModel.sendConfirmCodeAndStartBackTimer()
                }

                mine_et_security_find.addTextChangedListener(
                        object : AnswerTextWatcher(viewModel.firstTipText, mine_bt_security_find_next, this) {
                            override fun afterTextChanged(s: Editable?) {
                                if (s?.length !in 5..6) {
                                    button.background = ContextCompat.getDrawable(context, R.drawable.mine_shape_round_corner_light_blue)
                                } else {
                                    button.background = ContextCompat.getDrawable(context, R.drawable.mine_shape_round_cornor_purple_blue)
                                }
                            }
                        }
                )

                //点击下一步以判断验证码是否正确
                mine_bt_security_find_next.setOnSingleClickListener {
                    viewModel.confirmCode(
                            onSuccess = {
                                if (isFromLogin) {
                                    ChangePasswordActivity.startFormLogin(this, stuNumber, it)
                                } else {
                                    ChangePasswordActivity.actionStart(this, ChangePasswordActivity.TYPE_START_FROM_OTHERS)
                                }
                                finish()
                            },
                            onField = {
                                viewModel.firstTipText.set("验证码有误或过期，请重新获取")
                            }
                    )
                }
            }
            FIND_PASSWORD_BY_SECURITY_QUESTION -> {
                //默认的界面
                //首先获取用户的密保问题
                viewModel.getUserQuestion()
                //设置点击事件，即认证密保问题
                mine_et_security_find.addTextChangedListener(
                        AnswerTextWatcher(viewModel.firstTipText, mine_bt_security_find_next, this)
                )
                mine_bt_security_find_next.setOnSingleClickListener {
                    viewModel.confirmAnswer {
                        ChangePasswordActivity.startFormLogin(this, stuNumber, it)
                        finish()
                    }
                }
                mine_tv_security_second_title.visibility = View.VISIBLE
            }
        }
    }
}