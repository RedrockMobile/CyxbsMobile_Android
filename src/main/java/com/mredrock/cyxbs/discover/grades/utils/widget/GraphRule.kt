package com.mredrock.cyxbs.discover.grades.utils.widget

/**
 * Created by roger on 2020/3/20
 */
/**
 * 映射规则，用于将任何范围的数据映射到[0, 4]的区间
 */
abstract class GraphRule {
    abstract fun mappingRule(old: Float): Float
}