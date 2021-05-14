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

const val MAIN_SPLASH = "/main/splash"

const val DISCOVER_OTHER_COURSE = "/other_course/discover/entry"
const val DISCOVER_NO_CLASS = "/no_class/discover/entry"
const val DISCOVER_MAP = "/map/discover/entry"
const val DISCOVER_CALENDAR = "/calendar/discover/entry"
const val DISCOVER_EMPTY_ROOM = "/empty_room/discover/entry"
const val DISCOVER_GRADES = "/grades/discover/entry"
const val DISCOVER_VOLUNTEER = "/volunteer/discover/entry"
const val DISCOVER_VOLUNTEER_RECORD = "/volunteer/discover/record"
const val DISCOVER_SCHOOL_CAR = "/school_car/discover/entry"
const val DISCOVER_NEWS = "/news/discover/entry"
const val DISCOVER_MORE_FUNCTION = "/more_function/discover/entry"

//志愿服务在发现页的展示信息
const val DISCOVER_VOLUNTEER_FEED = "/volunteer/discover/feed"

//查电费在发现页面的展示信息
const val DISCOVER_ELECTRICITY_FEED = "/electricity/discover/feed"

//教务新闻子项入口
const val DISCOVER_NEWS_ITEM = "/news/discover/item"

//签到页
const val MINE_CHECK_IN = "/check_in/mine/entry"
// 个人界面的确认密码模块
const val MINE_FORGET_PASSWORD = "/forget_password/mine/entry"
// 个人界面的绑定邮箱模块
const val MINE_BIND_EMAIL = "/bind_email/mine/entry"

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
const val QA_CIRCLE_DETAIL = "/circle_detail/qa/entry"
const val QA_COMMENT_LIST = "/comment_list/qa/entry"
const val QA_DYNAMIC_DETAIL = "/dynamic_detail/qa/entry"
const val QA_DYNAMIC_MINE = "/dynamic_mine/qa/entry"
const val QA_MY_COMMENT = "/my_comment/qa/entry"
const val QA_MY_IGNORE = "/my_ignore/qa/entry"
const val QA_MY_PRAISE = "/my_praise/qa/entry"

// 迎新专题
const val FRESHMAN_ENTRY = "/freshman/entry"

// 更新服务
const val APP_UPDATE_SERVICE = "/update/service"

//主模块服务
const val MAIN_SERVICE = "/main/service"

//发现模块服务
const val DISCOVER_SERVICE = "/discover/service"

//电费模块服务
const val ELECTRICITY_SERVICE = "/electricity/service"

//电费模块服务
const val VOLUNTEER_SERVICE = "/volunteer/service"