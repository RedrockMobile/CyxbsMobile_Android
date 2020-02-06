package com.mredrock.cyxbs.mine.page.edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.getRequestBody
import com.mredrock.cyxbs.common.utils.extensions.loadAvatar
import com.mredrock.cyxbs.common.utils.extensions.uri
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.EditDialogFragment
import com.mredrock.cyxbs.mine.util.user
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.mine_activity_edit_info.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException


/**
 * Created by zzzia on 2018/8/14.
 * 编辑个人信息
 */
class EditInfoActivity(override val isFragmentActivity: Boolean = false,
                       override val viewModelClass: Class<EditViewModel> = EditViewModel::class.java)
    : BaseViewModelActivity<EditViewModel>() {

    private val SELECT_PICTURE = 1
    private val SELECT_CAMERA = 2

    private val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkColorAndText()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun checkColorAndText() {
        val userForTemporal = user ?: return
        if (checkIfInfoChange()) {
            mine_btn_info_save.apply {
                textColor = ContextCompat.getColor(context, R.color.mine_white)
                background = ResourcesCompat.getDrawable(resources, R.drawable.mine_bg_round_corner_blue_gradient, null)
                text = "保存"
                isClickable = true
            }
        } else {
            mine_btn_info_save.apply {
                textColor = ContextCompat.getColor(context, R.color.greyButtonText)
                background = ResourcesCompat.getDrawable(resources, R.drawable.mine_bg_round_corner_grey, null)
                text = "已保存"
                isClickable = false
            }
        }
        val nickname = mine_et_nickname.text.toString()
        val introduction = mine_et_introduce.text.toString()
        val qq = mine_et_qq.text.toString()
        val phone = mine_et_phone.text.toString()
        mine_tv_nickname.text = "昵称(${nickname.length}/8)"
        mine_tv_sign.text = "个性签名(${introduction.length}/20)"
        if (nickname != userForTemporal.nickname) {
            mine_et_nickname.textColor = ContextCompat.getColor(this, R.color.levelTwoFontColor)
        } else {
            mine_et_nickname.textColor = ContextCompat.getColor(this, R.color.greyText)
        }
        if (introduction != userForTemporal.introduction) {
            mine_et_introduce.textColor = ContextCompat.getColor(this, R.color.levelTwoFontColor)
        } else {
            mine_et_introduce.textColor = ContextCompat.getColor(this, R.color.greyText)
        }
        if (qq != userForTemporal.qq) {
            mine_et_qq.textColor = ContextCompat.getColor(this, R.color.levelTwoFontColor)
        } else {
            mine_et_qq.textColor = ContextCompat.getColor(this, R.color.greyText)
        }
        if (phone != userForTemporal.phone) {
            mine_et_phone.textColor = ContextCompat.getColor(this, R.color.levelTwoFontColor)
        } else {
            mine_et_phone.textColor = ContextCompat.getColor(this, R.color.greyText)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_edit_info)

        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@EditInfoActivity, R.color.windowBackground))
            initWithSplitLine("资料编辑",
                    false,
                    R.drawable.mine_ic_arrow_left,
                    View.OnClickListener {
                        if (checkIfInfoChange()) {
                            EditDialogFragment().show(supportFragmentManager, "SaveInfo")
                        } else {
                            finish()
                        }
                    })
            setTitleLocationAtLeft(true)
        }




        initObserver()
        loadAvatar(user?.photoThumbnailSrc, mine_edit_et_avatar)

        initData()
        setTextChangeListener()
        //点击更换头像
        mine_edit_et_avatar.setOnClickListener { changeAvatar() }
        //需调用一次给textView赋值
        checkColorAndText()
        mine_btn_info_save.setOnClickListener {
            saveInfo()
        }
        mine_btn_info_save.isClickable = false
    }

    override fun onBackPressed() {
        if (checkIfInfoChange()) {
            EditDialogFragment().show(supportFragmentManager, "SaveInfo")
        } else {
            super.onBackPressed()
        }
    }

    private fun initData() {
        val userForTemporal = user ?: return
        mine_et_nickname.setText(userForTemporal.nickname)
        mine_et_introduce.setText(userForTemporal.introduction)
        mine_et_qq.setText(userForTemporal.qq)
        mine_et_phone.setText(userForTemporal.phone)
    }

    private fun setTextChangeListener() {
        mine_et_nickname.addTextChangedListener(watcher)
        mine_et_introduce.addTextChangedListener(watcher)
        mine_et_qq.addTextChangedListener(watcher)
        mine_et_phone.addTextChangedListener(watcher)
    }

    private fun initObserver() {
        viewModel.updateInfoEvent.observe(this, Observer {
            if (it) {
                toast("更改资料成功")
            } else {
                toast("上传资料失败")
            }
        })

        viewModel.upLoadImageEvent.observe(this, Observer {
            if (it) {
                loadAvatar(user?.photoThumbnailSrc, mine_edit_et_avatar)
                toast("修改头像成功")
            } else {
                toast("修改头像失败")
            }
        })
    }

    private fun saveInfo() {
        val nickname = mine_et_nickname.text.toString()
        val introduction = mine_et_introduce.text.toString()
        val qq = mine_et_qq.text.toString()
        val phone = mine_et_phone.text.toString()

        //数据没有改变，不进行网络请求
        if (!checkIfInfoChange()) {
            return
        }

        if (nickname.isEmpty()) {
            toast("昵称不能为空")
            return
        }
        viewModel.updateUserInfo(nickname, introduction, qq, phone) { checkColorAndText() }
    }

    private fun checkIfInfoChange(): Boolean {
        val userForTemporal = user ?: return true
        val nickname = mine_et_nickname.text.toString()
        val introduction = mine_et_introduce.text.toString()
        val qq = mine_et_qq.text.toString()
        val phone = mine_et_phone.text.toString()
        if (nickname == userForTemporal.nickname &&
                introduction == userForTemporal.introduction &&
                qq == userForTemporal.qq &&
                phone == userForTemporal.phone) {
            return false
        }
        return true
    }

    //主要做清除焦点的处理
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            val v = currentFocus

            //如果不是落在EditText区域，则需要关闭输入法
            if (hideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                v?.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun hideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view != null && view is EditText) {

            val location = intArrayOf(0, 0)
            view.getLocationInWindow(location)

            //获取现在拥有焦点的控件view的位置，即EditText
            val left = location[0]
            val top = location[1]
            val bottom = top + view.height
            val right = left + view.width
            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
            val isInEt = (event.x > left && event.x < right && event.y > top
                    && event.y < bottom)
            return !isInEt
        }
        return false
    }

    /*下面是上传头像部分的代码*/

    private fun changeAvatar() {
        //获取权限
        doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            reason = "读取图片需要访问您的存储空间哦~"
            doAfterGranted {
                //检查目录
                val parent = File(fileDir)
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                //选择
                MaterialDialog.Builder(this@EditInfoActivity)
                        .items("拍照", "从相册中选择")
                        .itemsCallback { _, _, which, _ ->
                            if (which == 0) {
                                getImageFromCamera()
                            } else {
                                getImageFromAlbum()
                            }
                        }
                        .show()
            }
        }
    }

    private val fileDir by lazy {
        StringBuilder(Environment.getExternalStorageDirectory().path)
                .append(DIR_PHOTO)
                .toString()
    }
    private val cameraImageFile by lazy { File(fileDir + File.separator + System.currentTimeMillis() + ".png") }
    private val destinationFile by lazy { File(fileDir + File.separator + user?.stuNum + ".png") }

    private fun getImageFromCamera() {
        doPermissionAction(Manifest.permission.CAMERA) {
            reason = "拍照需要访问你的相机哦~"
            doAfterGranted {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageFile.uri)
                startActivityForResult(intent, SELECT_CAMERA)
            }
        }
    }

    //文件权限在点击头像框时已经获取到了，这里不需要再获取
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, SELECT_PICTURE)
    }

    private fun startCropActivity(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(destinationFile))
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        options.setCompressionQuality(100)
        options.setLogoColor(ContextCompat.getColor(this, R.color.mine_black_lightly))
        options.setToolbarColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark))
        options.setStatusBarColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark))
        uCrop.withOptions(options)
                .withAspectRatio(300f, 300f)
                .withMaxResultSize(300, 300)
                .start(this)
    }

    private fun handleCropError(result: Intent) {
        val cropError = UCrop.getError(result)
        if (cropError != null) {
            toast(cropError.message.toString())
        } else {
            toast("Unexpected error")
        }
    }

    private fun uploadImage(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri == null) {
            toast("无法获得裁剪结果")
            return
        }

        try {
            val fileBody = MultipartBody.Part.createFormData("fold", destinationFile.name, destinationFile.getRequestBody())
            val numBody = RequestBody.create(MediaType.parse("multipart/form-data"), user?.stuNum
                    ?: return)
            viewModel.uploadAvatar(numBody, fileBody)
        } catch (e: IOException) {
            e.printStackTrace()
            toast("图片加载失败")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (resultCode == UCrop.RESULT_ERROR && data != null) handleCropError(data)

        when (requestCode) {
            SELECT_PICTURE -> {
                val uri = data?.data
                if (uri != null) {
                    startCropActivity(uri)
                } else {
                    toast("无法识别该图像")
                }
            }
            SELECT_CAMERA -> {
                startCropActivity(cameraImageFile.uri)
            }
            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    uploadImage(data)
                } else {
                    toast("未知错误，请重试")
                }
            }
        }
    }
}
