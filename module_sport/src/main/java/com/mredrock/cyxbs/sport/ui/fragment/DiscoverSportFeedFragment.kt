package com.mredrock.cyxbs.sport.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.config.route.DISCOVER_SPORT
import com.mredrock.cyxbs.config.route.DISCOVER_SPORT_FEED
import com.mredrock.cyxbs.config.route.LOGIN_BIND_IDS
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmBindFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.sport.R
import com.mredrock.cyxbs.sport.databinding.SportFragmentDiscoverFeedBinding
import com.mredrock.cyxbs.sport.ui.viewmodel.DiscoverSportFeedViewModel
import com.mredrock.cyxbs.sport.util.sSpIdsIsBind

/**
 * @author : why
 * @time   : 2022/8/12 17:11
 * @bless  : God bless my code
 */
@Route(path = DISCOVER_SPORT_FEED)
class DiscoverSportFeedFragment :
    BaseVmBindFragment<DiscoverSportFeedViewModel, SportFragmentDiscoverFeedBinding>(),
    IUserStateService.StateListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * 在这里注册登录状态改变的监听，当登录状态改变时，需要刷新界面（但请记得在onDestroyView方法中移除[onDestroyView]）
         */
        IAccountService::class.impl
            .getVerifyService()
            .addOnStateChangedListener(this)

        binding.sportIvFeedTips.setOnClickListener {
            MaterialDialog(requireActivity()).show {
                customView(R.layout.sport_dialog_feed)
                getCustomView().apply {
                    findViewById<Button>(R.id.sport_btn_feed_dialog_confirm).setOnClickListener {
                        dismiss()
                    }
                }
                cornerRadius(8f)
            }
        }
        //出错后弹出提示
        viewModel.isError.observe() {
            if (it) {
                binding.run {
                    //隐藏用于显示数据的控件
                    sportTvFeedRunNeed.gone()
                    sportTvFeedRunTimes.gone()
                    sportTvFeedOtherNeed.gone()
                    sportTvFeedOtherTimes.gone()
                    sportTvFeedAward.gone()
                    sportTvFeedAwardTimes.gone()
                    sportTvFeedRunNeedHint.gone()
                    sportTvFeedOtherNeedHint.gone()
                    sportTvFeedAwardHint.gone()
                    sportTvFeedHint.text = "当前数据错误，正在努力修复中"
                    sportTvFeedHint.visible()
                }
            }
        }
        //监听绑定ids的状态，存入SharePreference中
        viewModel.isBind.observe() {
            if (!it) {
                sSpIdsIsBind = false
                unbound()
            } else {
                sSpIdsIsBind = true
            }
        }
        //进入首页后对登录和绑定状态进行判断
        if (!IAccountService::class.impl.getVerifyService().isLogin()) {
            //游客模式则不显示数据，显示需要先登录
            binding.run {
                //隐藏用于显示数据的控件
                sportTvFeedRunNeed.gone()
                sportTvFeedRunTimes.gone()
                sportTvFeedOtherNeed.gone()
                sportTvFeedOtherTimes.gone()
                sportTvFeedAward.gone()
                sportTvFeedAwardTimes.gone()
                sportTvFeedRunNeedHint.gone()
                sportTvFeedOtherNeedHint.gone()
                sportTvFeedAwardHint.gone()
                sportTvFeedHint.text = "登录后才能查看体育打卡哦"
                sportTvFeedHint.visible()
            }
        } else {
            //登录后检查是否绑定了ids，如果没有绑定则显示需要绑定
            if (!sSpIdsIsBind) {
                unbound()
            } else {
                showData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        IAccountService::class.impl
            .getVerifyService()
            .removeStateChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshSportData()
    }

    override fun onStateChanged(state: IUserStateService.UserState) {
        viewModel.refreshSportData()
    }

    //未绑定ids
    private fun unbound() {
        binding.run {
            //隐藏用于显示数据的控件
            sportTvFeedRunNeed.gone()
            sportTvFeedRunTimes.gone()
            sportTvFeedOtherNeed.gone()
            sportTvFeedOtherTimes.gone()
            sportTvFeedAward.gone()
            sportTvFeedAwardTimes.gone()
            sportTvFeedRunNeedHint.gone()
            sportTvFeedOtherNeedHint.gone()
            sportTvFeedAwardHint.gone()
            //设置提示
            val ssb = SpannableStringBuilder()
            ssb.append("查询失败，请先绑定 教务在线 后再试")
            //设置”教务在线“字体为所需蓝色
            val colorSpan = ForegroundColorSpan(Color.parseColor("#4a44e4"))
            ssb.setSpan(colorSpan, 10, 14, SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE)
            //设置下划线
            val uls = UnderlineSpan()
            ssb.setSpan(uls, 10, 14, SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE)
            sportTvFeedHint.text = ssb
            sportTvFeedHint.visible()
            //设置点击跳转教务在线登录界面
            sportClFeed.setOnClickListener {
                ARouter.getInstance().build(LOGIN_BIND_IDS).navigation()
            }
        }
    }

    //展示数据
    private fun showData() {
        binding.run {
            //隐藏提示
            sportTvFeedHint.gone()
            //显示数据
            sportTvFeedRunNeed.visible()
            sportTvFeedRunTimes.visible()
            sportTvFeedOtherNeed.visible()
            sportTvFeedOtherTimes.visible()
            sportTvFeedAward.visible()
            sportTvFeedAwardTimes.visible()
            sportTvFeedRunNeedHint.visible()
            sportTvFeedOtherNeedHint.visible()
            sportTvFeedAwardHint.visible()
            viewModel.sportData.observe(viewLifecycleOwner) {
                //跑步剩余次数 = 所需跑步次数 - 已跑步次数， 剩余次数需要 >= 0
                sportTvFeedRunNeed.text =
                    if (it.runTotal - it.runDone >= 0) (it.runTotal - it.runDone).toString() else "0"
                //其他 剩余次数
                val other = if (it.runTotal - it.runDone > 0) {
                    //剩余跑步次数大于零时，其他次数 = 所需其他次数 - 已打卡次数
                    it.otherTotal - it.otherDone
                } else {
                    //剩余跑步次数 <= 0 时，其他次数 = 所需总次数 - 已打卡其他次数 - 已跑步次数
                    (it.runTotal + it.otherTotal) - it.otherDone - it.runDone
                }
                //其他剩余次数必须 >= 0
                sportTvFeedOtherNeed.text = if (other >= 0) other.toString() else "0"
                //奖励次数
                sportTvFeedAward.text = it.award.toString()
            }
            //设置点击跳转进详情页
            sportClFeed.setOnClickListener {
                ARouter.getInstance().build(DISCOVER_SPORT).navigation()
            }
        }
    }
}