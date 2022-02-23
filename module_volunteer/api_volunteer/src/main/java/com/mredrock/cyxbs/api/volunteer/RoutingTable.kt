package com.mredrock.cyxbs.api.volunteer

/**
 * 路由表命名规则：
 * <ul>
 *     <li>常量名（全大写）：模块名_功能描述，例：QA_ENTRY</li>
 *     <li>二级路由：/模块名/功能描述，例：/qa/entry</li>
 *     <li>多级路由：/模块依赖关系倒置/功能描述，例：/map/discover/entry</li>
 * </ul>
 */

const val DISCOVER_VOLUNTEER = "/volunteer/discover/entry"
const val DISCOVER_VOLUNTEER_RECORD = "/volunteer/discover/record"

//志愿服务在发现页的展示信息
const val DISCOVER_VOLUNTEER_FEED = "/volunteer/discover/feed"

//电费模块服务
const val VOLUNTEER_SERVICE = "/volunteer/service"