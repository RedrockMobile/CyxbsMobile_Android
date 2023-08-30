package com.mredrock.cyxbs.login.bean

import java.io.Serializable

/**
 * @author : why
 * @time   : 2023/3/31 16:46
 * @bless  : God bless my code
 */

/**
 * 登录时上传的设备信息
 * @param phone 手机的唯一标识符
 * @param manufacturer 手机的制造厂商（小米，华为等）
 * @param ip 登录时如果连接了WiFi，则上传ip，否则为null
 */
data class DeviceInfoParams(val phone: String, val manufacturer: String, val ip: String? = null): Serializable
