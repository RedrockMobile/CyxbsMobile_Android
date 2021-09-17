package com.mredrock.cyxbs.update

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.APP_UPDATE_SERVICE
import com.mredrock.cyxbs.common.config.updateFile
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.FIRST_TIME_OPEN
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.uri
import com.mredrock.cyxbs.update.component.AppUpdateDownloadService
import com.mredrock.cyxbs.update.component.PackageInstallsFragment
import com.mredrock.cyxbs.update.model.AppUpdateModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

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
            positiveButton(text = "更新") {
                RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted == true) {
                                //更新标志
                                BaseApp.context.defaultSharedPreferences.editor {
                                    putBoolean(FIRST_TIME_OPEN, false)
                                    commit()
                                }
                                downloadUpdate(activity)
                            } else {
                                activity.toast("没有赋予权限就不能更新哦")
                            }
                        }
            }
            negativeButton(text = "下次吧") {
                dismiss()
                AppUpdateModel.status.value = AppUpdateStatus.LATER
            }
            cornerRadius(res=R.dimen.common_corner_radius)
        }
    }

    @SuppressLint("CheckResult")
    override fun installUpdate(activity: AppCompatActivity) {
        requestPackageInstallPermissions(activity).subscribe {
            if (it != true) {
                return@subscribe
            }
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW)
                        .addFlags(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        } else {
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        .setDataAndType(updateFile.uri, "application/vnd.android.package-archive"))
            } catch (e: Exception) {
                e.printStackTrace()
                AppUpdateModel.status.value = AppUpdateStatus.ERROR
            }
        }
    }

    override fun init(context: Context) {}

    private fun downloadUpdate(activity: AppCompatActivity) {
        AppUpdateModel.status.value = AppUpdateStatus.DOWNLOADING
        activity.startService(
                Intent(activity, AppUpdateDownloadService::class.java)
                        .putExtra("url", AppUpdateModel.updateInfo?.apkUrl ?: return))
    }

    private fun requestPackageInstallPermissions(activity: AppCompatActivity): Observable<Boolean> {
        var fragment: PackageInstallsFragment?
        if (activity.isFinishing) {
            return Observable.just(false)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || activity.packageManager.canRequestPackageInstalls()) {
            return Observable.just(true)
        }

        val fm = activity.supportFragmentManager
        fragment = fm.findFragmentByTag(PackageInstallsFragment.TAG) as PackageInstallsFragment?
        if (fragment == null) {
            fragment = PackageInstallsFragment()
            fm.beginTransaction()
                    .add(fragment, PackageInstallsFragment.TAG)
                    .commitNow()
        }
        if (!fragment.requesting) {
            fragment.sub = PublishSubject.create()
        }
        fragment.requestPermissions()

        return fragment.sub ?: Observable.just(false)
    }
}