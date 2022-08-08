package com.mredrock.cyxbs.store.page.record.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.databinding.StoreActivityExchangeDetailBinding
import com.mredrock.cyxbs.store.utils.Date

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 11:39
 */
class ExchangeDetailActivity : BaseBindActivity<StoreActivityExchangeDetailBinding>() {

    companion object {
        fun activityStart(context: Context, data: ExchangeRecord) {
            val intent = Intent(context, ExchangeDetailActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    private lateinit var data: ExchangeRecord
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        data = intent.getSerializableExtra("data") as ExchangeRecord
    }

    private fun initView() {
        //绑定数据
        binding.data = data
        //判断是否领取 动态改变IV TV
        if (data.isReceived) {
            binding.storeIvExchangeOrderBg.setImageResource(R.drawable.store_ic_bg_claimed_exchange_order)
            binding.storeExchangeDetailState.text = "已领取"
        } else {
            binding.storeIvExchangeOrderBg.setImageResource(R.drawable.store_ic_bg_unclaimed_exchange_order)
            binding.storeExchangeDetailState.text = "待领取"
        }
        //设置时间
        binding.storeExchangeDetailTime.text = Date.getExactTime(data.date)

        //设置左上角返回点击事件
        val button: ImageButton = findViewById(R.id.store_iv_toolbar_no_line_arrow_left)
        button.setOnSingleClickListener {
            finish()
        }
    }
}