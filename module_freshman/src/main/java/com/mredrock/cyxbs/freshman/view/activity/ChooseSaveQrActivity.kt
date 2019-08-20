package com.mredrock.cyxbs.freshman.view.activity

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.freshman.R
import kotlinx.android.synthetic.main.freshman_activity_choose_save_qr.*

/**
 * Create by yuanbing
 * on 2019/8/8
 */
class ChooseSaveQrActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_choose_save_qr)

        initWindow()
        initButton()
    }

    private fun initButton() {
        btn_choose_save_qr_cancel.setOnClickListener { finish() }
        btn_choose_save_qr_save.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun initWindow() {
        val attr = window.attributes
        attr.gravity = Gravity.BOTTOM
        window.setBackgroundDrawableResource(R.color.freshman_transparent_bg)
    }
}