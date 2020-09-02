package com.mredrock.cyxbs.common.event

/**
 * 当main模块的ViewPager的page改变后，发送此事件。
 *
 * @param index 表示改变后处于哪个Page。
 *
 * Created by anriku on 2018/10/22.
 */
data class MainVPChangeEvent(val index: Int)