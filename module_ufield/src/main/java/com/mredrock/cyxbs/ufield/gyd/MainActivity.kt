package com.mredrock.cyxbs.ufield.gyd

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.ufield.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity:BaseActivity(){

        private  val captureButton by R.id.capture_button.view<Button>()
        private val imageView by R.id.image_view.view<ImageView>()
        private lateinit var photoUri: Uri

        // 定义所需的权限和请求码
        private val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private val permissionsRequestCode = 100


    private val takePhotoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startCropActivity(photoUri)
        } else {
            // 拍照取消或失败
            Toast.makeText(this, "拍照取消或失败", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
            } else {
                // 已经拥有权限，直接启动相机
                launchCamera()
            }
        }
    }


    // 处理权限请求结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 检查权限请求码
        if (requestCode == permissionsRequestCode) {
            // 检查权限授予结果
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已被授予，启动相机
                launchCamera()
            } else {
                // 权限被拒绝，显示一个提示或执行其他操作
                Toast.makeText(this, "需要相机和存储权限才能继续操作", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            imageView.setImageURI(resultUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createPhotoFile()
        photoUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".fileProvider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePhotoResultLauncher.launch(intent)
    }

    private fun createPhotoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun createDestinationFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "cropped_$timeStamp.jpg")
    }
    private fun startCropActivity(uri: Uri) {
        val destinationFile = createDestinationFile()
        val uCrop = UCrop.of(uri, Uri.fromFile(destinationFile))
        val options = UCrop.Options()
        options.setCropGridStrokeWidth(5)
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        options.setCompressionQuality(100)
        options.setLogoColor(
            ContextCompat.getColor(
                this,
                com.mredrock.cyxbs.common.R.color.common_level_two_font_color
            )
        )
        options.setCircleDimmedLayer(true)
        options.setToolbarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.config.R.color.colorPrimaryDark)
        )
        options.setStatusBarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.config.R.color.colorPrimaryDark)
        )
        uCrop.withOptions(options)
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(convertDpToPx(100), convertDpToPx(100))
            .start(this)
    }
    private fun convertDpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}