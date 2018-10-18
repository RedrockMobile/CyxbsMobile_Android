package com.mredrock.cyxbs.mine.network

/**
 * Created by zia on 2018/8/15.
 */
object Const {
    //获取个人信息
    const val API_GET_INFO_DETAIL = "/cyxbsMobile/index.php/Home/Person/search"
    //更新个人信息
    const val API_EDIT_INFO = "/cyxbsMobile/index.php/Home/Person/setInfo"
    //上传头像
    const val API_SOCIAL_IMG_UPLOAD = "/cyxbsMobile/index.php/Home/Photo/uploadArticle"

    //签到
    const val CHECK_IN = "springtest/cyxbsMobile/index.php/QA/Integral/checkIn"
    const val CHECK_IN_STATUS = "springtest/cyxbsMobile/index.php/QA/Integral/getCheckInStatus"
    const val CHECK_IN_ACCOUNT = "springtest/cyxbsMobile/index.php/QA/Integral/getDiscountBalance"
    //草稿箱
    const val DELETE_DRAFT = "springtest/cyxbsMobile/index.php/QA/User/deleteItemInDraft"
    const val COMMENT_ANSWER = "springtest/cyxbsMobile/index.php/QA/Answer/remark"
    const val REFRESH_DRAFT = "https://wx.idsbllp.cn/springtest/cyxbsMobile/index.php/QA/User/updateItemInDraft"
    const val DRAFT_LIST = "springtest/cyxbsMobile/index.php/QA/User/getDraftList"
    //问一问
    const val API_HELP_USER_ASK = "/springtest/cyxbsMobile/index.php/QA/User/ask"
    //帮一帮
    const val API_HELP_USER_HELP = "/springtest/cyxbsMobile/index.php/QA/User/help"
}