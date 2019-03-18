package com.mredrock.cyxbs.mine.network

/**
 * Created by zia on 2018/8/15.
 */
object Const {
    //获取个人信息
    const val API_GET_INFO_DETAIL = "/app/index.php/Home/Person/search"
    //更新个人信息
    const val API_EDIT_INFO = "/app/index.php/Home/Person/setInfo"
    //上传头像
    const val API_SOCIAL_IMG_UPLOAD = "/app/index.php/Home/Photo/uploadArticle"

    //签到
    const val CHECK_IN = "app/index.php/QA/Integral/checkIn"
    const val CHECK_IN_STATUS = "app/index.php/QA/Integral/getCheckInStatus"
    const val CHECK_IN_ACCOUNT = "app/index.php/QA/Integral/getDiscountBalance"
    //草稿箱
    const val DELETE_DRAFT = "app/index.php/QA/User/deleteItemInDraft"
    const val COMMENT_ANSWER = "app/index.php/QA/Answer/remark"
    const val REFRESH_DRAFT = "https://wx.idsbllp.cn/app/index.php/QA/User/updateItemInDraft"
    const val DRAFT_LIST = "app/index.php/QA/User/getDraftList"
    //问一问
    const val API_HELP_USER_ASK = "/app/index.php/QA/User/ask"
    //帮一帮
    const val API_HELP_USER_HELP = "/app/index.php/QA/User/help"
}