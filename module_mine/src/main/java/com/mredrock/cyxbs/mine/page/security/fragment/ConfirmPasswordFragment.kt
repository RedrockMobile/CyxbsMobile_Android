package com.mredrock.cyxbs.mine.page.security.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.base.ui.BaseBindFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineFragmentFindPasswordIdsConfirmBinding
import com.mredrock.cyxbs.mine.page.security.util.IdsFindPasswordDialog
import com.mredrock.cyxbs.mine.page.security.viewmodel.FindPasswordByIdsViewModel

/**
 * @author : why
 * @time   : 2022/8/22 00:33
 * @bless  : God bless my code
 */
class ConfirmPasswordFragment : BaseBindFragment<MineFragmentFindPasswordIdsConfirmBinding>() {

    /**
     * activity的viewModel
     */
    private val mViewModel: FindPasswordByIdsViewModel by lazy {
        ViewModelProvider(requireActivity())[FindPasswordByIdsViewModel::class.java]
    }

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
        mUnClickable = AppCompatResources.getDrawable(
            requireActivity(),
            R.drawable.mine_shape_bg_find_password_ids_button_invalid
        )
        mClickable = AppCompatResources.getDrawable(
            requireActivity(),
            R.drawable.mine_shape_bg_find_password_ids_button
        )
        val dialog =
            IdsFindPasswordDialog(requireActivity())
                .setTitle("成功")
                .setContent("新密码修改成功，请重新登录后再进行操作")
                .setConfirm {
                    //并设置点击跳转至登录界面
                    ILoginService::class.impl
                        .startLoginActivityReboot(context)
                    dismiss()
                    requireActivity().finish()
                }

        //刚进入页面时按钮设置为灰色,且不可点击
        binding.mineBtnFindPasswordIdsChange.background = mUnClickable
        binding.mineBtnFindPasswordIdsChange.isEnabled = false
        binding.run {
            mineEtFindPasswordIdsInputPassword.addTextChangedListener()
            mineEtFindPasswordIdsConfirmPassword.addTextChangedListener()
            mViewModel.isChangeSuccess.observe {
                if (!it) {
                    binding.mineBtnFindPasswordIdsChange.text = "修改"
                    binding.mineBtnFindPasswordIdsChange.isEnabled = true
                    "修改失败！".toast()
                } else {
                    //修改成功后弹出dialog提示
                    dialog.show()
                }
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
                binding.mineEtFindPasswordIdsInputPassword.text.toString() != "" &&
                binding.mineEtFindPasswordIdsConfirmPassword.text.toString() != ""
            ) {
                //设置按钮背景颜色为正常的渐变蓝
                binding.mineBtnFindPasswordIdsChange.background = mClickable
                //设置按钮可点击
                binding.mineBtnFindPasswordIdsChange.isEnabled = true
                binding.mineBtnFindPasswordIdsChange.setOnSingleClickListener {
                    if (binding.mineEtFindPasswordIdsInputPassword.text.toString() ==
                        binding.mineEtFindPasswordIdsConfirmPassword.text.toString()
                    ) {
                        //若长度不够则弹出提示
                        if (binding.mineEtFindPasswordIdsInputPassword.text.toString().length < 6) {
                            "密码长度应不小于6位哦~".toast()
                        } else {
                            //若两个输入栏里面的内容相同且长度足够，则正常设置点击事件
                            //设置提示内容为不可见
                            binding.mineTvFindPasswordIdsDifferent.gone()
                            binding.mineBtnFindPasswordIdsChange.text = "Loading..."
                            binding.mineBtnFindPasswordIdsChange.isEnabled = false
                            mViewModel.changePassword(
                                mViewModel.stuNum,
                                binding.mineEtFindPasswordIdsInputPassword.text.toString()
                            )
                        }
                    } else {
                        //若两个输入栏里面的内容不相同，则弹出提示
                        binding.mineTvFindPasswordIdsDifferent.visible()
                    }
                }
            } else {
                //设置按钮背景颜色为灰色
                binding.mineBtnFindPasswordIdsChange.background = mUnClickable
                //设置按钮不可点击
                binding.mineBtnFindPasswordIdsChange.isEnabled = false
            }
        }
    }
}