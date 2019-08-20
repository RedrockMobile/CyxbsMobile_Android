package com.mredrock.cyxbs.freshman.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.config.INTENT_MESSAGE
import com.mredrock.cyxbs.freshman.config.INTENT_QR
import com.mredrock.cyxbs.freshman.interfaces.model.IActivitySaveQRModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivitySaveQRPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivitySaveQRView
import com.mredrock.cyxbs.freshman.presenter.ActivitySaveQRPresenter
import kotlinx.android.synthetic.main.freshman_activity_save_qr.*
import org.jetbrains.anko.toast
import java.io.File

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class SaveQRActivity : BaseActivity<IActivitySaveQRView, IActivitySaveQRPresenter, IActivitySaveQRModel>(),
        IActivitySaveQRView {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_save_qr)

        initView()
    }

    private fun initView() {
        iv_save_qr_qr.setImageFromUrl(intent.getStringExtra(INTENT_QR))
        tv_save_qr_description.text = intent.getStringExtra(INTENT_MESSAGE)

        val detector = GestureDetector(object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                val intent = Intent(this@SaveQRActivity, ChooseSaveQrActivity::class.java)
                startActivityForResult(intent, 0)
            }
        })
        iv_save_qr_qr.setOnTouchListener { _, motionEvent -> detector.onTouchEvent(motionEvent) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK)  {
                val bitmapDrawable = iv_save_qr_qr.drawable as BitmapDrawable
                doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    doAfterGranted { presenter?.saveBitmapToDisk(bitmapDrawable.bitmap,
                            intent.getStringExtra(INTENT_QR)) }
                }
            }
        }
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivitySaveQRPresenter()

    override fun saveSuccess(file: File) {
        val intent = Intent()
        intent.action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
        intent.data = Uri.fromFile(file)
        sendBroadcast(intent)
        toast("保存成功")
    }
}
