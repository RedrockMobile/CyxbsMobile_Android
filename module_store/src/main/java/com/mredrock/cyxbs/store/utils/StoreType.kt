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

    object Task { // 邮票中心首页的邮票任务, 因为在进度修改时要我们自己上传任务进度(会涉及不同模块), 所以类型写在了 common 模块中
        fun jumpOtherUi(context: Context, task: StampCenter.Task) {
            when (task.title) {
                StoreTask.Task.DAILY_SIGN.title -> {
                    ARouter.getInstance().build(MINE_CHECK_IN).navigation()
                }
                StoreTask.Task.SEE_DYNAMIC.title,
                StoreTask.Task.PUBLISH_DYNAMIC.title,
                StoreTask.Task.SHARE_DYNAMIC.title,
                StoreTask.Task.POST_COMMENT.title,
                StoreTask.Task.GIVE_A_LIKE.title -> {
                    val intent = Intent(context, QaActivity::class.java)
                    context.startActivity(intent)
                }
                StoreTask.Task.EDIT_INFO.title -> {
                    ARouter.getInstance().build(MINE_EDIT_INFO).navigation()

                }
                StoreTask.Task.LOGIN_VOLUNTEER.title -> {
                    ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
                }
                /*
                * 注意, 如果后面接手的学弟要添加新的任务, 目前(2021/9/11)对于任务进度是要我们自己完成后向后端发送请求的
                * 发送请求的代码我已经封装进了 common 模块的 /config/StoreTask 中
                * */
                else -> {
                    context.toast("若点击无跳转，请向我们反馈，谢谢")
                }
            }
        }
    }

    object ExchangeError { // 请求兑换时使用
        const val OTHER_ERROR = -4734 // 其他错误, 乱打的数字防止出现重复
        const val NOT_ENOUGH_MONEY = 50000 // 钱不够
        const val OUT_OF_STOCK = 50001 // 库存不足
    }
}