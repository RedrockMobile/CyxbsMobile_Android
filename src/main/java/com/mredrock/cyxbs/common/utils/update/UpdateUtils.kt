package com.mredrock.cyxbs.common.utils.update

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.bean.UpdateInfo
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.uri
import com.mredrock.cyxbs.common.utils.getAppVersionCode
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File

/**
 * Create By Hosigus at 2019/5/11
 */

object UpdateUtils {
    private var checking = false

    fun checkUpdate(activity: AppCompatActivity, onNewest: (() -> Unit)? = null) {
        if (checking) {
            return
        }
        checking = true
        ApiGenerator.getApiService(UpdateApiService::class.java)
                .getUpdateInfo()
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    doubleInsurance(activity, onNewest)
                }, onNext = {
                    if (it.versionCode <= getAppVersionCode(activity)) {
                        onNewest?.invoke()
                        checking = false
                    } else {
                        noticeUpdate(activity, it)
                    }
                })
    }

    private fun noticeUpdate(activity: AppCompatActivity, it: UpdateInfo) {
        MaterialDialog.Builder(activity)
                .title("更新")
                .title("有新版本更新")
                .content("最新版本:" + it.versionName + "\n" + it.updateContent + "\n点击点击，现在就更新一发吧~")
                .positiveText("更新")
                .negativeText("下次吧")
                .onNegative { dialog, _ -> dialog.dismiss() }
                .onPositive { _, _ ->
                    RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe { granted ->
                                if (granted == true) {
                                    startDownload(activity, it.apkURL)
                                } else {
                                    activity.toast("没有赋予权限就不能更新哦")
                                }
                            }
                }
                .cancelable(false)
                .show()
    }

    /**
     * 当默认域名请求错误
     * 更换备用域名尝试更新
     */
    private fun doubleInsurance(activity: AppCompatActivity, onNewest: (() -> Unit)?) {
        ApiGenerator.getApiService(Retrofit.Builder()
                .baseUrl("http://hongyan.cqupt.edu.cn")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build(), UpdateApiService::class.java)
                .getUpdateInfo()
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    it.printStackTrace()
                    onNewest?.let {
                        activity.toast("检查更新失败")
                    }
                    checking = false
                }, onNext = {
                    if (it.versionCode <= getAppVersionCode(activity)) {
                        onNewest?.invoke()
                        checking = false
                    } else {
                        noticeUpdate(activity, it)
                    }
                })
    }

    private fun startDownload(activity: AppCompatActivity, urlStr: String) {
        val updateIntent = Intent(activity, UpdateService::class.java)
        updateIntent.putExtra("url", urlStr)
        activity.startService(updateIntent)
    }

    @SuppressLint("CheckResult")
    fun installApk(activity: AppCompatActivity, file: File) {
        requestPackageInstalls(activity).subscribe {
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
                        .setDataAndType(file.uri, "application/vnd.android.package-archive"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun requestPackageInstalls(activity: AppCompatActivity): Observable<Boolean> {
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

    class PackageInstallsFragment : Fragment() {
        var sub: PublishSubject<Boolean>? = null
        var requesting = false

        companion object {
            const val TAG = "PackageInstallsFragment"
            const val CODE = 0x0101
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun requestPermissions() {
            requesting = true
            val packageName = activity?.packageName
            if (packageName == null) {
                requesting = false
                return
            }
            startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.fromParts("package", packageName, null)), CODE)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            requesting = false
            val sub = sub ?: return
            sub.onNext(activity?.packageManager?.canRequestPackageInstalls() ?: false)
            sub.onComplete()
        }
    }
}