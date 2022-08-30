package com.mredrock.cyxbs.api.update

/**
 * Create By Hosigus at 2020/5/2
 */
enum class AppUpdateStatus {
    UNCHECK,        // 未检查
    DATED,          // 当前版本已过时，建议更新
    VALID,          // 当前版本有效，无需更新
    
    CHECKING,       // 检查更新中
    ERROR           // 任何时候出错，建议RECHECK，但可能是客户端或服务端的网络问题
}