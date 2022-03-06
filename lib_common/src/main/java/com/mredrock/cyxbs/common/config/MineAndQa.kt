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

    /*
    * 某位学长遗留的代码，设计的有些问题，
    * 问题如下：
    * 这个是 activity 与 Fragment 通信，应该放到 ViewModel 中
    *
    * 由于时间原因，目前暂时就这样改了：在 Fragment 的 onViewCreated()，onDestroyView() 中取消
    * todo 建议以后某位学弟改一改这个回调，把它移到 activity 的 ViewModel 中
    * */
    var refreshListener:RefreshListener?=null

    interface RefreshListener{
        fun onRefresh(redid:String?)
    }
}