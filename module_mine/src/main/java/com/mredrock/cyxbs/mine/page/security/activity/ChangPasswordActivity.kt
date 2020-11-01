package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.ChangePasswordViewModel
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_change_password)
        setToolBar(TYPE_OLD_PASSWORDS)
        changePageType(TYPE_OLD_PASSWORDS)
        changeButtonColorType(TYPE_COLOR_LIGHT_BUTTON)
    }

    fun changePageType(type: Int) {
        when (type) {
            TYPE_OLD_PASSWORDS -> {
                mine_textinputlayout_line2.visibility = View.GONE
                mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                mine_iv_security_front_ic_line2.visibility = View.GONE
                mine_tv_security_tip_line2.visibility = View.GONE
                mine_tv_security_tip_line1.visibility=View.GONE
                mine_divider2.visibility=View.GONE
                mine_iv_security_change_paswword_line1_eye.visibility = View.GONE
                mine_security_firstinput_password.hint = R.string.mine_security_type_in_old_password.toString()
            }
            TYPE_NEW_PASSWORD -> {
                mine_textinputlayout_line2.visibility = View.VISIBLE
                mine_iv_security_change_paswword_line2_eye.visibility = View.VISIBLE
                mine_iv_security_front_ic_line2.visibility = View.VISIBLE
                mine_tv_security_tip_line2.visibility = View.VISIBLE
                mine_iv_security_change_paswword_line1_eye.visibility = View.GONE
            }
        }
    }

    fun changeButtonColorType(type: Int) {
        when (type) {
            TYPE_COLOR_LIGHT_BUTTON -> {
                mine_bt_security_change_password_confirm.background = ContextCompat.getDrawable(this,R.drawable.mine_shape_security_next_btn)
        }
            TYPE_COLOR_NIGHT_BUTTON -> {
                mine_bt_security_change_password_confirm.background = ContextCompat.getDrawable(this,R.drawable.mine_shape_round_cornor_purple_blue)
            }
        }
    }

    fun setToolBar(type: Int) {
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
}