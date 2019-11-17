package com.mredrock.cyxbs.common.bean

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences

/**
 * Created By jay68 on 2019-11-13.
 *
 * 做适配，适配完了以后记得删除
 */

@Deprecated(message = "User逻辑已迁移至lib_account，common不再提供User", replaceWith = ReplaceWith("ServiceManager.getService(IAccountService::class.java).getUserService()", "com.mredrock.cyxbs.common.service.ServiceManager", "com.mredrock.cyxbs.common.service.account.IAccountService"), level = DeprecationLevel.WARNING)
class User {
    private val userService get() = ServiceManager.getService(IAccountService::class.java).getUserService()
    val nickname get() = userService.getNickname()
    val stunum get() = userService.getStuNum()
    val stuNum get() = stunum
    val name get() = userService.getRealName()
    val gender get() = userService.getGender()

    val idNum get() = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    val introduction get() = userService.getIntroduction()
    val photoThumbnailSrc get() = userService.getAvatarImgUrl()
    val qq get() = userService.getQQ()
    val phone get() = userService.getPhone()
    val photoSrc get() = photoThumbnailSrc

}