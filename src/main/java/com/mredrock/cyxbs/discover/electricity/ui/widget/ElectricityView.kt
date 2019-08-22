package com.mredrock.cyxbs.discover.electricity.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.mredrock.cyxbs.electricity.R
import kotlinx.android.synthetic.main.electricity_view_electricity.view.*

/**
 * Create By Hosigus at 2019/3/30
 */
class ElectricityView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.electricity_view_electricity, this, true)
    }

    @SuppressLint("SetTextI18n")
    fun refresh(lastMoney: String, costPower: String) {
        tv_electric_money.text = lastMoney
        tv_electric_use.text = "$costPower 度"
        electric_circle_view.refresh(costPower.toInt())
    }
}