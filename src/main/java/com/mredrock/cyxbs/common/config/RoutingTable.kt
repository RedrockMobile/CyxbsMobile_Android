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
const val MAIN_MAIN = "/main/main"

const val DISCOVER_OTHER_COURSE = "/other_course/discover/entry"
const val DISCOVER_NO_CLASS = "/no_class/discover/entry"
const val DISCOVER_MAP = "/map/discover/entry"
const val DISCOVER_CALENDAR = "/calendar/discover/entry"
const val DISCOVER_ELECTRICITY = "/electricity/discover/entry"
const val DISCOVER_EMPTY_ROOM = "/empty_room/discover/entry"
const val DISCOVER_GRADES = "/grades/discover/entry"
const val DISCOVER_VOLUNTEER = "/volunteer/discover/entry"
const val DISCOVER_SCHOOL_CAR = "/school_car/discover/entry"
const val DISCOVER_NEWS = "/news/discover/entry"

// NoCourseInviteActivity
const val COURSE_NO_COURSE_INVITE = "/course/no_course_invite_activity"

const val COURSE_OTHER_COURSE = "/course/other_course_activity"

//大红页
const val REDROCK_HOME_ENTRY = "/redrock_home/entry"


//小控件设置
const val WIDGET_SETTING = "/setting/widget"

// QA
const val QA_QUIZ = "/quiz/qa/entry"
const val QA_ANSWER = "/answer/qa/entry"
const val QA_ANSWER_LIST = "/answer_list/qa/entry"