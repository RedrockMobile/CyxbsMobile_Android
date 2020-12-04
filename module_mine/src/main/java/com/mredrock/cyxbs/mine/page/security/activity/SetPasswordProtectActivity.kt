package com.mredrock.cyxbs.mine.page.security.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivitySetPasswordProtectBinding
import com.mredrock.cyxbs.mine.page.security.util.AnswerTextWatcher
import com.mredrock.cyxbs.mine.page.security.viewmodel.SetPasswordProtectViewModel
import com.mredrock.cyxbs.mine.util.ui.SelQuestionDialog
import kotlinx.android.synthetic.main.mine_activity_set_password_protect.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 设置密保的活动
 */
class SetPasswordProtectActivity : BaseViewModelActivity<SetPasswordProtectViewModel>() {
    lateinit var selQuestionDialog: SelQuestionDialog

    var canClick = false

    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, SetPasswordProtectActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.inflate<MineActivitySetPasswordProtectBinding>(LayoutInflater.from(this),
                R.layout.mine_activity_set_password_protect, null, false)
        binding.viewModel = viewModel
        setContentView(binding.root)

        //设置toolBar
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this.context, R.color.common_white_background))
            initWithSplitLine(getString(R.string.mine_security_set_password_protect),
                    true
            )
        }

        //网络请求获取密保问题
        viewModel.getSecurityQuestions {
            canClick = true
            //加载sheetDialog，设置展开和消失的阴影
            selQuestionDialog = SelQuestionDialog(this, viewModel.listOfSecurityQuestion) {
                mine_tv_security_question.text = viewModel.listOfSecurityQuestion[it].content
                viewModel.securityQuestionId = viewModel.listOfSecurityQuestion[it].id
                setBackGroundShadowOrShadow()
            }

            mine_tv_security_question.text = viewModel.listOfSecurityQuestion[0].content
            viewModel.securityQuestionId = viewModel.listOfSecurityQuestion[0].id

            selQuestionDialog.setOnCancelListener {
                setBackGroundShadowOrShadow()
            }
        }


        mine_ll_sel_question_show.setOnSingleClickListener {
            if (canClick) {
                selQuestionDialog.show()
                setBackGroundShadowOrShadow()
            } else {
                BaseApp.context.toast("正在执行网络请求，请稍候")
            }
        }

        //对editText输入进行监听
        mine_edt_security_answer.addTextChangedListener(
                AnswerTextWatcher(viewModel.tipForInputNum, mine_bt_security_set_protectconfirm, this)
        )

        //确定设置密保的点击事件
        mine_bt_security_set_protectconfirm.setOnSingleClickListener {
            viewModel.setSecurityQA {
                finish()
            }
        }
    }

    private fun setBackGroundShadowOrShadow() {
        //设置sheetDialog弹出之后的背景阴影的有无
        val lp = window.attributes
        lp.alpha = if (lp.alpha == 1.0f) 0.6f else 1.0f
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}