package com.mredrock.cyxbs.affair.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.fragment.NoClassAffairFragment
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.NoClassAffairActivityViewModel
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.config.route.NOTIFICATION_HOME
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager

class NoClassAffairActivity : BaseActivity() {

    companion object {
        /**
         *  没课约的专属跳转方法
         *  @param noClassBean : 学号：是否空闲
         */
        fun startForNoClass(noClassBean: NoClassBean) {
            Log.d("lx", "startForNoClass:${noClassBean} ")
            appContext.startActivity(
                Intent(appContext, NoClassAffairActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(NoClassAffairActivity::mParams.name, noClassBean)
            )
        }
    }

    // 启动参数
    private val mParams by intent<NoClassBean>()

    private val mViewModel by viewModels<NoClassAffairActivityViewModel>()

    // 返回键
    private val mBtnBack: ImageButton by R.id.affair_btn_edit_affair_back.view()

    // 没课越设置的下一项
    private val mBtnNoClassNext: Button by R.id.affair_btn_noclass_affair_next.view()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.affair_activity_noclass)
        initObserve()
        initClick()
        initFragment()
    }

    /**
     * 目前没课越专属使用,用于更改按钮的背景
     */
    private fun initObserve() {
        mViewModel.changeBtn.collectLaunch {
            when (it) {
                1 -> {
                    mBtnNoClassNext.setBackgroundResource(R.drawable.affair_ic_next_process_positive)
                }

                2 -> {
                    mBtnNoClassNext.apply {
                        setBackgroundResource(R.drawable.affair_ic_send_notification)
                        text = "发送通知"
                    }
                }

                3 -> {
                    // 发送成功之后跳转到消息中心
                    ServiceManager.activity(NOTIFICATION_HOME){
                        withInt("MsgType", 2)
                    }
                    finishAfterTransition()
                }

                4 -> {
                    mBtnNoClassNext.apply {
                        setBackgroundResource(R.drawable.affair_ic_next_process_positive)
                        text = "下一步"
                    }
                }
            }
        }
    }


    private fun initClick() {
        // 如果是没课越的事务界面
        mBtnNoClassNext.setOnSingleClickListener {
            mViewModel.clickNextBtn()
        }
        mBtnBack.setOnSingleClickListener {
            mViewModel.clickLastBtn()
        }
    }

    private fun initFragment() {
        replaceFragment(R.id.affair_fcv_edit_affair) {
            NoClassAffairFragment.newInstance(mParams)
        }
    }
}