package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.SecurityQuestion
import com.mredrock.cyxbs.mine.util.apiService

/**
 * @date 2020-10-29
 * @author Sca RayleighZ
 */
class SetPasswordProtectViewModel : BaseViewModel() {
    //密保问题的题目
    val securityQuestionContent = ObservableField<String>("")

    //密保问题的答案（由用户键入）
    val securityAnswer = ObservableField<String>()

    //输入字符数不合理的提示
    val tipForInputNum = ObservableField<String>()

    //默认密保问题id为0
    var securityQuestionId = 0
    lateinit var listOfSecurityQuestion: List<SecurityQuestion>

    //请求获取密保问题的方法，建议在Activity建立之后就进行调用
    //最后传递进来的方法旨在帮助Activity刷新视图，没有使用必要时可以传空
    fun getSecurityQuestions(onQuestionLoaded: (List<SecurityQuestion>?) -> Unit) {
        apiService.getAllSecurityQuestions()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    context.toast("获取密保问题失败")
                    true
                }
                .safeSubscribeBy {
                    listOfSecurityQuestion = it.data
                    onQuestionLoaded(listOfSecurityQuestion)
                }
    }

    fun setSecurityQA(onSucceed: () -> Unit) {
        //如果输入的答案字数合理
        securityAnswer.get()?.length?.let { length ->
            if (length in 2..17) {
                tipForInputNum.set("")
                LogUtils.d("SetProtectViewModel", "id = $securityQuestionId, content = ${securityAnswer.get().toString()}")
                apiService.setSecurityQuestionAnswer(
                        id = securityQuestionId,
                        content = securityAnswer.get().toString())
                        .setSchedulers()
                        .doOnErrorWithDefaultErrorHandler {
                            context.toast(it.toString())
                            true
                        }
                        .safeSubscribeBy {
                            if (it.status == 10000) {
                                context.toast("恭喜您，设置成功")
                                onSucceed()
                            }
                        }
            }
        }
    }
}