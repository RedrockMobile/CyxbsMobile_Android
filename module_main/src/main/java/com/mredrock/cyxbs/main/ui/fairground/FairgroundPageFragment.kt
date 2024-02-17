package com.mredrock.cyxbs.main.ui.fairground

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.DECLARE_ENTRY
import com.mredrock.cyxbs.config.route.FAIRGROUND_ENTRY
import com.mredrock.cyxbs.config.route.FOOD_ENTRY
import com.mredrock.cyxbs.config.route.UFIELD_MAIN_ENTRY
import com.mredrock.cyxbs.lib.base.operations.doIfLogin
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.FairgroundViewModel

/**
 *
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/26 14:59
 */
@Route(path = FAIRGROUND_ENTRY)
class FairgroundPageFragment : BaseFragment(R.layout.main_fragment_fairground) {


    private val tvDays by R.id.main_tv_days.view<TextView>()
    private val tvNickname by R.id.tv_nickname.view<TextView>()
    private val ivHead by R.id.main_iv_head.view<ImageView>()
    private val startActivity by R.id.main_fairground_activity.view<ConstraintLayout>()
    private val startFood by R.id.main_fairground_food.view<ConstraintLayout>()
    private val startSquare by R.id.main_fairground_square.view<ConstraintLayout>()

    val viewModel by viewModels<FairgroundViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startActivity.setOnClickListener {
            doIfLogin {
                ServiceManager.activity(UFIELD_MAIN_ENTRY)
            }
        }
        startFood.setOnClickListener {
            doIfLogin {
                ServiceManager.activity(FOOD_ENTRY)
            }
        }
        startSquare.setOnClickListener {
            doIfLogin {
                ServiceManager.activity(DECLARE_ENTRY)
            }
        }
        viewModel.days.observe(viewLifecycleOwner) {
                val text = "这是你来到邮乐园的第 $it 天"

                val spannableStringBuilder = SpannableStringBuilder(text)
                val startIndex = text.indexOf(it)
                val endIndex = startIndex + it.length

                // 设置加粗样式
                spannableStringBuilder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 设置字体大小
                spannableStringBuilder.setSpan(
                    AbsoluteSizeSpan(20, true),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 设置字体颜色
                spannableStringBuilder.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5D5DF7")),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 将 spannableStringBuilder 应用到文本视图
                tvDays.text = spannableStringBuilder
        }
        viewModel.message.observe(viewLifecycleOwner) {
            if (it != null) {
                tvNickname.text = "Hi, ${it.nickname}"
                ivHead.setAvatarImageFromUrl(it.photo_src)
            }
        }

    }
}