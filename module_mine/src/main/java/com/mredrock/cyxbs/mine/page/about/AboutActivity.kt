package com.mredrock.cyxbs.mine.page.about

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.APP_WEBSITE
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.DynamicRVAdapter
import kotlinx.android.synthetic.main.mine_activity_about.*
import kotlinx.android.synthetic.main.mine_layout_dialog_recyclerview_dynamic.view.*


class AboutActivity : BaseViewModelActivity<AboutViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_about)

        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@AboutActivity, R.color.common_window_background))
            initWithSplitLine("关于我们",
                    false,
                    R.drawable.mine_ic_arrow_left)
            setTitleLocationAtLeft(false)
        }
        setAppVersionName()

        bindUpdate()
        mine_about_rl_website.setOnClickListener { clickWebsite() }
        mine_about_legal.setOnClickListener { clickLegal() }
        mine_about_rl_share.setOnClickListener { onShareClick() }
        mine_about_rl_update.setOnClickListener { clickUpdate() }
        mine_about_rl_function.setOnClickListener { clickFeatureIntroduction() }
    }

    private fun clickFeatureIntroduction() {
        val materialDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.mine_layout_dialog_recyclerview_dynamic, materialDialog.window?.decorView as ViewGroup, false)
        materialDialog.setContentView(view)
        materialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val featureIntroAdapter = DynamicRVAdapter(viewModel.featureIntroList)
        view.rv_content.adapter = featureIntroAdapter
        view.rv_content.layoutManager = LinearLayoutManager(this@AboutActivity)
        if (viewModel.featureIntroList.isNotEmpty()) view.loader.visibility = View.GONE
        materialDialog.show()
        getAppVersionName(this@AboutActivity)?.let {
            val name = "zscy-feature-intro-${it}"
            viewModel.getFeatureIntro(name,
                    successCallBack = {
                        featureIntroAdapter.notifyDataSetChanged()
                        view.loader.visibility = View.GONE
                    },
                    errorCallback = {
                        materialDialog.dismiss()
                        BaseApp.context.toast("获取失败")
                    }
            )
        }

    }

    private fun clickUpdate() {
        selfUpdateCheck = true
        ServiceManager.getService(IAppUpdateService::class.java).apply {
            when (getUpdateStatus().value) {
                AppUpdateStatus.DOWNLOADING -> {
                    toast("在下了在下了,下拉可以看进度")
                }
                AppUpdateStatus.CANCEL -> {
                    noticeUpdate(this@AboutActivity)
                }
                AppUpdateStatus.TO_BE_INSTALLED -> {
                    installUpdate(this@AboutActivity)
                }
                else -> checkUpdate()
            }
        }
    }

    private fun clickWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APP_WEBSITE))
        startActivity(intent)
    }

    private fun clickLegal() {
        val tag = "legal"
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.mine_layout_dialog_with_title_and_content,
                        onPositiveClick = { dismiss() },
                        positiveString = "我知道了",
                        elseFunction = { rootView ->
                            rootView.findViewById<TextView>(R.id.dialog_title).text = "使用条款"
                            rootView.findViewById<TextView>(R.id.dialog_content).text = "版权归红岩网校工作站所有,感谢您的使用"
                        }
                )
            }.show(supportFragmentManager, tag)
        }
    }

    private fun setAppVersionName() {
        mine_about_version.text = StringBuilder("Version ").append(getAppVersionName(this@AboutActivity))
    }

    private var selfUpdateCheck = false
    private fun bindUpdate() {
        ServiceManager.getService(IAppUpdateService::class.java).apply {
            getUpdateStatus().observe {
                when (it) {
                    AppUpdateStatus.UNCHECK -> checkUpdate()
                    AppUpdateStatus.DATED -> {
                        mine_about_tv_already_up_to_date.text = "发现新版本"
                        if (selfUpdateCheck) noticeUpdate(this@AboutActivity)
                    }
                    AppUpdateStatus.VALID -> {
                        mine_about_tv_already_up_to_date.text = "已是最新版本"
                        if (selfUpdateCheck) toast("已经是最新版了哦")
                    }
                    AppUpdateStatus.LATER -> {
                        mine_about_tv_already_up_to_date.text = "存在被忽略的新版本"
                    }
                    AppUpdateStatus.DOWNLOADING -> {
                        mine_about_tv_already_up_to_date.text = "新版本下载中"
                    }
                    AppUpdateStatus.CANCEL -> {
                        mine_about_tv_already_up_to_date.text = "更新被取消惹"
                    }
                    AppUpdateStatus.TO_BE_INSTALLED -> {
                        mine_about_tv_already_up_to_date.text = ">>点我安装<<"
                        installUpdate(this@AboutActivity)
                    }
                    AppUpdateStatus.ERROR -> {
                        mine_about_tv_already_up_to_date.text = "建议再试试哟~"
                        if (selfUpdateCheck) toast("有一股神秘力量阻拦了更新，请稍候重试或尝试反馈")
                    }
                }
            }
        }
    }

    private fun onShareClick() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "掌上重邮是重邮首款校园生活类App，拥有查课表，签到，邮问等功能，记得分享给好友哦。 $APP_WEBSITE")
        startActivity(Intent.createChooser(intent, "分享"))
    }
}
