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
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp.appContext
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.page.about.AboutActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.feedback.center.ui.FeedbackCenterActivity
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.setting.SettingActivity
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import com.ndhzs.api.store.IStoreService
import kotlinx.android.synthetic.main.mine_fragment_main_new.*

/**
 * Created by zzzia on 2018/8/14.
 * 我的 主界面Fragment
 * 这个类的代码不要格式化了吧 否则initView里面的代码会很凌乱
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
            mine_user_tv_dynamic_number.setOnSingleClickListener { doIfLogin { jump(QA_DYNAMIC_MINE) } }
            mine_user_tv_dynamic.setOnSingleClickListener { doIfLogin { jump(QA_DYNAMIC_MINE) } }
            mine_user_ib_arrow.setOnSingleClickListener { doIfLogin { HomepageActivity.startHomePageActivity(null,context as Activity) } }
            mine_user_tv_comment_number.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_COMMENT, 1) } }
            mine_user_tv_comment.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_COMMENT, 1) } }
            mine_user_tv_praise_number.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_PRAISE, 2) } }
            mine_user_tv_praise.setOnSingleClickListener { doIfLogin { jumpAndSaveTime(QA_MY_PRAISE, 2) } }
            mine_user_iv_center_stamp.setOnSingleClickListener { doIfLogin { jump(STORE_ENTRY) } }
            mine_user_iv_center_feedback.setOnSingleClickListener { doIfLogin { startActivity(Intent(this,FeedbackCenterActivity::class.java)) } }

            mine_user_tv_sign.setOnSingleClickListener { doIfLogin { startActivity<DailySignActivity>() } }
            mine_user_btn_sign.setOnSingleClickListener { doIfLogin { startActivity<DailySignActivity>() } }

            mine_user_fm_about_us.setOnSingleClickListener { doIfLogin { startActivity<AboutActivity>() } }
            mine_user_fm_setting.setOnSingleClickListener { doIfLogin { startActivity<SettingActivity>() } }
            mine_user_cl_info.setOnSingleClickListener { doIfLogin { HomepageActivity.startHomePageActivity(null,context as Activity) }
            }

            mine_user_iv_center_notification.setOnSingleClickListener { toast(R.string.mine_person_empty_notification) }
            mine_user_tv_center_notification.setOnSingleClickListener { toast(R.string.mine_person_empty_notification) }

            mine_user_avatar.setOnSingleClickListener {
                doIfLogin {
                    startActivity(
                        Intent(
                            context,
                            EditInfoActivity::class.java
                        ),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            Pair(mine_user_avatar, "avatar")
                        ).toBundle()
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addObserver() {
        viewModel.status.observe(viewLifecycleOwner, Observer {
            mine_user_tv_sign.text = "已连续签到 ${it.serialDays} 天 "
            if (it.isChecked) {
                mine_user_btn_sign.apply {
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.mine_bg_round_corner_grey,
                        null
                    )
                    text = "已签到"
                    setTextColor(ContextCompat.getColor(context, com.mredrock.cyxbs.common.R.color.common_grey_button_text))
                }
            } else {
                mine_user_btn_sign.apply {
                    text = "签到"
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.mine_shape_bg_user_btn_sign,
                        null
                    )
                    setTextColor(ContextCompat.getColor(context, com.mredrock.cyxbs.common.R.color.common_white_font_color))
                }
            }
        })
        viewModel.userCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                //可能会出现部分number为负数的情况，客户端需要处理（虽然是后端的锅）
                Log.d("TAG","(UserFragment.kt:122)->${it.dynamicCount},${it.commentCount},${it.praiseCount}")
                viewModel.judgeChangedAndSetText(mine_user_tv_dynamic_number, it.dynamicCount)
                viewModel.judgeChangedAndSetText(mine_user_tv_comment_number, it.commentCount)
                viewModel.judgeChangedAndSetText(mine_user_tv_praise_number, it.praiseCount)
//                //由于视觉给的字体不是等宽的，其中数字"1"的宽度明显小于其他数字的宽度，要对此进行单独的处理
//                //基本规则是：基础距离是17dp，非1字体+15dp，1则+12dp
//                viewModel.setLeftMargin(mine_main_tv_uncheck_comment_count, it.commentCount)
//                viewModel.setLeftMargin(mine_main_tv_uncheck_praise_count, it.praiseCount)
                //在这里再请求unChecked的红点仅仅是为了好看，让动画显得更加流畅
                viewModel.getUserUncheckedCommentCount()
                viewModel.getUserUncheckedPraiseCount()
            }
        })

        viewModel.userUncheckCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.uncheckPraiseCount?.let { uncheckPraise ->
                    viewModel.setViewWidthAndText(
                        mine_user_tv_unchecked_notification_count,
                        99
                    )

                    viewModel.setViewWidthAndText(
                        mine_user_tv_unchecked_praise,
                        uncheckPraise
                    )
                }
                it.uncheckCommentCount?.let { uncheckComment ->
                    viewModel.setViewWidthAndText(
                        mine_user_tv_unchecked_comment,
                        uncheckComment
                    )
                }
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
        context?.loadAvatar(userService.getAvatarImgUrl(), mine_user_avatar)
        mine_user_username.text =
            if (userService.getNickname().isBlank()) appContext.getString(R.string.mine_user_empty_username)
            else userService.getNickname()
        mine_user_introduce.text =
            if (userService.getIntroduction().isBlank()) appContext.getString(R.string.mine_user_empty_introduce)
            else userService.getIntroduction()

        if (userService.getNickname().isNotBlank() &&
            userService.getIntroduction().isNotBlank() &&
            userService.getQQ().isNotBlank() &&
            userService.getPhone().isNotBlank()
        ) {
            // 当都不为空时, 说明已经设置了个人信息, 则提交积分商城任务进度, 后端已做重复处理
            ServiceManager.getService(IStoreService::class.java)
                .postTask(IStoreService.Task.EDIT_INFO, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.mine_fragment_main_new, container, false)

    private fun jump(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    private fun jumpAndSaveTime(path: String, type: Int) {
        viewModel.saveCheckTimeStamp(type)
        jump(path)
    }
}