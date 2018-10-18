package com.mredrock.cyxbs.mine


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.config.MINE_ENTRY
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.loadAvatar
import com.mredrock.cyxbs.mine.page.aboutme.AboutMeActivity
import com.mredrock.cyxbs.mine.page.ask.AskActivity
import com.mredrock.cyxbs.mine.page.draft.DraftActivity
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.help.HelpActivity
import com.mredrock.cyxbs.mine.page.setting.SettingActivity
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import com.mredrock.cyxbs.mine.page.store.StoreActivity
import com.mredrock.cyxbs.mine.util.user
import kotlinx.android.synthetic.main.mine_fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

/**
 * Created by zzzia on 2018/8/14.
 * 我的 主界面Fragment
 */
@Route(path = MINE_ENTRY)
class UserFragment : BaseViewModelFragment<UserViewModel>() {
    override val viewModelClass: Class<UserViewModel>
        get() = UserViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mine_main_toolbar.title = "我的"

        addObserver()

        //加载资料
        getPersonInfoData()

        //功能按钮
//        mine_main_dailySign.setOnClickListener { checkLoginBeforeAction("签到") { startActivity<DailySignActivity>() } }
        mine_main_dailySign.setOnClickListener {
                startActivity(Intent(context,DailySignActivity::class.java))
        }
        mine_main_store.setOnClickListener { checkLoginBeforeAction("商店") { startActivity<StoreActivity>() } }
        mine_main_question.setOnClickListener { checkLoginBeforeAction("问一问") { startActivity<AskActivity>() } }
        mine_main_help.setOnClickListener { checkLoginBeforeAction("帮一帮") { startActivity<HelpActivity>() } }
        mine_main_draft.setOnClickListener { checkLoginBeforeAction("草稿箱") { startActivity<DraftActivity>() } }
        mine_main_relateMe.setOnClickListener { checkLoginBeforeAction("与我相关") { startActivity<AboutMeActivity>() } }
        mine_main_setting.setOnClickListener { startActivity<SettingActivity>() }

        //个人资料按钮，没有加载完资料之前不能跳转到更改信息页面
        setUserInfoClickListener(View.OnClickListener {
            if (user == null){
                toast("请先登录")
            }else{
                toast("正在加载资料，请稍后")
            }
        })
    }

    private fun addObserver() {
        viewModel.mUser.observe(this, Observer {
            if (it == null) return@Observer
            freshBaseUser(it)
            refreshEditLayout()
            setUserInfoClickListener(View.OnClickListener { _ ->
                startActivity<EditInfoActivity>()
            })
        })
    }

    /**
     * 更新数据，加载详细资料，加载完后进入编辑页面
     */
    private fun loadInfoAndGoEdit() {
        checkLoginBeforeAction("个人资料") { viewModel.getUserInfo() }
    }

    private fun freshBaseUser(user: User) {
        val finalUser = BaseApp.user!!
        finalUser.nickname = user.nickname
        finalUser.introduction = user.introduction
        finalUser.qq = user.qq
        finalUser.phone = user.phone
        finalUser.photoSrc = user.photoSrc
        finalUser.photoThumbnailSrc = user.photoThumbnailSrc
        BaseApp.user = finalUser
    }

    private fun setUserInfoClickListener(onClickListener: View.OnClickListener) {
        mine_main_avatar.setOnClickListener(onClickListener)
        mine_main_username.setOnClickListener(onClickListener)
        mine_main_introduce.setOnClickListener(onClickListener)
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (BaseApp.isLogin) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能查看${msg}哦~"))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshEditLayout()
    }

    private fun getPersonInfoData() {
        if (!BaseApp.isLogin) {
            mine_main_username.setText(R.string.mine_user_empty_username)
            mine_main_avatar.setImageResource(R.drawable.mine_default_avatar)
            mine_main_introduce.setText(R.string.mine_user_empty_introduce)
            clearAllRemind()
            return
        } else {
            loadInfoAndGoEdit()
        }
    }

    private fun clearAllRemind() {
        mine_main_dailySign.isRemindIconShowing = false
        mine_main_question.isRemindIconShowing = false
        mine_main_help.isRemindIconShowing = false
        mine_main_relateMe.isRemindIconShowing = false
        mine_main_setting.isRemindIconShowing = false
    }


    private fun refreshEditLayout() {
        if (BaseApp.isLogin) {
            context?.loadAvatar(user!!.photoThumbnailSrc, mine_main_avatar)
            mine_main_username.text = if (user!!.nickname!!.isBlank()) getString(R.string.mine_user_empty_username) else user!!.nickname
            mine_main_introduce.text = if (user!!.introduction!!.isBlank()) getString(R.string.mine_user_empty_introduce) else user!!.introduction
        } else {
            mine_main_username.setText(R.string.mine_user_empty_username)
            mine_main_avatar.setImageResource(R.drawable.mine_default_avatar)
            mine_main_introduce.setText(R.string.mine_user_empty_introduce)
        }
    }

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        refreshEditLayout()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.mine_fragment_main, container, false)
    }
}
