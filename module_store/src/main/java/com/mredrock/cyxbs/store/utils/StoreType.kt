package com.mredrock.cyxbs.store.utils

import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN
import com.mredrock.cyxbs.store.bean.StampCenter

/**
 * 需要与后端交互时的类型区分
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/27
 */
class StoreType {
    object Product { // 货物分类时使用
        const val DRESS = 1 // 装扮
        const val GOODS = 0 // 邮货
    }

    object Task { // 邮票中心首页的邮票任务
        const val Base = "base" // 每日任务
        const val More = "more" // 更多任务

        fun jumpOtherUi(task: StampCenter.Task) {
            when (task.title) {
                "今日打卡" -> {
                    ARouter.getInstance().build(MINE_CHECK_IN).navigation()
                }
                "逛逛邮问" -> {

                }
                "斐然成章" -> {

                }
                "围观吃瓜" -> {

                }
                "能说会道" -> {

                }
            }
        }
    }

    object ExchangeError { // 请求兑换时使用
        const val OUT_OF_STOCK = "reduce goods error" // 库存不足
        const val NOT_ENOUGH_MONEY = "Integral not enough" // 钱不够
    }
}