package com.mredrock.cyxbs.qa.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.config.DIR
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.qa_activity_view_image_crop.*
import java.io.File

class ViewImageCropActivity : AppCompatActivity() {
    companion object {
        const val DEFAULT_RESULT_CODE = 0x1248
        const val EXTRA_NEW_PATH = "extra_new_path"
        private const val NEED_RESULT = "needResult"
        private const val IMG_RES_PATH = "imgResPath"
        const val DELETE_CODE = 0x123
        fun activityStart(context: Context, imgResUrl: String) {
            context.startActivity<ViewImageCropActivity>(IMG_RES_PATH to imgResUrl)
        }

        fun activityStartForResult(context: Activity, imgResPath: String, resultCode: Int = DEFAULT_RESULT_CODE) {
            context.startActivityForResult<ViewImageCropActivity>(resultCode, IMG_RES_PATH to imgResPath, NEED_RESULT to true)
        }

        fun activityStartForResult(context: Fragment, imgResPath: String, resultCode: Int = DEFAULT_RESULT_CODE) {
            context.startActivityForResult<ViewImageCropActivity>(resultCode, IMG_RES_PATH to imgResPath, NEED_RESULT to true)
        }
    }


    private var needResult: Boolean = false
    private var imgResPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_view_image_crop)

        setFullScreen()

        needResult = intent.getBooleanExtra(NEED_RESULT, false)
        imgResPath = intent.getStringExtra(IMG_RES_PATH)?:""

        tv_delete.setOnSingleClickListener {
            setResult(DELETE_CODE)
            finish()
        }
        if (needResult) {
            tv_edit.visible()
            tv_edit.setOnSingleClickListener {
                startCropActivity()
                tv_delete.gone()
            }
        }

        Glide.with(this)
                .load(imgResPath)
                .apply(RequestOptions()
                        .placeholder(com.mredrock.cyxbs.common.R.drawable.common_ic_place_holder)
                        .error(com.mredrock.cyxbs.common.R.drawable.common_ic_place_holder))
                .into(iv)
    }

    private val resultPath by lazy {
        val path = File(StringBuilder(Environment.getExternalStorageDirectory().path)
                .append(DIR)
                .append(File.separatorChar)
                .append("crop")
                .toString())
        if (!path.exists()) {
            path.mkdirs()
        }
        path
    }
    private var cropResultFilePath: File? = null
    private fun getNewResultUri(): Uri {
        val name = StringBuilder(imgResPath.split(File.separatorChar).last().split(".").first())
                .append(System.currentTimeMillis())
                .append(".png")
                .toString()
        cropResultFilePath = File(resultPath, name)
        return Uri.fromFile(cropResultFilePath)
    }

    private fun startCropActivity() = UCrop.of(Uri.fromFile(File(imgResPath)), getNewResultUri())
            .withOptions(UCrop.Options().apply {
                setLogoColor(ContextCompat.getColor(this@ViewImageCropActivity, R.color.qa_crop_logo_color))
                setToolbarColor(
                        ContextCompat.getColor(this@ViewImageCropActivity, R.color.colorPrimaryDark))
                setStatusBarColor(
                        ContextCompat.getColor(this@ViewImageCropActivity, R.color.colorPrimaryDark))
            })
            .start(this)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == UCrop.RESULT_ERROR && data != null) {
            val cropError = UCrop.getError(data)
            if (cropError != null) {
                toast(cropError.message.toString())
            } else {
                toast("Unexpected error")
            }
        } else if (resultCode == Activity.RESULT_OK && data != null) {
            imgResPath = cropResultFilePath?.absolutePath ?: ""
            Glide.with(this)
                    .load(imgResPath)
                    .apply(RequestOptions().placeholder(com.mredrock.cyxbs.common.R.drawable.common_ic_place_holder)
                            .error(com.mredrock.cyxbs.common.R.drawable.common_ic_place_holder))
                    .into(iv)
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_NEW_PATH, imgResPath))
        } else {
            toast("无法获得裁剪结果")
        }
    }

}
