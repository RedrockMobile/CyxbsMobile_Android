package com.mredrock.cyxbs.common.config

/**
 * @author Jovines
 * create 2020-08-16 6:36 PM
 * description: 埋点自定义事件
 */
object CyxbsMob {

    object Event{
        const val COURSE_SHOW = "course_show"
        const val CLICK_COURSE_ITEM = "click_course_item"
        const val CLICK_ASK = "click_ask"
    }

    object Key{
        const val IS_HEAD = "is_head"
    }

    object Value{
        const val TRUE = "true"
        const val FALSE = "false"
    }
}