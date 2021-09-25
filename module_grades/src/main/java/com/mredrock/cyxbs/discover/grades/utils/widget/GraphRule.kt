package com.mredrock.cyxbs.discover.grades.utils.widget

/**
 * Created by roger on 2020/3/20
 */
/**
 * 映射规则，用于将任何范围的数据映射到[0, 4]的区间
 */
abstract class GraphRule {
    abstract fun mappingRule(old: String): Float
}

open class AverageRule(val list: List<String>) : GraphRule() {
    private val max = list.map { it.toFloat() }.maxOrNull() ?: 4F
    private val min = list.map { it.toFloat() }.minOrNull() ?: 0F
    override fun mappingRule(old: String): Float {
        //对曲线做了一定改动，保证曲线的值在[0,4]，同时为了使最大值和最小值不会相差过大，做了一定计算
        return ((old.toFloat() - min) / (max - min) * 3.5f) + 0.5f
    }
}