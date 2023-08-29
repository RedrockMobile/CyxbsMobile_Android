package com.mredrock.cyxbs.main.ui.fairground

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.mredrock.cyxbs.lib.utils.extensions.setAvatarImageFromUrl
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.FAIRGROUND_ENTRY
import com.mredrock.cyxbs.config.route.UFIELD_ACTIVITY
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
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
    private val ivActivity by R.id.main_fairground_iv_dctivity.view<ImageView>()
    private val btActivity by R.id.main_fairground_bt_dctivity.view<Button>()
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel by viewModels<FairgroundViewModel>()
        viewModel.days.observe(viewLifecycleOwner) {
            ivActivity.setOnClickListener {
                ServiceManager.activity(UFIELD_ACTIVITY)
            }
            btActivity.setOnClickListener {
                ServiceManager.activity(UFIELD_ACTIVITY)
            }
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

        viewModel.message.observe(viewLifecycleOwner){
            if (it != null) {
                tvNickname.text="Hi, ${it.nickname}"
                ivHead.setAvatarImageFromUrl(it.photo_src)
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}