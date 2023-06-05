package com.mredrock.cyxbs.common.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.R

/**
 * @author Jovines
 * @date 2019/12/4 21:14
 * description：一个顶部有圆角，并且有轻微蓝色阴影,使用方法和标准的bottomSheetDialog一样
 * bottomSheetDialog里面要显示的View设置进去就好
 */
open class RedRockBottomSheetDialog(context: Context) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {
    override fun setContentView(view: View) {
        val viewGroup = LayoutInflater.from(context)
            .inflate(
                R.layout.common_red_rock_bottom_sheet_dalog_container,
                window!!.decorView as ViewGroup, // 使用它是为了解析参数，除此以外没其他作用
                false
            ) as ViewGroup
        viewGroup.addView(view)
        super.setContentView(viewGroup)
    }
    
    override fun setContentView(layoutResId: Int) {
        val viewGroup = LayoutInflater.from(context)
            .inflate(
                R.layout.common_red_rock_bottom_sheet_dalog_container,
                window!!.decorView as ViewGroup, // 使用它是为了解析参数，除此以外没其他作用
                false
            ) as ViewGroup
        // 第三个参数 attachToRoot 是 true，它会 layoutResId 对应的 View 添加进 viewGroup 中
        LayoutInflater.from(context).inflate(layoutResId, viewGroup, true)
        super.setContentView(viewGroup, viewGroup.layoutParams)
    }
    
    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        val viewGroup = LayoutInflater.from(context)
            .inflate(R.layout.common_red_rock_bottom_sheet_dalog_container, null) as ViewGroup
        viewGroup.addView(view)
        super.setContentView(viewGroup, params)
    }
    
    override fun <T : View> findViewById(id: Int): T {
        return super.findViewById(id)!!
    }

}