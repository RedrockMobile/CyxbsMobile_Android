package com.mredrock.cyxbs.store.utils

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN
import com.mredrock.cyxbs.common.config.MINE_EDIT_INFO
import com.mredrock.cyxbs.common.config.StoreTask
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.qa.ui.activity.QaActivity

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

        fun jumpOtherUi(context: Context, task: StampCenter.Task) {
            when (task.title) {
                StoreTask.Base.DAILY_SIGN.s -> {
                    ARouter.getInstance().build(MINE_CHECK_IN).navigation()
                }
                StoreTask.Base.SEE_DYNAMIC.s,
                StoreTask.Base.PUBLISH_DYNAMIC.s,
                StoreTask.Base.SHARE_DYNAMIC.s,
                StoreTask.Base.POST_COMMENT.s,
                StoreTask.Base.GIVE_A_LIKE.s -> {
                    val intent = Intent(context, QaActivity::class.java)
                    context.startActivity(intent)
                }
                StoreTask.More.EDIT_INFO.s -> {
                    ARouter.getInstance().build(MINE_EDIT_INFO).navigation()

                }
                StoreTask.More.LOGIN_VOLUNTEER.s -> {
                    ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
                }
                else -> {
                    context.toast("若点击无跳转，请向我们反馈，谢谢")
                }
            }
        }
    }

    object ExchangeError { // 请求兑换时使用
        const val OUT_OF_STOCK = "reduce goods error" // 库存不足
        const val NOT_ENOUGH_MONEY = "Integral not enough" // 钱不够
    }
}