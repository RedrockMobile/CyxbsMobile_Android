package com.mredrock.cyxbs.lib.utils.utils

import com.mredrock.cyxbs.common.utils.LogLocal

/**
 *
 * 以前廖老师写的本地收集报错文件
 *
 * 在 关于我们 底部文字长按即可触发本地收集报错文件
 *
 * 解密请看飞书知识库
 *
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/10
 * @Description:
 */
object LogLocal {
    fun log(tag: String = "tag", msg: String, throwable: Throwable? = null) {
        LogLocal.log(tag, msg, throwable)
    }
}