package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFindPasswordBinding
import com.mredrock.cyxbs.mine.page.security.viewmodel.FindPasswordViewModel
import kotlinx.android.synthetic.main.mine_activity_find_password.*
/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 找回密码的活动
 */
class FindPasswordActivity : BaseViewModelActivity<FindPasswordViewModel>() {

    //TODO:顶部ToolBar返回按钮的颜色以及大小需要改变
    //TODO:字符串尚未写入String文件
    override val isFragmentActivity = false
    companion object{
        fun start (context: Context, type : Int){
            val intent = Intent(context , FindPasswordActivity::class.java)
            intent.putExtra("type" , type)
            context.startActivity(intent)
        }
        const val FIND_PASSWORD_BY_EMAIL = 0
        const val FIND_PASSWORD_BY_SECURITY_QUESTION = 1
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
        //获取用户绑定的邮箱
        viewModel.getBindingEmail()
        common_toolbar.apply {
            this.initWithSplitLine(
                    "找回密码"
            )
        }
        val type = intent.getIntExtra("type" , FIND_PASSWORD_BY_SECURITY_QUESTION)//如果出错，则默认展示为按照密保问题进行找回密码
        turnPageType(type)
    }

    private fun turnPageType(type : Int){
        when(type){
            FIND_PASSWORD_BY_EMAIL -> {
                //将页面变更为为按照邮箱进行查找
                //首先设置inputBox(ll)的高度
                mine_ll_securoty_find_password_input_box.apply {
                    this.layoutParams.height = context.dp2px(41f)
                }
                //更改title和hint的提示字符
                mine_et_security_find.hint = "请输入验证码"
                mine_tv_security_find_first_title.text = "你的保密邮箱是，请点击获取验证码"
                //设置点击获取验证码的text
                viewModel.timerText.set("获取验证码")
                //接下来配置页面的点击事件以及相关逻辑
                //点击获取验证码(内部含有倒计时)
                mine_tv_security_find_send_confirm_code.setOnClickListener {
                    viewModel.sendConfirmCodeAndStartBackTimer()
                }
                //点击下一步以判断验证码是否正确
                mine_bt_security_find_next.setOnClickListener {
                    viewModel.sendConfirmCodeRequest()
                }
            }
            FIND_PASSWORD_BY_SECURITY_QUESTION ->{

            }
        }
    }

}