package com.mredrock.cyxbs.course.widget.page.trans

import android.content.Context
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences

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
        context.sharedPreferences(shareName).editor {
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
                timeTextColor = context.sharedPreferences(shareName).getString("timeTextColor", timeTextColor)
                        ?: "#FFFFFF"
                courseTextColor = context.sharedPreferences(shareName).getString("courseTextColor", courseTextColor)
                        ?: "#FFFFFF"
                roomTextColor = context.sharedPreferences(shareName).getString("roomTextColor", roomTextColor)
                        ?: "#FFFFFF"
                holderColor = context.sharedPreferences(shareName).getString("holderColor", holderColor)
                        ?: "#FFFFFF"
                timeTextSize = context.sharedPreferences(shareName).getInt("timeTextSize", timeTextSize)
                courseTextSize = context.sharedPreferences(shareName).getInt("courseTextSize", courseTextSize)
                roomTextSize = context.sharedPreferences(shareName).getInt("roomTextSize", roomTextSize)
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