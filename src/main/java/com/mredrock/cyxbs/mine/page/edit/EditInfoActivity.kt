package com.mredrock.cyxbs.mine.page.edit

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.getRequestBody
import com.mredrock.cyxbs.common.utils.extensions.loadAvatar
import com.mredrock.cyxbs.common.utils.extensions.uri
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.setAutoGravity
import com.mredrock.cyxbs.mine.util.user
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.mine_activity_edit_info.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    private val editTextList by lazy { arrayOf(mine_edit_nickname, mine_edit_introduce, mine_edit_phone, mine_edit_qq) }

    private val SELECT_PICTURE = 1
    private val SELECT_CAMERA = 2
    private var isEdit = true //toolbar的字是否是编辑
    private var currentEditText: EditText? = null//当前编辑的edtText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_edit_info)

        mine_edit_toolbar.init("修改信息")

        initObserver()

        loadAvatar(user?.photoThumbnailSrc, mine_edit_avatarImageView)
        initData()

        //点击更换头像
        mine_edit_avatar.setOnClickListener { changeAvatar() }

        //点击编辑/保存
        mine_edit_toolbarEdit.setOnClickListener {
            currentEditText?.background = null
            if (isEdit) {
                //点击编辑按钮
                enableEdit()
                isEdit = !isEdit
            } else {
                //点击保存按钮
                saveInfo()
            }
        }

        mine_edit_introduce.setAutoGravity()
    }

    private fun initData() {
        mine_edit_nickname.setText(user!!.nickname)
        mine_edit_introduce.setText(user!!.introduction)
        mine_edit_qq.setText(user!!.qq)
        mine_edit_phone.setText(user!!.phone)
    }

    private fun initObserver() {
        viewModel.updateInfoEvent.observe(this, Observer {
            it!!
            if (it) {
                toast("更改资料成功")
                disableEdit()
                isEdit = !isEdit
                mine_edit_toolbarEdit.isClickable = true
            } else {
                toast("上传资料失败")
                disableEdit()
                isEdit = !isEdit
                mine_edit_toolbarEdit.isClickable = true
            }
        })

        viewModel.upLoadImageEvent.observe(this, Observer {
            it!!
            if (it) {
                loadAvatar(user!!.photoThumbnailSrc, mine_edit_avatarImageView)
                toast("修改头像成功")
            } else {
                toast("修改头像失败")
            }
        })
    }

    private fun saveInfo() {
        val nickname = mine_edit_nickname.text.toString()
        val introduction = mine_edit_introduce.text.toString()
        val qq = mine_edit_qq.text.toString()
        val phone = mine_edit_phone.text.toString()

        //数据没有改变，不进行网络请求
        if (nickname == user!!.nickname &&
                introduction == user!!.introduction &&
                qq == user!!.qq &&
                phone == user!!.phone) {
            disableEdit()
            isEdit = !isEdit
            return
        }

        if (nickname.isEmpty()) {
            toast("昵称不能为空")
            return
        }

        mine_edit_toolbarEdit.isClickable = false

        viewModel.updateUserInfo(nickname, introduction, qq, phone)
    }

    private fun enableEdit() {
        editTextList.forEach {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.setOnFocusChangeListener { _, _ ->
                it.setBackgroundResource(R.drawable.mine_bg_edit)
                currentEditText?.background = null
                currentEditText = it
            }
        }
        editTextList[0].requestFocus()
        mine_edit_toolbarEdit.text = "保存"
    }

    private fun disableEdit() {
        editTextList.forEach {
            it.isFocusable = false
            it.isFocusableInTouchMode = false
            it.setOnClickListener(null)
        }
        mine_edit_toolbarEdit.text = "编辑"
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
        StringBuilder(Environment.getExternalStorageDirectory().toString())
                .append(DIR_PHOTO)
                .toString()
    }
    private val cameraImageFile by lazy { File(fileDir + File.separator + System.currentTimeMillis() + ".png") }
    private val destinationFile by lazy { File(fileDir + File.separator + user!!.stuNum + ".png") }

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
                .withAspectRatio(300f,300f)
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
            val numBody = RequestBody.create(MediaType.parse("multipart/form-data"), user!!.stuNum!!)
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
