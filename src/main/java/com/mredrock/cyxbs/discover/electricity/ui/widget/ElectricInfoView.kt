package com.mredrock.cyxbs.discover.electricity.ui.widget

import android.content.Context
import androidx.annotation.DrawableRes
import android.util.AttributeSet
import android.view.LayoutInflater
import com.mredrock.cyxbs.common.component.JCardViewPlus
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_view_info.view.*

/**
 * Author: Hosigus
 * Date: 2018/9/16 19:53
 * Description: com.mredrock.cyxbs.electricity.ui.widgt
 */
class ElectricInfoView : JCardViewPlus {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.electricity_view_info, this, true)
        val style = context.obtainStyledAttributes(attrs, R.styleable.ElectricInfoView)
        setDescribe(style.getString(R.styleable.ElectricInfoView_describe))
        setIcon(style.getResourceId(R.styleable.ElectricInfoView_icon, R.drawable.electricity_charge_ic_free))
        style.recycle()
    }

    fun setIcon(@DrawableRes resId: Int) {
        iv_icon.setImageResource(resId)
    }

    fun setDescribe(describe: String) {
        tv_info_describe.text = describe
    }

    fun setValue(value: String) {
        tv_info_value.text = value
    }
}