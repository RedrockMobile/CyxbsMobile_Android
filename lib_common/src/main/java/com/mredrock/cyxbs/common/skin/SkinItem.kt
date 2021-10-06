package com.mredrock.cyxbs.common.skin

import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.tabs.TabLayout


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
                                SkinManager.getColor(resName, resId)
                        )
                    } else if ("drawable" == attrType) {
                        view.background =
                                SkinManager.getDrawable(resName, resId)
                    }
                }

                "textColor" -> {
                    if (view is TextView && "color" == attrType) {
                        view.setTextColor(
                                SkinManager.getColor(resName, resId)
                        )
                    } else if (view is AppCompatEditText) {
                        view.setTextColor(SkinManager.getColor(resName, resId))
                    }
                }

                "textColorHint" -> {
                    if (view is TextView) {
                        view.setHintTextColor(
                                SkinManager.getColor(resName, resId)
                        )
                    }
                    if (view is EditText) {
                        view.setHintTextColor(
                                SkinManager.getColor(resName, resId)
                        )
                    }
                }


                "src" -> {
                    if (view is ImageView) {
                        view.setImageDrawable(
                                SkinManager.getDrawable(resName, resId)
                        )
                    }
                }

                "srcCompat" -> {
                    if (view is ImageView) {
                        view.setImageDrawable(
                                SkinManager.getDrawable(resName, resId)
                        )
                    }
                }

                "track" -> {
                    if (view is Switch) {
                        view.trackDrawable = SkinManager.getDrawable(resName, resId)
                    }
                }

                //主页底部的icon
                "drawableTop" -> {
                    if (view is CheckedTextView) {
                        val drawable = SkinManager.getDrawable(resName, resId)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        view.setCompoundDrawables(null, drawable, null, null)
                    }
                }

                "drawableStart" -> {
                    if (view is TextView) {
                        val drawable = SkinManager.getDrawable(resName, resId)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        view.setCompoundDrawables(drawable, null, null, null)
                    }
                }

                "drawableEnd" -> {
                    if (view is TextView) {
                        val drawable = SkinManager.getDrawable(resName, resId)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        view.setCompoundDrawables(null, null, drawable, null)
                    }
                }
                "tabIndicatorColor" -> {
                    if (view is TabLayout) {
                        view.setSelectedTabIndicatorColor(SkinManager.getColor(resName, resId))
                    }
                }
            }

        }
    }

}