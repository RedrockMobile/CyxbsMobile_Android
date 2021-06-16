package com.mredrock.cyxbs.mine


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.page.about.AboutActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.security.util.Jump2QQHelper
import com.mredrock.cyxbs.mine.page.setting.SettingActivity
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import kotlinx.android.synthetic.main.mine_fragment_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by zzzia on 2018/8/14.
 * 我的 主界面Fragment
 */
@SuppressLint("SetTextI18n")
@Route(path = MINE_ENTRY)
class UserFragment : BaseViewModelFragment<UserViewModel>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addObserver()
        initView()
    }

    private fun initView() {
        //功能按钮
        context?.apply {
            mine_main_btn_sign.setOnClickListener { doIfLogin { startActivity<DailySignActivity>() } }
            mine_main_btn_sign.setOnClickListener { doIfLogin { startActivity<DailySignActivity>() } }
            mine_main_fm_setting.setOnSingleClickListener { doIfLogin { startActivity<SettingActivity>() } }
            mine_main_fm_about_us.setOnSingleClickListener { doIfLogin { startActivity<AboutActivity>() } }
            mine_main_fm_point_store.setOnClickListener { doIfLogin { DailySignActivity.actionStart(this, BottomSheetBehavior.STATE_EXPANDED) } }
            mine_main_tv_sign.setOnClickListener { doIfLogin { DailySignActivity.actionStart(this, BottomSheetBehavior.STATE_COLLAPSED) } }
            mine_main_tv_dynamic_number.setOnSingleClickListener { doIfLogin { jump(QA_DYNAMIC_MINE) } }
            mine_main_tv_dynamic.setOnSingleClickListener { doIfLogin { jump(QA_DYNAMIC_MINE) } }
            mine_main_tv_comment_number.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_COMMENT, 1) } }
            mine_main_tv_praise_number.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_PRAISE, 2) } }
            mine_main_tv_comment.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_COMMENT, 1) } }
            mine_main_tv_praise.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_PRAISE, 2) } }
            mine_main_fm_feedback.setOnSingleClickListener { doIfLogin { Jump2QQHelper.onFeedBackClick(this) } }
            mine_main_cl_info_edit.setOnClickListener {
                doIfLogin {
                    startActivity(
                            Intent(context, EditInfoActivity::class.java),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, Pair(mine_main_avatar, "avatar")).toBundle())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addObserver() {
        viewModel.status.observe(viewLifecycleOwner, Observer {
            mine_main_tv_sign.text = "已连续签到${it.serialDays}天 "
            if (it.isChecked) {
                mine_main_btn_sign.apply {
                    background = ResourcesCompat.getDrawable(resources, R.drawable.mine_bg_round_corner_grey, null)
                    text = "已签到"
                    setTextColor(ContextCompat.getColor(context, R.color.common_grey_button_text))
                }
            } else {
                mine_main_btn_sign.apply {
                    text = "签到"
                    background = ResourcesCompat.getDrawable(resources, R.drawable.common_dialog_btn_positive_blue, null)
                    setTextColor(ContextCompat.getColor(context, R.color.common_white_font_color))
                }
            }
        })
        viewModel.userCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                //可能会出现部分number为负数的情况，客户端需要处理（虽然是后端的锅）
                viewModel.judgeChangedAndSetText(mine_main_tv_dynamic_number, it.dynamicCount)
                viewModel.judgeChangedAndSetText(mine_main_tv_comment_number, it.commentCount)
                viewModel.judgeChangedAndSetText(mine_main_tv_praise_number, it.praiseCount)
                //由于视觉给的字体不是等宽的，其中数字"1"的宽度明显小于其他数字的宽度，要对此进行单独的处理
                //基本规则是：基础距离是17dp，非1字体+15dp，1则+12dp
                viewModel.setLeftMargin(mine_main_tv_uncheck_comment_count, it.commentCount)
                viewModel.setLeftMargin(mine_main_tv_uncheck_praise_count, it.praiseCount)
                //在这里再请求unChecked的红点仅仅是为了好看，让动画显得更加流畅
                viewModel.getUserUncheckCount(1)
                viewModel.getUserUncheckCount(2)
            }
        })

        viewModel.userUncheckCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.uncheckPraiseCount?.let { uncheckPraise -> viewModel.setViewWidthAndText(mine_main_tv_uncheck_praise_count, uncheckPraise) }
                it.uncheckCommentCount?.let { uncheckComment -> viewModel.setViewWidthAndText(mine_main_tv_uncheck_comment_count, uncheckComment) }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            fetchInfo()
        }
    }

    private fun fetchInfo() {
        viewModel.getScoreStatus()
        viewModel.getUserCount()
        refreshUserLayout()
    }

    //刷新和User信息有关的界面
    private fun refreshUserLayout() {
        val userService = ServiceManager.getService(IAccountService::class.java).getUserService()
        context?.loadAvatar(userService.getAvatarImgUrl(), mine_main_avatar)
        mine_main_username.text = if (userService.getNickname().isBlank()) getString(R.string.mine_user_empty_username) else userService.getNickname()
        mine_main_introduce.text = if (userService.getIntroduction().isBlank()) getString(R.string.mine_user_empty_introduce) else userService.getIntroduction()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.mine_fragment_main, container, false)

    private fun jump(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    private fun jumpAndSaveTime(path: String, type: Int){
        viewModel.saveCheckTimeStamp(type)
        jump(path)
    }
}