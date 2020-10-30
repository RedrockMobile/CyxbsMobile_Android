package com.mredrock.cyxbs.mine.page.security.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.viewmodel.ChangePasswordViewModel
import kotlinx.android.synthetic.main.mine_activity_chang_password.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 */
class ChangPasswordActivity : BaseViewModelActivity<ChangePasswordViewModel>() {
    override val isFragmentActivity = false
    override val viewModelClass = ChangePasswordViewModel::class.java

    companion object {
        //下面的常量是转化页面形态的标识量
        const val TYPE_OLD_PASSWORDS = 0 //仅一行的输入旧密码模式
        const val TYPE_NEW_PASSWORD = 1 //两行的输入新密码模式
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_chang_password)
    }

    fun changePageType(type : Int){
        when(type){
            TYPE_OLD_PASSWORDS ->{
                mine_textinputlayout_line2.visibility = View.GONE
                mine_iv_security_change_paswword_line2_eye.visibility = View.GONE
                mine_iv_security_front_ic_line2.visibility = View.GONE
                mine_tv_security_tip_line2.visibility = View.GONE
                //TODO: 换字
            }
            TYPE_NEW_PASSWORD ->{
                
            }
        }
    }
}