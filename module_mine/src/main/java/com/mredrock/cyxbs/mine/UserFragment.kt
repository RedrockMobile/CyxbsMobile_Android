package com.mredrock.cyxbs.mine


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.main.MAIN_LOGIN
import com.mredrock.cyxbs.mine.page.about.AboutActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.setting.SettingActivity
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import kotlinx.android.synthetic.main.mine_fragment_main.*

/**
 * Created by zzzia on 2018/8/14.
 * 我的 主界面Fragment
 */
@SuppressLint("SetTextI18n")
@Route(path = MINE_ENTRY)
class UserFragment : BaseViewModelFragment<UserViewModel>() {
    override var TAG = "RayleighZ"

    override var isOpenLifeCycleLog: Boolean
        get() = true
        set(value) {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addObserver()
        initView()
//        val token = ServiceManager.getService(IAccountService::class.java).getUserTokenService().getToken()
//        LogUtils.d("RayleighZ", token)
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
            mine_main_cl_info_edit.setOnClickListener {
                doIfLogin {
                    startActivity(
                            Intent(context, EditInfoActivity::class.java),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, Pair(mine_main_avatar, "avatar")).toBundle())
                }
            }
//            mine_main_tv_about.setOnClickListener { startActivity<AboutActivity>() }
//            if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
//                mine_main_btn_exit.text = getString(R.string.mine_exit)
//                mine_main_btn_exit.setOnClickListener {
//                    onExitClick()
//                }
//            } else {
//                mine_main_btn_exit.text = getString(R.string.mine_login_now)
//                mine_main_btn_exit.setOnClickListener {
//                    cleanAppWidgetCache()
//                    //清除user信息，必须要在LoginStateChangeEvent之前
//                    viewModel.clearUser()
//                    requireActivity().startLoginActivity()
//                }
//            }
//            mine_main_btn_exit.pressToZoomOut()
//            mine_main_tv_feedback.setOnClickListener { onFeedBackClick() }
//            mine_main_tv_custom_widget.setOnClickListener { onSetWidgetClick() }
//            mine_main_tv_redrock.setOnClickListener { clickAboutUsWebsite() }
//
//            mine_main_switch.setOnCheckedChangeListener { _, isChecked ->
//                defaultSharedPreferences.editor {
//                    if (isChecked) {
//                        putBoolean(COURSE_SHOW_STATE, true)
//                    } else {
//                        putBoolean(COURSE_SHOW_STATE, false)
//                    }
//                }
//            }
//            mine_main_switch.isChecked = context?.defaultSharedPreferences?.getBoolean(COURSE_SHOW_STATE, false)
//                    ?: false

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
        viewModel.userCount.observe(viewLifecycleOwner, Observer { it ->
            //可能会出现部分number为负数的情况，客户端需要处理（虽然是后端的锅）
            mine_main_tv_dynamic_number.text = viewModel.getNumber(it.dynamicCount)
            mine_main_tv_comment_number.text = viewModel.getNumber(it.commentCount)
            mine_main_tv_praise_number.text = viewModel.getNumber(it.praiseCount)
            //由于视觉给的字体不是等宽的，其中数字"1"的宽度明显小于其他数字的宽度，要对此进行单独的处理
            //基本规则是：基础距离是17dp，非1字体+15dp，1则+12dp
            viewModel.setLeftMargin(mine_main_tv_uncheck_comment_count, it.commentCount)
            viewModel.setLeftMargin(mine_main_tv_uncheck_praise_count, it.praiseCount)
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 200
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { va ->
                mine_main_tv_dynamic_number.scaleY = va.animatedValue as Float
                mine_main_tv_comment_number.scaleY = va.animatedValue as Float
                mine_main_tv_praise_number.scaleY = va.animatedValue as Float
            }
            animator.start()
            //在这里再请求unChecked的红点仅仅是为了好看，让动画显得更加流畅
            viewModel.getUserUncheckCount(1)
            viewModel.getUserUncheckCount(2)
        })

