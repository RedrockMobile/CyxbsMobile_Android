package com.mredrock.cyxbs.qa.config

/**
 *@author zhangzhe
 *@date 2020/12/19
 *@description
 */

object RequestResultCode {
    /**
     * request
     */
    // 去详细界面的请求
    const val DYNAMIC_DETAIL_REQUEST = 1

    // 发帖子的请求
    const val RELEASE_DYNAMIC_ACTIVITY_REQUEST = 2

    // 发回复的请求
    const val RELEASE_COMMENT_ACTIVITY_REQUEST = 3

    // 去回复详细的界面的请求
    const val REPLY_DETAIL_REQUEST = 4

    /**
     * result
     */
    // 需要刷新动态首页
    const val NEED_REFRESH_RESULT = 1
    const val RESULT_CODE = 5

    /*
    判断邮问知识库是否被点击
    */
     var ClickKnowledge=false
}