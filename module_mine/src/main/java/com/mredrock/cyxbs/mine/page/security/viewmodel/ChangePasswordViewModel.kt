package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * Author: RayleighZ
 * Time: 2020-10-31 1:33
 */
class ChangePasswordViewModel : BaseViewModel() {
    /*//是否展示第一行的眼睛图标
    val showFirstEyeIcon = ObservableField<Boolean>(false)
    //是否展示第二行的眼睛图标
    val showSecondEyeIcon = ObservableField<Boolean>(false)
    //是否展示第二行的editText
    val showSecondEditText = ObservableField<Boolean>(false)
    //是否展示第二行的输入行前图标
    val showSecondInputIcon = ObservableField<Boolean>(false)
    //是否展示第一行的*/

    //旧密码是否输入正确
    var originPassWordIsCorrect=ObservableField<Boolean>(false)


    //检查旧密码输入是否相同
    fun originPassWordCheck(originPassword:String?){

    }
}