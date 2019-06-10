package com.mredrock.cyxbs.common.config

/**
 * Created By jay68 on 2018/8/10.
 */

const val DIR = "/cyxbs"
const val DIR_PHOTO = "/cyxbs/photo"
const val DIR_FILE = "/cyxbs/file"
const val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"
const val PREF_USER_LOGIN_ALREADY = "login_already"
const val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"

val dataFilePath = android.os.Environment.getExternalStorageDirectory().toString() + "/" + "Android/data/com.mredrock.cyxbs/"
val updateFilePath = android.os.Environment.getExternalStorageDirectory().toString() + "/" + "Download/"
const val updateFilename = "com.mredrock.cyxbs.apk"

const val APP_WIDGET_CACHE_FILE_NAME = "AppWidgetCache.json"

/**
 * SharedPreferences key for encrypt version of user
 *
 * use by [com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption] for the current encrypt version,
 * you can getUpdateInfo the encrypt method in the future and keep compatibility
 * @see com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption.onUpdate
 */
const val SP_KEY_ENCRYPT_VERSION_USER = "encrypt_version_user"

/**
 * SharedPreferences value for encrypt version of user
 *
 * use by [com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption] for the current encrypt version,
 * you can getUpdateInfo the encrypt method in the future and keep compatibility
 * @see com.mredrock.cyxbs.common.utils.encrypt.UserInfoEncryption.onUpdate
 */
const val USER_INFO_ENCRYPT_VERSION = 1
const val SP_KEY_USER = "cyxbsmobile_user"

const val DEFAULT_PREFERENCE_FILENAME = "share_data"

//在课表上没课的地方显示备忘录
const val SP_SHOW_MODE = "showMode"

//连续签到每日提醒
const val SP_SIGN_REMIND = "signRemind"

const val APP_WEBSITE = "https://wx.idsbllp.cn/app/"

//小控件课表
const val WIDGET_COURSE = "course_widget"
const val SP_WIDGET_NEED_FRESH = "sharepreference_widget_need_fresh"

// uri跳转打开应用
    // QA
const val URI_HOST_QA = "/qa"
        // question
const val URI_PATH_QA_QUESTION = "/question"
        // answer
const val URI_PATH_QA_ANSWER = "/answer"