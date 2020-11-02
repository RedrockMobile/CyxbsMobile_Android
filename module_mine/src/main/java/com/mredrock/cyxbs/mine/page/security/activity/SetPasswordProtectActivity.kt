package com.mredrock.cyxbs.mine.page.security.activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivitySetPasswordProtectBinding
import com.mredrock.cyxbs.mine.page.security.fragment.SelQuestionDialog
import com.mredrock.cyxbs.mine.page.security.viewmodel.SetPasswordProtectViewModel
import kotlinx.android.synthetic.main.mine_activity_set_password_protect.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 设置密保的活动
 */
class SetPasswordProtectActivity : BaseViewModelActivity<SetPasswordProtectViewModel>() {

    override val isFragmentActivity = false

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
            LogUtils.d("SecurityActivity", it.toString())
        }

        //加载sheetDialog，设置展开和消失的阴影
        val selQuestionDialog = SelQuestionDialog(this, viewModel.listOfSecurityQuestion) {
            mine_tv_security_question.text = viewModel.listOfSecurityQuestion[it].content
            setBackGroundShadowOrShadow()
        }
        mine_ll_sel_question_show.setOnClickListener {
            selQuestionDialog.show()
            setBackGroundShadowOrShadow()
        }

        selQuestionDialog.setOnCancelListener {
            setBackGroundShadowOrShadow()
        }

        //确定设置密保的点击事件
        mine_bt_security_set_protectconfirm.setOnClickListener{
            mine_bt_security_set_protectconfirm.background = ContextCompat.getDrawable(this , R.drawable.mine_shape_round_cornor_purple_blue)
            viewModel.setSecurityQA()
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