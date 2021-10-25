package com.mredrock.cyxbs.common.config

/**
 * @ClassName MineAndQa
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/22 21:19
 * @Version 1.0
 */
object MineAndQa {

    var refreshListener:RefreshListener?=null

    interface RefreshListener{
        fun onRefresh(redid:String?)
    }
}