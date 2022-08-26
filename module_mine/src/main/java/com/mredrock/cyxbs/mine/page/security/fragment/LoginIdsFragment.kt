package com.mredrock.cyxbs.mine.page.security.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.base.ui.BaseBindFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineFragmentFindPasswordIdsLoginBinding
import com.mredrock.cyxbs.mine.page.security.activity.FindPasswordByIdsActivity
import com.mredrock.cyxbs.mine.page.security.util.IdsFindPasswordDialog
import com.mredrock.cyxbs.mine.page.security.viewmodel.FindPasswordByIdsViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * @author : why
 * @time   : 2022/8/21 22:32
 * @bless  : God bless my code
 */
class LoginIdsFragment : BaseBindFragment<MineFragmentFindPasswordIdsLoginBinding>() {

    /**
     * activity的viewModel
     */
    private val mViewModel: FindPasswordByIdsViewModel by lazy {
        ViewModelProvider(requireActivity())[FindPasswordByIdsViewModel::class.java]
    }

    /**
     * 将参数转换为json类型时使用
     */
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    //设置按钮背景使用
    /**
     * 不可点击的背景
     */
    private var mUnClickable: Drawable? = null

    /**
     * 可点击的背景
     */
    private var mClickable: Drawable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * 提示用的dialog
         */
        val dialog =
            IdsFindPasswordDialog(requireActivity())
                .setTitle("错误")
                .setContent("请重新核验学号是否与统一认证码绑定、密码是否正确，如若忘记统一认证码密码请前往教务在线进行改密操作")
                .setConfirm { dismiss() }

        mUnClickable = AppCompatResources.getDrawable(
            requireActivity(),
            R.drawable.mine_shape_bg_find_password_ids_button_invalid
        )
        mClickable = AppCompatResources.getDrawable(
            requireActivity(),
            R.drawable.mine_shape_bg_find_password_ids_button
        )
        //刚进入页面时按钮设置为灰色,且不可点击
        binding.mineBtnFindPasswordIdsCheck.background = mUnClickable
        binding.mineBtnFindPasswordIdsCheck.isEnabled = false
        binding.mineEtFindPasswordIdsStuNum.addTextChangedListener()
        binding.mineEtFindPasswordIdsAccount.addTextChangedListener()
        binding.mineEtFindPasswordIdsPassword.addTextChangedListener()
        mViewModel.isGetCodeSuccess.observe {
            if (!it) {
                binding.mineBtnFindPasswordIdsCheck.text = "验证"
                binding.mineBtnFindPasswordIdsCheck.isEnabled = true
                //若出错则弹出dialog提示
                dialog.show()
            } else {
                //若成功获取验证码，则保存学号并进入修改密码界面
                mViewModel.stuNum = binding.mineEtFindPasswordIdsStuNum.text.toString()
                (requireActivity() as FindPasswordByIdsActivity).replace { ConfirmPasswordFragment() }
            }
        }
    }

    /**
     * 设置按钮的颜色、是否可点击以及具体的点击事件
     */
    @SuppressLint("SetTextI18n")
    private fun EditText.addTextChangedListener() {
        doOnTextChanged { _, _, _, _ ->
            //若三个输入栏中的内容均不为空，则背景设置为正常颜色，且可以点击
            if (
                binding.mineEtFindPasswordIdsStuNum.text.toString() != "" &&
                binding.mineEtFindPasswordIdsAccount.text.toString() != "" &&
                binding.mineEtFindPasswordIdsPassword.text.toString() != ""
            ) {
                //设置按钮背景颜色为正常的渐变蓝
                binding.mineBtnFindPasswordIdsCheck.background = mClickable
                //设置按钮可点击，并设置点击事件
                binding.mineBtnFindPasswordIdsCheck.isEnabled = true
                binding.mineBtnFindPasswordIdsCheck.setOnSingleClickListener {
                    //设置按钮的文字为Loading
                    binding.mineBtnFindPasswordIdsCheck.text = "Loading..."
                    binding.mineBtnFindPasswordIdsCheck.isEnabled = false
                    //发送获取验证码的请求
                    val jsonBody = JSONObject().apply {
                        put("stu_num", binding.mineEtFindPasswordIdsStuNum.text.toString())
                        put("ids_num", binding.mineEtFindPasswordIdsAccount.text.toString())
                        put("password", binding.mineEtFindPasswordIdsPassword.text.toString())
                    }
                    mViewModel.getCode(jsonBody.toString().toRequestBody(jsonType))
                }
            } else {
                //设置按钮背景颜色为灰色
                binding.mineBtnFindPasswordIdsCheck.background = mUnClickable
                //设置按钮不可点击
                binding.mineBtnFindPasswordIdsCheck.isEnabled = false
            }
        }
    }
}