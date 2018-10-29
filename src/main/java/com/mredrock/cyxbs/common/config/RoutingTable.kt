package com.mredrock.cyxbs.common.config

/**
 * 路由表命名规则：
 * <ul>
 *     <li>常量名（全大写）：模块名_功能描述，例：QA_ENTRY</li>
 *     <li>二级路由：/模块名/功能描述，例：/qa/entry</li>
 *     <li>多级路由：/模块依赖关系倒置/功能描述，例：/map/discover/entry</li>
 * </ul>
 */
const val COURSE_ENTRY = "/course/entry"
const val QA_ENTRY = "/qa/entry"
const val DISCOVER_ENTRY = "/discover/entry"
const val MINE_ENTRY = "/mine/entry"

const val DISCOVER_OTHER_COURSE = "/other_course/discover/entry"
const val DISCOVER_NO_CLASS = "/no_class/discover/entry"
const val DISCOVER_MAP = "/map/discover/entry"
const val DISCOVER_CALENDER = "/calender/discover/entry"
const val DISCOVER_ELECTRICITY = "/electricity/discover/entry"
const val DISCOVER_EMPTY_ROOM = "/empty_room/discover/entry"
const val DISCOVER_GRADES = "/grades/discover/entry"
const val DISCOVER_VOLUNTEER = "/volunteer/discover/entry"
const val DISCOVER_SCHOOL_CAR = "/school_car/discover/entry"
const val DISCOVER_ABOUT_US = "/about_us/discover/entry"
const val DISCOVER_NEWS = "/news/discover/entry"
