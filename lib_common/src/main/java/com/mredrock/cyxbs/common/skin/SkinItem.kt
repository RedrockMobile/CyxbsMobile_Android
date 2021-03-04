package com.mredrock.cyxbs.common.skin

import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView


class SkinItem(private val view: View?, private val attrs: List<SkinAttr>?) {

    /**
    更换皮肤
     */
    fun apply() {
        if (view == null || attrs == null) return
        for (attr in attrs) {
            val attrName = attr.attrName

            val attrType = attr.attrType
            val resName = attr.resName
            val resId = attr.resId

            when (attrName) {

                "background" -> {
                    if ("color" == attrType) {
                        view.setBackgroundColor(
                                SkinManager.instance.getColor(resName, resId)
                        )
                    } else if ("drawable" == attrType) {
                        view.background =
                                SkinManager.instance.getDrawable(resName, resId)
                    }
                }

                "textColor" -> {
                    if (view is TextView && "color" == attrType) {
                        view.setTextColor(
                                SkinManager.instance.getColor(resName, resId)
                        )
                    }
                }

                "src" -> {
                    if (view is ImageView) {
                        view.setImageDrawable(
                                SkinManager.instance.getDrawable(resName, resId)
                        )
                    }
                }

                "track" -> {
                    if (view is Switch) {
                        view.trackDrawable = SkinManager.instance.getDrawable(resName, resId)
                    }
                }
            }

        }
    }

}