package com.mredrock.cyxbs.mine.page.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.APP_WEBSITE
import com.mredrock.cyxbs.common.config.updateFile
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_about.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class AboutActivity(override val isFragmentActivity: Boolean = false,
                    override val viewModelClass: Class<AboutViewModel> = AboutViewModel::class.java)
    : BaseViewModelActivity<AboutViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_about)

        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@AboutActivity, R.color.windowBackground))
            initWithSplitLine("关于掌邮",
                    false,
                    R.drawable.mine_ic_arrow_left)
            setTitleLocationAtLeft(true)
        }
        getAppVersionName(this@AboutActivity)?.let {
            val name = "zscy-feature-intro-${it}"
            viewModel.getFeatureIntroduction(name)
        }


        setAppVersionName()

        setIsUpdate()
        mine_about_rl_website.setOnClickListener { clickWebsite() }
        mine_about_legal.setOnClickListener { clickLegal() }
        mine_about_rl_update.setOnClickListener { clickUpdate() }
        mine_about_rl_share.setOnClickListener { onShareClick() }

        mine_about_rl_function.setOnClickListener { clickFeatureIntroduction() }
    }

    private fun clickFeatureIntroduction() {
        val tag = "feature"
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.mine_layout_dialog_feature_intro,
                        onPositiveClick = { dismiss() },
                        positiveString = "我知道了",
                        elseFunction = { rootView ->
                            viewModel.featureIntro.value?.let {
                                if (it.textList.isNotEmpty()) {
                                    val feature = it.textList.last()
                                    rootView.findViewById<TextView>(R.id.mine_about_tv_feature_title).text = feature.title
                                    rootView.findViewById<TextView>(R.id.mine_about_tv_feature_content).text = feature.content
                                }
                            }
                        }
                )
            }.show(supportFragmentManager, tag)
        }
    }

    private fun clickUpdate() {
        UpdateUtils.checkUpdate(this) {
            toast("已经是最新版了哦")
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

    private fun setIsUpdate() {
        if (UpdateUtils.isUpdate(this)) {
            mine_about_tv_already_up_to_date.text = "已是最新版本"
        } else {
            mine_about_tv_already_up_to_date.text = "发现新版本"
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
    }

    private fun onShareClick() {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "掌上重邮是重邮首款校园生活类App，拥有查课表，签到，邮问等功能，记得分享给好友哦。 https://wx.idsbllp.cn/app/")
        startActivity(Intent.createChooser(intent, "分享"))
    }
}
