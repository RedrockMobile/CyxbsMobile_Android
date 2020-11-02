package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * Author: RayleighZ
 * Time: 2020-10-31 1:33
 */
class ChangePasswordViewModel : BaseViewModel() {

    //旧密码是否输入正确
    var originPassWordIsCorrect=ObservableField<Boolean>(false)


    //检查旧密码输入是否相同
    fun originPassWordCheck(originPassword:String?){

    }

}