package com.mredrock.cyxbs.volunteer.widget

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences

/**
 * Created by glossimar on 2018/10/24.
 */

class VolunteerTimeSP(private val mContext: Context) {
    private val sharedPref: SharedPreferences
    private val editor: SharedPreferences.Editor

    val volunteerUid: String
        get() = sharedPref.getString("uid", "404")

    val volunteerAccount: String
        get() = sharedPref.getString("account", "404")

    val volunteerPassword: String
        get() = sharedPref.getString("password", "404")

    init {
        sharedPref = mContext.defaultSharedPreferences
        editor = sharedPref.edit()
    }

    fun bindVolunteerInfo(account: String, password: String, uid: String) {
        editor.putString("account", account)
        editor.putString("password", password)
        editor.putString("uid", uid)
        editor.commit()
    }

    fun unBindVolunteerInfo() {
        editor.putString("account", "404")
        editor.putString("password", "404")
        editor.putString("uid", "404")
        editor.commit()
    }

    fun removeVolunteerInfo() {
        editor.remove("account")
        editor.remove("password")
        editor.remove("uid")
        editor.commit()
    }

    fun isBind() = !(volunteerUid == "404" || volunteerAccount == "404" ||
            volunteerPassword == "404")
}
