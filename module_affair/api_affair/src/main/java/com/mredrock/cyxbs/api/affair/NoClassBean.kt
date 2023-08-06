package com.mredrock.cyxbs.api.affair

import java.io.Serializable


/**
 * 前人写好了通过服务来模块间通信(降低对ARouter的依赖)
 *服务中传递自定义对象找不到太好的做法
 * 1：像这样把数据类直接建立在api模块下
 * 2：将对象转换成为json然后传递再解析
 * 期待后人有更好的做法
 */

/**
 * 发送通知的时候需要的学号的集合：前面是学号，后面是是否空闲，true代表空闲
 */

data class NoClassBean(
    val mStuList : List<Pair<String,Boolean>>
) : Serializable
