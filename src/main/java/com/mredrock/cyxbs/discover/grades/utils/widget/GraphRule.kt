package com.mredrock.cyxbs.discover.grades.utils.widget

import kotlin.math.abs

/**
 * Created by roger on 2020/3/20
 */
/**
 * 映射规则，用于将任何范围的数据映射到[0, 4]的区间
 */
abstract class GraphRule {
    abstract fun mappingRule(old: Float): Float
}

open class AverageRule(val list: List<Float>) : GraphRule() {
    override fun mappingRule(old: Float): Float {
        if (old == 0F) {
            return 0F
        }
        val local = list.filter {
            abs(it) > 0.01F
        }
        val max = local.max() ?: return 0F
        val min = local.min() ?: return 0F
        //对曲线做了一定改动，保证曲线的值再[0,4]，同时为了使最大值和最小值不会相差过大，作业一定计算
        return ((old - min) / (max - min) * 4) / 2 + 1
    }
}