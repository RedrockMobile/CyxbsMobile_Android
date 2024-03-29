package com.mredrock.cyxbs.store.utils

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.store.IStoreService.Task.*
import com.mredrock.cyxbs.config.route.DECLARE_ENTRY
import com.mredrock.cyxbs.config.route.DEFAULT_PAGE
import com.mredrock.cyxbs.config.route.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.config.route.FOOD_ENTRY
import com.mredrock.cyxbs.config.route.MINE_CHECK_IN
import com.mredrock.cyxbs.config.route.MINE_EDIT_INFO
import com.mredrock.cyxbs.config.route.UFIELD_MAIN_ENTRY
import com.mredrock.cyxbs.lib.utils.extensions.toast
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
  
  object Task { // 邮票中心首页的邮票任务, 在进度修改时要我们自己上传任务进度 (会涉及不同模块)
    fun jumpOtherUi(context: Context, task: StampCenter.Task) {
      when (task.title) {
        DAILY_SIGN.title -> {
          ARouter.getInstance().build(MINE_CHECK_IN).navigation()
        }
        SEE_DYNAMIC.title,
        PUBLISH_DYNAMIC.title,
        SHARE_DYNAMIC.title,
        POST_COMMENT.title,
        GIVE_A_LIKE.title -> {
          /**
           * TODO 下架服务
           */
          ARouter.getInstance().build(DEFAULT_PAGE).navigation()
//          val intent = Intent(context, QaActivity::class.java)
//          context.startActivity(intent)
        }
        JOIN_FOOD.title->{
          ARouter.getInstance().build(FOOD_ENTRY).navigation()
        }
        JOIN_NOCLASS.title->{
          ARouter.getInstance().build(DISCOVER_NO_CLASS).navigation()
        }
        JOIN_DECLARE.title->{
          ARouter.getInstance().build(DECLARE_ENTRY).navigation()
        }
        JOIN_UFIELD.title->{
          ARouter.getInstance().build(UFIELD_MAIN_ENTRY).navigation()
        }
        EDIT_INFO.title -> {
          ARouter.getInstance().build(MINE_EDIT_INFO).navigation()
        }
        LOGIN_VOLUNTEER.title -> {
          ARouter.getInstance().build(DEFAULT_PAGE).navigation()
//          ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
        }
        /*
        * 注意, 如果后面接手的学弟要添加新的任务, 目前(2021/9/11)对于任务进度是要我们自己完成后向后端发送请求的
        * 发送请求的代码我已经封装进了 api 模块中
        * */
        else -> {
          toast("若点击无跳转，请向我们反馈，谢谢")
        }
      }
    }
  }
  
  object ExchangeError { // 请求兑换时使用
    const val OTHER_ERROR = Int.MIN_VALUE // 其他错误, 乱打的数字防止出现重复
    const val NOT_ENOUGH_MONEY = 50000 // 钱不够
    const val OUT_OF_STOCK = 50001 // 库存不足
    const val IS_PURCHASED = 50002 // 当商品为邮货时且已经购买过
  }
}