package com.mredrock.cyxbs.common.skin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import java.util.*

/**
 * 自定义一个创建view的工厂，收集需要换肤的view。
 */
class MySkinFactory : LayoutInflater.Factory {
    private val skinItems: MutableList<SkinItem> =
            ArrayList()

    override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
    ): View? {
        //这里要返回可以为空的，不然会报错
        val view = createView(name, context, attrs)
        if (view != null) {
            collectViewAttr(view, context, attrs)
        }
        return view
    }

    private fun createView(
            name: String,
            context: Context,
            attrs: AttributeSet
    ): View? {
        var view: View? = null
        try {
            if (-1 == name.indexOf('.')) {    //不带".",说明是系统的View
                if ("View" == name) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs)
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs)
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs)
                }
            } else {    //带".",说明是自定义的View
                view = LayoutInflater.from(context).createView(name, null, attrs)
            }
        } catch (e: Exception) {
            view = null
        }
        return view
    }

    private fun collectViewAttr(
            view: View,
            context: Context,
            attrs: AttributeSet
    ) {
        val skinAttrs: MutableList<SkinAttr> = ArrayList()
        val attCount = attrs.attributeCount
        for (i in 0 until attCount) {
            try {
                val attributeName = attrs.getAttributeName(i)
                val attributeValue = attrs.getAttributeValue(i)
                if (isSupportedAttr(attributeName)) {
                    if (attributeValue.startsWith("@")) {
                        val resId = attributeValue.substring(1).toInt()
                        val resName = context.resources.getResourceEntryName(resId)
                        val attrType = context.resources.getResourceTypeName(resId)
                        skinAttrs.add(SkinAttr(attributeName, attrType, resName, resId))
                        val skinItem = SkinItem(view, skinAttrs)
                        if (SkinManager.instance.isExternalSkin()) {
                            skinItem.apply()
                        }
                        skinItems.add(skinItem)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun isSupportedAttr(attributeName: String): Boolean {
        return "background" == attributeName
                || "textColor" == attributeName
                || "src" == attributeName
                || "track" == attributeName
    }

    fun apply() {
        for (item in skinItems) {
            item.apply()
        }
    }
}