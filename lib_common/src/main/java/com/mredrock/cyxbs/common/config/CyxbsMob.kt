package com.mredrock.cyxbs.common.config

/**
 * @author Jovines
 * create 2020-08-16 6:36 PM
 * description: 埋点自定义事件
 */
object CyxbsMob {

    object Event {
        const val COURSE_SHOW = "course_show"
        const val CLICK_COURSE_ITEM = "click_course_item"
        const val CLICK_ASK = "click_ask"
        const val SWITCH_QA_PAGE = "switch_qa_page"
        const val BOTTOM_TAB_CLICK = "bottom_tab_click"
        const val CLICK_QA_QUESTION = "click_qa_question"
        const val QA_SEARCH = "qa_search"
        const val QA_SEARCH_RECOMMEND = "qa_search_recommend"
    }

    object Key {
        const val IS_HEAD = "is_head"
        const val QA_PAGE = "qa_page"
        const val TAB_INDEX = "tab_index"
    }

    object Value {
        const val TRUE = "true"
        const val FALSE = "false"
    }
}