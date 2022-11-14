package com.mredrock.api.dialog

import android.content.Context
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.template.IProvider
import kotlinx.coroutines.flow.SharedFlow

/**
 * kim.bifrost.api_dialog.IDialogService
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/8/1 21:09
 */
interface IDialogService : IProvider {

    /**
     * 打开一个Web Dialog
     *
     * @param lifecycle 该dialog的生命周期与传入的lifecycle一致
     * @param context
     * @param url
     */
    fun openWebDialog(lifecycle: Lifecycle, context: Context, url: String, handleWebView: WebView.() -> Unit = {})

    /**
     * 打开一个Web Dialog，简化写法
     *
     * @param activity
     * @param url
     */
    fun openWebDialog(activity: AppCompatActivity, url: String, handleWebView: WebView.() -> Unit = {}) {
        openWebDialog(activity.lifecycle, activity, url, handleWebView)
    }

    /**
     * 打开一个Web Dialog，简化写法
     *
     * @param fragment
     * @param url
     */
    fun openWebDialog(fragment: Fragment, url: String, handleWebView: WebView.() -> Unit = {}) {
        openWebDialog(fragment.viewLifecycleOwner.lifecycle, fragment.requireContext(), url, handleWebView)
    }

    /**
     * 订阅它以接收前端侧发送的事件
     * 下载 保存图片 自定义事件
     */
    val webEventChannel: SharedFlow<DialogWebEvent>
}