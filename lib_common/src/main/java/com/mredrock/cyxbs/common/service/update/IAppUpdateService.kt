package com.mredrock.cyxbs.common.service.update

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Create By Hosigus at 2020/5/3
 */
interface IAppUpdateService : IProvider {
    // 订阅更新状态
    fun getUpdateStatus(): LiveData<AppUpdateStatus>
    // 检查更新
    fun checkUpdate()
    // 通知用户有更新
    fun noticeUpdate(activity: AppCompatActivity)
    // 安装更新
    fun installUpdate(activity: AppCompatActivity)
}