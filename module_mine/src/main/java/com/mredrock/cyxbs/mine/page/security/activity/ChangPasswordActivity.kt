package com.mredrock.cyxbs.mine.page.security.activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.account.IUserService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.ChangePasswordViewModel
import com.mredrock.cyxbs.mine.util.ui.ChooseFindTypeDialog
import com.mredrock.cyxbs.mine.util.ui.DoubleChooseDialog
import kotlinx.android.synthetic.main.mine_activity_change_password.*

/**
 * Author: SpreadWater
 * Time: 2020-10-29 15:06
 */
class ChangPasswordActivity : BaseViewModelActivity<ChangePasswordViewModel>() {

    override val isFragmentActivity = false

    companion object {
        //下面的常量是转化页面形态的标识量
        const val TYPE_OLD_PASSWORDS = 0 //仅一行的输入旧密码模式
        const val TYPE_NEW_PASSWORD = 1 //两行的输入新密码模式
        const val TYPE_COLOR_LIGHT_BUTTON = 2//没输入满时按钮颜色
        const val TYPE_COLOR_NIGHT_BUTTON = 3//深色按钮
        const val TYPE_START_FROM_MINE=4//从个人页面跳转到修改密码页面
        const val TYPE_START_FROM_OTHERS=5//从密保和邮箱界面跳转
        fun actionStart(context:Context,startType:Int){
            val intent=Intent(context,ChangPasswordActivity::class.java)
            intent.putExtra("startType",startType)
            context.startActivity(intent)
        }
    }

    private var isLine2ShowPassword = false//是否显示密码

    private var isLine1ShowPassword = false//是否显示密码

    var isClick=false//按钮是否能点击

    var isOriginView=true//是否是在第一个界面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type=intent.getIntExtra("startType",4)
        initView(type)
        //TODO:测试界面用
        //FindPasswordActivity.start(this , FIND_PASSWORD_BY_EMAIL)
        /*ChooseFindTypeDialog.showDialog(
                this,
                hasEmailBinding = true,
                hasSecurityQuestion = false
        )*/
        DoubleChooseDialog.show(this)
    }

    private fun initView(type: Int){
        when(type){
            TYPE_START_FROM_MINE->{
                setContentView(R.layout.mine_activity_change_password)
                setToolBar(TYPE_OLD_PASSWORDS)
                changePageType(TYPE_OLD_PASSWORDS)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                InitEvent()
            }
            TYPE_START_FROM_OTHERS->{
                isOriginView=false
                setContentView(R.layout.mine_activity_change_password)
                changePageType(TYPE_NEW_PASSWORD)
                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                setToolBar(TYPE_NEW_PASSWORD)
                InitEvent()
            }
        }
    }
    fun changePageType(type: Int) {
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
                mine_textview4.visibility = View.GONE
                mine_security_firstinput_password.hint = getString(R.string.mine_security_pleace_type_new_words)
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
                    setBackgroundColor(ContextCompat.getColor(this@ChangPasswordActivity, R.color.common_white_background))
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
                    setBackgroundColor(ContextCompat.getColor(this@ChangPasswordActivity, R.color.common_white_background))
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

    private fun InitEvent() {
        mine_security_secondinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
        mine_security_firstinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
        mine_iv_security_change_paswword_line2_eye.setOnClickListener {
            if (isLine2ShowPassword) {
                mine_iv_security_change_paswword_line2_eye.setImageResource(R.drawable.mine_ic_close_eye)
                mine_security_firstinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
                mine_security_firstinput_password.setSelection(mine_security_firstinput_password.text!!.length)
                isLine2ShowPassword = false
            } else {
                mine_iv_security_change_paswword_line2_eye.setImageResource(R.drawable.mine_ic_open_eye)
                mine_security_firstinput_password.transformationMethod = HideReturnsTransformationMethod.getInstance()//显示密码
                mine_security_firstinput_password.setSelection(mine_security_firstinput_password.text!!.length)
                isLine2ShowPassword = true
            }
        }
        mine_iv_security_change_paswword_line1_eye.setOnClickListener {
            if (isLine1ShowPassword) {
                mine_iv_security_change_paswword_line1_eye.setImageResource(R.drawable.mine_ic_close_eye)
                mine_security_secondinput_password.transformationMethod = PasswordTransformationMethod.getInstance()//不显示密码
                mine_security_secondinput_password.setSelection(mine_security_secondinput_password.text!!.length)
                isLine1ShowPassword = false
            } else {
                mine_iv_security_change_paswword_line1_eye.setImageResource(R.drawable.mine_ic_open_eye)
                mine_security_secondinput_password.transformationMethod = HideReturnsTransformationMethod.getInstance()//显示密码
                mine_security_secondinput_password.setSelection(mine_security_secondinput_password.text!!.length)
                isLine1ShowPassword = true
            }
        }
        mine_security_firstinput_password.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (isOriginView){
                    if (p0?.length!!>0){
//                            Log.d("zt","1")
                            changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                            isClick=true
                            mine_iv_security_change_paswword_line2_eye.visibility=View.VISIBLE
                    }else{
//                        Log.d("zt","2")
                        mine_iv_security_change_paswword_line2_eye.visibility=View.GONE
                        changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                        isClick=false
                    }
                }else{
                    //第二个界面的逻辑
                    if (p0?.length!!>0) {
                        if (mine_security_secondinput_password.text?.isNotEmpty()!!){
                            //第二个不为空才去检查
                            if (mine_security_secondinput_password.text.toString() != mine_security_firstinput_password.text.toString()) {
                                //用于防止第二个输入框输入了，用于再次修改第一个输入框
//                                Log.d("zt","3")
                                mine_tv_security_tip_line2.visibility = View.VISIBLE
                                changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                                isClick=false
                            } else {
//                                Log.d("zt","4")
                                mine_tv_security_tip_line2.visibility = View.GONE
                                changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                                isClick = true
                            }
                        }
                        mine_iv_security_change_paswword_line2_eye.visibility = View.VISIBLE
                    }else{
//                        Log.d("zt","5")
                        mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                        changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                        isClick=false
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
                //判断两次输入的字符串是否相同
                if (!isOriginView){
                    if (p0?.length!! > 0) {
                        if (mine_security_secondinput_password.text.toString() != mine_security_firstinput_password.text.toString()) {
                            mine_tv_security_tip_line2.visibility = View.VISIBLE
                            changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                            isClick=false
//                            Log.d("zt","6")
                        } else {
//                            Log.d("zt","7")
                            mine_tv_security_tip_line2.visibility = View.GONE
                            changeButtonColorType(TYPE_COLOR_NIGHT_BUTTON)
                            isClick=true
                        }
                        mine_iv_security_change_paswword_line1_eye.visibility=View.VISIBLE
                    } else {
//                        Log.d("zt","8")
                        mine_tv_security_tip_line2.visibility = View.GONE
                        mine_iv_security_change_paswword_line1_eye.visibility=View.GONE
                        changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                        isClick=false
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        mine_bt_security_change_password_confirm.setOnClickListener {
            if (isClick) {
                if (isOriginView) {
                    //旧密码检测的网络请求

                    //切换到修改新密码的页面
//                    Log.d("zt","9")
                    changePageType(TYPE_NEW_PASSWORD)
                    changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
                    setToolBar(TYPE_NEW_PASSWORD)
                    mine_security_firstinput_password.setText("")
                    isOriginView=false
                } else {
                    //填写新密码的界面，跳转到上传新密码
//                    Log.d("zt","10")
                }
            }else{
                //不可点击状态下用户点击了
//                Log.d("zt","11")
            }

        }
    }
}