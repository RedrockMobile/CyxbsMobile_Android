package com.mredrock.cyxbs.widget.widget.page.trans

import android.content.Context
import androidx.core.content.edit

/**
 * Created by zia on 2018/10/11.
 */
class TransConfig {

    private val shareName = "cyxbs_widget_trans_config"

    var timeTextSize = 15
    var courseTextSize = 19
    var roomTextSize = 15


    var timeTextColor = "#FFFFFF"


    var courseTextColor = "#FFFFFF"


    var roomTextColor = "#FFFFFF"


    var holderColor = "#FFFFFF"

    fun save(context: Context) {
        context.getSharedPreferences(shareName, Context.MODE_PRIVATE).edit {
            putInt("timeTextSize", timeTextSize)
            putInt("courseTextSize", courseTextSize)
            putInt("roomTextSize", roomTextSize)
            putString("timeTextColor", timeTextColor)
            putString("courseTextColor", courseTextColor)
            putString("roomTextColor", roomTextColor)
            putString("holderColor", holderColor)
        }
    }

    override fun toString(): String {
        return "TransConfig(timeTextSize=$timeTextSize, courseTextSize=$courseTextSize, roomTextSize=$roomTextSize, timeTextColor='$timeTextColor', courseTextColor='$courseTextColor', roomTextColor='$roomTextColor', holderColor='$holderColor')"
    }


    companion object {

        fun getUserConfig(context: Context): TransConfig {
            return TransConfig().apply {
                timeTextColor = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getString("timeTextColor", timeTextColor)
                    ?: "#FFFFFF"
                courseTextColor = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getString("courseTextColor", courseTextColor)
                    ?: "#FFFFFF"
                roomTextColor = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getString("roomTextColor", roomTextColor)
                    ?: "#FFFFFF"
                holderColor = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getString("holderColor", holderColor)
                    ?: "#FFFFFF"
                timeTextSize = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getInt("timeTextSize", timeTextSize)
                courseTextSize = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getInt("courseTextSize", courseTextSize)
                roomTextSize = context.getSharedPreferences(shareName, Context.MODE_PRIVATE)
                    .getInt("roomTextSize", roomTextSize)
            }
        }

        fun getDefaultWhite(): TransConfig {
            return TransConfig()
        }

        fun getDefaultBlack(): TransConfig {
            return TransConfig().apply {
                timeTextColor = "#000000"
                courseTextColor = "#000000"
                roomTextColor = "#000000"
                holderColor = "#E7E7E7"
            }
        }

        fun getDefaultPink(): TransConfig {
            return TransConfig().apply {
                timeTextColor = "#F1AAA6"
                courseTextColor = "#FF6E97"
                roomTextColor = "#FF6E97"
                holderColor = "#E2E2E2"
            }
        }
    }
}