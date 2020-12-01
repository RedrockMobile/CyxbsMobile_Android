package com.mredrock.cyxbs.mine


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.main.MAIN_LOGIN
import com.mredrock.cyxbs.mine.page.about.AboutActivity
import com.mredrock.cyxbs.mine.page.answer.AnswerActivity
import com.mredrock.cyxbs.mine.page.ask.AskActivity
import com.mredrock.cyxbs.mine.page.comment.CommentActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.security.activity.SecurityActivity
import com.mredrock.cyxbs.mine.page.security.util.Jump2QQHelper
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import kotlinx.android.synthetic.main.mine_fragment_main.*

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
            mine_main_tv_security.setOnSingleClickListener { doIfLogin { startActivity<SecurityActivity>() } }
            mine_main_btn_sign.setOnClickListener { doIfLogin { startActivity<DailySignActivity>() } }
            mine_main_tv_sign.setOnClickListener { doIfLogin { startActivity<DailySignActivity>() } }
            mine_main_question_number.setOnClickListener { doIfLogin { startActivity<AskActivity>() } }
            mine_main_tv_question.setOnClickListener { doIfLogin { startActivity<AskActivity>() } }
            mine_main_answer_number.setOnClickListener { doIfLogin { startActivity<AnswerActivity>() } }
            mine_main_tv_question.setOnClickListener { doIfLogin { startActivity<AnswerActivity>() } }
            mine_main_reply_comment_number.setOnClickListener { doIfLogin { startActivity<CommentActivity>() } }
            mine_main_tv_reply_comment.setOnClickListener { doIfLogin { startActivity<CommentActivity>() } }
            mine_main_cl_info_edit.setOnClickListener {
                doIfLogin {
                    startActivity(
                            Intent(context, EditInfoActivity::class.java),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, Pair(mine_main_avatar, "avatar")).toBundle())
                }
            }

            mine_main_tv_praise.setOnClickListener { doIfLogin { showPraise() } }
            mine_main_praise_number.setOnClickListener { doIfLogin { showPraise() } }

            mine_main_tv_about.setOnClickListener { startActivity<AboutActivity>() }
            if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
                mine_main_btn_exit.text = getString(R.string.mine_exit)
                mine_main_btn_exit.setOnClickListener {
                    onExitClick()
                }
            } else {
                mine_main_btn_exit.text = getString(R.string.mine_login_now)
                mine_main_btn_exit.setOnClickListener {
                    cleanAppWidgetCache()
                    //清除user信息，必须要在LoginStateChangeEvent之前
                    viewModel.clearUser()
                    requireActivity().startLoginActivity()
                }
            }
            mine_main_btn_exit.pressToZoomOut()
            mine_main_tv_feedback.setOnClickListener { Jump2QQHelper.onFeedBackClick(this) }
            mine_main_tv_custom_widget.setOnClickListener { onSetWidgetClick() }
            mine_main_tv_redrock.setOnClickListener { clickAboutUsWebsite() }

            mine_main_switch.setOnCheckedChangeListener { _, isChecked ->
                defaultSharedPreferences.editor {
                    if (isChecked) {
                        putBoolean(COURSE_SHOW_STATE, true)
                    } else {
                        putBoolean(COURSE_SHOW_STATE, false)
                    }
                }
            }
            mine_main_switch.isChecked = context?.defaultSharedPreferences?.getBoolean(COURSE_SHOW_STATE, false)
                    ?: false
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
        viewModel.qaNumber.observe(viewLifecycleOwner, Observer {
            //可能会出现部分number为负数的情况，客户端需要处理（虽然是后端的锅）
            fun getNumber(number: Int): String = if (number >= 0) number.toString() else "0"
            mine_main_question_number.text = getNumber(it.askPostedNumber)
            mine_main_answer_number.text = getNumber(it.answerPostedNumber)
            mine_main_reply_comment_number.text = getNumber(it.commentNumber)
            mine_main_praise_number.text = getNumber(it.praiseNumber)
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
        refreshUserLayout()
    }

    //刷新和User信息有关的界面
    private fun refreshUserLayout() {
        val userService = ServiceManager.getService(IAccountService::class.java).getUserService()
        context?.loadAvatar(userService.getAvatarImgUrl(), mine_main_avatar)
        mine_main_username.text = if (userService.getNickname().isBlank()) getString(R.string.mine_user_empty_username) else userService.getNickname()
        mine_main_introduce.text = if (userService.getIntroduction().isBlank()) getString(R.string.mine_user_empty_introduce) else userService.getIntroduction()
        mine_main_btn_exit.visibility = View.VISIBLE
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
}