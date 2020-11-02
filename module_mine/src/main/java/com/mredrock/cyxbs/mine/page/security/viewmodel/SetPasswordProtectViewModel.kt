package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.SecurityQuestion
import com.mredrock.cyxbs.mine.util.apiService

/**
 * @date 2020-10-29
 * @author Sca RayleighZ
 */
class SetPasswordProtectViewModel : BaseViewModel() {
    //密保问题的题目
    val securityQuestionContent = ObservableField<String>(context.getString(R.string.mine_security_your_best_friend_name))

    //密保问题的答案（由用户键入）
    val securityAnswer = ObservableField<String>()

    //输入字符数不合理的提示
    val tipForInputNum = ObservableField<String>()

    //默认密保问题id为0
    private val securityQuestionId = 0
    lateinit var listOfSecurityQuestion: List<SecurityQuestion>

    //请求获取密保问题的方法，建议在Activity建立之后就进行调用
    //最后传递进来的方法旨在帮助Activity刷新视图，没有使用必要时可以传空
    fun getSecurityQuestions(onQuestionLoaded: (List<SecurityQuestion>?) -> Unit) {
        apiService.getAllSecurityQuestions()
                .safeSubscribeBy(
                        onNext = {
                            listOfSecurityQuestion = it.data
                            onQuestionLoaded(listOfSecurityQuestion)
                        },
                        onError = {
                            it.message?.let { it1 -> LogUtils.d("SecurityActivity", it1) }
                            context.toast("获取密保问题失败")
                            //TODO：因为后端接口暂时还不能使用，所以暂时使用虚假数据
                            listOfSecurityQuestion = listOf(
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字"),
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字"),
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字"),
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字"),
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字"),
                                    SecurityQuestion(1, "你的好朋友的名字"),
                                    SecurityQuestion(2, "你最喜欢的足球队的名字"),
                                    SecurityQuestion(3, "你最喜欢的球星的名字"),
                                    SecurityQuestion(4, "你的高中班主任的名字")
                            )
                        }
                )
    }

    fun setSecurityQA() {
        //如果输入的答案数字少于2或者大于16
        when {
            securityAnswer.get()?.length!! < 2 -> {
                LogUtils.d("SecurityActivity" , "字符数过少")
                tipForInputNum.set("请至少输入2个字符")
            }
            securityAnswer.get()?.length!! > 16 -> {
                LogUtils.d("SecurityActivity" , "字符数过多")
                tipForInputNum.set("请至多输入16个字符")
            }
            else -> {
                tipForInputNum.set("")
                apiService.setSecurityQuestionAnswer(
                        id = securityQuestionId,
                        content = securityAnswer.get().toString())
                        .safeSubscribeBy(
                                onNext = {
                                    if (it.status == 10000) {
                                        context.toast("恭喜您，设置成功")
                                    }
                                }
                        )
            }
        }
    }
}