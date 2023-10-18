package com.mredrock.cyxbs.lib.utils.network

import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.NetType
import com.alibaba.sdk.android.httpdns.net.HttpDnsNetworkDetector
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import okhttp3.Dns
import java.net.InetAddress

/**
 * ... httpdns解析域名,由运维统一下发
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/9/13
 * @Description:
 */
class OkHttpDns private constructor() : Dns {


    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { OkHttpDns() }
        //阿里云httpDns账号id，由运维统一管理
        private const val ACCOUNT_ID = "137074"
        //预加载域名
        private val PRE_LOAD_ADDRESS =
            arrayListOf("be-dev.redrock.cqupt.edu.cn", "be-prod.redrock.cqupt.edu.cn")
        private val httpService = HttpDns.getService(appContext, ACCOUNT_ID)

        /**
         * 设置httpdns预加载功能
         */
        fun setPreLoading() {
            httpService.apply {
                setCachedIPEnabled(true)
                setPreResolveHosts(PRE_LOAD_ADDRESS)
            }
        }
    }

    override fun lookup(hostname: String): List<InetAddress> {
        var ip: String? = null
        val netType = HttpDnsNetworkDetector.getInstance().getNetType(appContext)
        //优先使用ipv4地址
        if (netType == NetType.v4 || netType == NetType.both) {
            ip = httpService.getIPv4ForHostAsync(hostname)
        } else if (netType == NetType.v6) {
            ip = httpService.getIPv6ForHostAsync(hostname)
        }
        ip?.let {
            return InetAddress.getAllByName(it).asList()
        }
        //httpDns解析失败走默认dns解析
        return Dns.SYSTEM.lookup(hostname)
    }
}