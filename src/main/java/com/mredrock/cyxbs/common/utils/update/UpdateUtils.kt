package com.mredrock.cyxbs.common.utils.update

import android.Manifest
import android.content.Context
import android.content.Intent
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.bean.UpdateInfo
import com.mredrock.cyxbs.common.config.updateFilePath
import com.mredrock.cyxbs.common.config.updateFilename
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.getAppVersionCode
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Create By Hosigus at 2019/5/11
 */

object UpdateUtils {
    fun checkUpdate(context: Context, permissions: RxPermissions, onNewest: (() -> Unit)? = null) {
        ApiGenerator.getApiService(UpdateApiService::class.java)
                .getUpdateInfo()
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    doubleInsurance(context, permissions, onNewest)
                }, onNext = {
                    if (it.versionCode <= getAppVersionCode(context)) {
                        onNewest?.invoke()
                    } else {
                        noticeUpdate(context, permissions, it)
                    }
                })
    }

    private fun noticeUpdate(context: Context, permissions: RxPermissions, it: UpdateInfo) {
        MaterialDialog.Builder(context)
                .title("更新")
                .title("有新版本更新")
                .content("最新版本:" + it.versionName + "\n" + it.updateContent + "\n点击点击，现在就更新一发吧~")
                .positiveText("更新")
                .negativeText("下次吧")
                .onNegative { dialog, _ -> dialog.dismiss() }
                .onPositive { _, _ ->
                    permissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe { granted ->
                                if (granted!!) {
                                    startDownload(it.apkURL, context)
                                } else {
                                    context.toast("没有赋予权限就不能更新哦")
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
    private fun doubleInsurance(context: Context, permissions: RxPermissions, onNewest: (() -> Unit)?) {
        ApiGenerator.getApiService(Retrofit.Builder()
                .baseUrl("http://hongyan.cqupt.edu.cn")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build(), UpdateApiService::class.java)
                .getUpdateInfo()
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    onNewest?.let {
                        context.toast("检查更新失败")
                    }
                }, onNext = {
                    if (it.versionCode <= getAppVersionCode(context)) {
                        onNewest?.invoke()
                    } else {
                        noticeUpdate(context, permissions, it)
                    }
                })
    }

    private fun startDownload(urlStr: String, context: Context) {
        val updateIntent = Intent(context, UpdateService::class.java)
        updateIntent.putExtra("url", urlStr)
        updateIntent.putExtra("path", updateFilePath)
        updateIntent.putExtra("name", updateFilename)
        context.startService(updateIntent)
    }
}