        viewModel.userUncheckCount.observe(viewLifecycleOwner, Observer {
            fun setViewWidthAndText(textView: TextView, count: Int) {
                LogUtils.d(TAG, "count = $count")
                if (count == 0) {
                    //如果当前的数值已经归零，就不操作了
                    if (textView.text == "0") return
                    textView.text = "0"
                    //加上一个逐渐变大弹出的动画
                    val animator = ValueAnimator.ofFloat(1f, 0f)
                    animator.duration = 200
                    animator.addUpdateListener { va ->
                        textView.scaleX = va.animatedValue as Float
                        textView.scaleY = va.animatedValue as Float
                    }
                    animator.interpolator = DecelerateInterpolator()
                    animator.start()
                    return
                }
                //如果前后数字没有变化就不进行刷新
                if (textView.text == count.toString()) return
                textView.visibility = View.VISIBLE
                textView.text = viewModel.getNumber(count)
                val width = when {
                    count > 99 -> {
                        26.5f
                    }
                    count in 10..99 -> {
                        21.3f
                    }
                    else -> {
                        16f
                    }
                }
                val lp = textView.layoutParams as ConstraintLayout.LayoutParams
                lp.width = BaseApp.context.dip(width)
                textView.layoutParams = lp
                //加上一个逐渐变大弹出的动画
                val animator = ValueAnimator.ofFloat(0f, 1f)
                animator.duration = 200
                animator.addUpdateListener { va ->
                    textView.scaleX = va.animatedValue as Float
                    textView.scaleY = va.animatedValue as Float
                }
                animator.interpolator = DecelerateInterpolator()
                animator.start()
            }

            it.uncheckPraiseCount?.let { uncheckPraise -> setViewWidthAndText(mine_main_tv_uncheck_praise_count, uncheckPraise) }
            it.uncheckCommentCount?.let { uncheckComment -> setViewWidthAndText(mine_main_tv_uncheck_comment_count, uncheckComment) }
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
        viewModel.getQANumber()
        viewModel.getUserCount()
        viewModel.getUserUncheckCount(1)
        viewModel.getUserUncheckCount(2)
        refreshUserLayout()
    }

    //刷新和User信息有关的界面
    private fun refreshUserLayout() {
        val userService = ServiceManager.getService(IAccountService::class.java).getUserService()
        context?.loadAvatar(userService.getAvatarImgUrl(), mine_main_avatar)
        mine_main_username.text = if (userService.getNickname().isBlank()) getString(R.string.mine_user_empty_username) else userService.getNickname()
        mine_main_introduce.text = if (userService.getIntroduction().isBlank()) getString(R.string.mine_user_empty_introduce) else userService.getIntroduction()
//        mine_main_btn_exit.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.mine_fragment_main, container, false)

    private fun onSetWidgetClick() {
        ARouter.getInstance().build(WIDGET_SETTING).navigation()
    }


    private fun onExitClick() {
        val tag = "exit"
        activity?.let { act ->
            if (act.supportFragmentManager.findFragmentByTag(tag) == null) {
                CommonDialogFragment().apply {
                    initView(
                            containerRes = R.layout.mine_layout_dialog_logout,
                            onPositiveClick = {
                                cleanAppWidgetCache()
                                //清除user信息，必须要在LoginStateChangeEvent之前
                                viewModel.clearUser()
                                //清空activity栈
                                val flag = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                ARouter.getInstance().build(MAIN_LOGIN).withFlags(flag).withBoolean(IS_EXIT_LOGIN, true).navigation()
                            },
                            positiveString = "退出",
                            onNegativeClick = { dismiss() }
                    )
                }.show(act.supportFragmentManager, tag)
            }

        }
    }

    private fun showPraise() {
        val tag = "praise"
        activity?.let { act ->
            if (act.supportFragmentManager.findFragmentByTag(tag) == null) {
                CommonDialogFragment().apply {
                    initView(
                            containerRes = R.layout.mine_layout_dialog_praise,
                            onPositiveClick = { dismiss() },
                            positiveString = "确定",
                            elseFunction = { view ->
                                view.findViewById<TextView>(R.id.mine_dialog_tv_praise).text = "你一共获得${
                                    viewModel.qaNumber.value?.praiseNumber
                                            ?: 0
                                }个赞"
                            }
                    )
                }.show(act.supportFragmentManager, tag)
            }

        }

    }

    private fun cleanAppWidgetCache() {
        context?.defaultSharedPreferences?.editor {
            putString(WIDGET_COURSE, "")
            putBoolean(SP_WIDGET_NEED_FRESH, true)
        }
    }

    private fun clickAboutUsWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_US_WEBSITE))
        startActivity(intent)
    }

    private fun jump(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    private fun jumpAndSaveTime(path: String, type: Int){
        viewModel.saveCheckTimeStamp(type)
        jump(path)
    }
}