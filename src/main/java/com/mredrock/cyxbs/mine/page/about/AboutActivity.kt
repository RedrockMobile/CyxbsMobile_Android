package com.mredrock.cyxbs.mine.page.about

import android.Manifest
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.APP_WEBSITE
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.config.updateFile
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.saveImage
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.extension.logr
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.mine_activity_about.*
import kotlinx.android.synthetic.main.mine_dialog_share.view.*
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
            logr(it)
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
        MaterialDialog.Builder(this)
                .title("使用条款")
                .content("版权归红岩网校工作站所有,感谢您的使用")
                .positiveText("确定")
                .build()
                .show()
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
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.mine_dialog_share, null, false)
        val dialog = builder.setView(view).show()
        val imageView = view.imageView
        imageView.setOnLongClickListener {
            doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                reason = "为保存图片需要您的文件写入权限"
                doAfterGranted {
                    val drawable = imageView.drawable
                    if (drawable is BitmapDrawable) {
                        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                        Schedulers.io().scheduleDirect {
                            this@AboutActivity.saveImage(bitmap, "掌邮二维码")
                            MediaScannerConnection.scanFile(this@AboutActivity,
                                    arrayOf(Environment.getExternalStorageDirectory().toString() + DIR_PHOTO),
                                    arrayOf("image/jpeg"),
                                    null)

                            runOnUiThread {
                                toast("图片保存于系统\"$DIR_PHOTO\"文件夹下哦")
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

            true
        }
    }
}
