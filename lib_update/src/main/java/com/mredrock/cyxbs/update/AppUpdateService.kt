package com.mredrock.cyxbs.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.update.APP_UPDATE_SERVICE
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.update.model.AppUpdateModel

/**
 * Create By Hosigus at 2020/5/2
 */
@Route(path = APP_UPDATE_SERVICE, name = APP_UPDATE_SERVICE)
internal class AppUpdateService : IAppUpdateService {
    override fun getUpdateStatus(): LiveData<AppUpdateStatus> = AppUpdateModel.status

    override fun checkUpdate() {
        AppUpdateModel.checkUpdate()
    }

    override fun noticeUpdate(activity: AppCompatActivity) {
        val info = AppUpdateModel.updateInfo ?: return
        MaterialDialog(activity).show {
            title(text = "有新版本更新")
            message(text = "最新版本:" + info.versionName + "\n" + info.updateContent + "\n点击点击，现在就更新一发吧~")
            positiveButton(text = "下载最新安装包") {
                val uri = Uri.parse(info.apkUrl)
                /*
                * 22-8-30
                * 因为应用内更新有很多毛病，所以采用浏览器下载
                * */
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, uri)
                )
            }
            negativeButton(text = "下次吧") {
                dismiss()
            }
            cornerRadius(16F)
        }
    }

    override fun init(context: Context) {}
}