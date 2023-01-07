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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.config.APP_WEBSITE
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.DynamicRVAdapter
import android.os.StrictMode

import android.os.StrictMode.VmPolicy
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DIR_LOG
import com.mredrock.cyxbs.common.config.OKHTTP_LOCAL_LOG
import java.io.File


class AboutActivity : BaseViewModelActivity<AboutViewModel>()  {
    private val mine_about_rl_share by R.id.mine_about_rl_share.view<RelativeLayout>()
    private val mine_about_legal by R.id.mine_about_legal.view<TextView>()
    private val mine_about_rl_website by R.id.mine_about_rl_website.view<RelativeLayout>()
    private val mine_about_rl_update by R.id.mine_about_rl_update.view<RelativeLayout>()
    private val mine_about_rl_function by R.id.mine_about_rl_function.view<RelativeLayout>()
    private val mine_about_tv_copy_right by R.id.mine_about_tv_copy_right.view<TextView>()
    private val mine_about_version by R.id.mine_about_version.view<TextView>()
    private val mine_about_tv_already_up_to_date by R.id.mine_about_tv_already_up_to_date.view<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_about)

        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@AboutActivity, com.mredrock.cyxbs.common.R.color.common_window_background))
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
        mine_about_tv_copy_right.setOnLongClickListener {
            clickLogLocal() }
    }

    private fun clickLogLocal():Boolean{
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val path = "${applicationContext.filesDir.absolutePath}${DIR_LOG}/$OKHTTP_LOCAL_LOG"
        val file = File(path)
        if (!file.exists()){
            CyxbsToast.makeText(this,"暂无log日志",Toast.LENGTH_SHORT).show()
            return false
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.setPackage("com.tencent.mobileqq")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = FileProvider.getUriForFile(applicationContext,"com.mredrock.cyxbs.fileProvider",file)
        intent.putExtra(Intent.EXTRA_STREAM,uri) //传输图片或者文件 采用流的方式
        intent.type = "*/*" //分享文件
        this.startActivity(Intent.createChooser(intent, "分享"))
        return false
    }

    private fun clickFeatureIntroduction() {
        val materialDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.mine_layout_dialog_recyclerview_dynamic, materialDialog.window?.decorView as ViewGroup, false)
        val rv_content:RecyclerView = view.findViewById(R.id.rv_content)
        val loader:ProgressBar = view.findViewById(R.id.loader)
        materialDialog.setContentView(view)
        materialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val featureIntroAdapter = DynamicRVAdapter(viewModel.featureIntroList)
        rv_content.adapter = featureIntroAdapter
        rv_content.layoutManager = LinearLayoutManager(this@AboutActivity)
        if (viewModel.featureIntroList.isNotEmpty()) loader.visibility = View.GONE
        materialDialog.show()
        getAppVersionName(this@AboutActivity)?.let {
            val name = "zscy-feature-intro-${it}"
            viewModel.getFeatureIntro(name,
                    successCallBack = {
                        featureIntroAdapter.notifyDataSetChanged()
                        loader.visibility = View.GONE
                    },
                    errorCallback = {
                        materialDialog.dismiss()
                        toast("获取失败")
                    }
            )
        }

    }

    private fun clickUpdate() {
        selfUpdateCheck = true
        ServiceManager(IAppUpdateService::class).apply {
            when (getUpdateStatus().value) {
                AppUpdateStatus.DATED -> {
                    noticeUpdate(this@AboutActivity)
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
        startActivity(
            Intent(
                this,
                AgreementActivity::class.java
            )
        )
    }

    private fun setAppVersionName() {
        mine_about_version.text = StringBuilder("Version ").append(getAppVersionName(this@AboutActivity))
    }

    private var selfUpdateCheck = false
    private fun bindUpdate() {
        ServiceManager(IAppUpdateService::class).apply {
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
                    AppUpdateStatus.ERROR -> {
                        mine_about_tv_already_up_to_date.text = "建议再试试哟~"
                        if (selfUpdateCheck) toast("有一股神秘力量阻拦了更新，请稍候重试或尝试反馈")
                    }
                    else -> {}
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
