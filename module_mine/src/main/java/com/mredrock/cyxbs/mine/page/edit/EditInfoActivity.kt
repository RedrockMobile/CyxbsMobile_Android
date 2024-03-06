package com.mredrock.cyxbs.mine.page.edit

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.config.MINE_EDIT_INFO
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.config.view.JToolbar
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.DynamicRVAdapter
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


/**
 * Created by zzzia on 2018/8/14.
 * 编辑个人信息
 */
@Route(path = MINE_EDIT_INFO)
class EditInfoActivity : BaseActivity() {

    private val mine_et_name by R.id.mine_tv_name.view<TextView>()
    private val mine_et_introduce by R.id.mine_tv_number.view<TextView>()
    private val mine_et_gender by R.id.mine_et_gender.view<TextView>()
    private val mine_et_college by R.id.mine_et_college.view<TextView>()
    private val mine_edit_et_avatar by R.id.mine_edit_et_avatar.view<CircleImageView>()
    private val mine_edit_iv_agreement by R.id.mine_edit_iv_agreement.view<ImageView>()
    val common_toolbar by com.mredrock.cyxbs.config.R.id.toolbar.view<JToolbar>()
    private val viewModel by lazy { ViewModelProvider(this)[EditViewModel::class.java] }

    private val userService: IUserService by lazy {
        IAccountService::class.impl.getUserService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(android.R.color.transparent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_edit_info)
        viewModel.getUserData()

        common_toolbar.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    this@EditInfoActivity,
                    com.mredrock.cyxbs.common.R.color.common_window_background
                )
            )
            initWithSplitLine("资料编辑",
                false,
                R.drawable.mine_ic_arrow_left,
                View.OnClickListener {
                    finishAfterTransition()
                })
            setTitleLocationAtLeft(true)
        }


        initObserver()

        initViewAndData()

    }

    protected fun JToolbar.initWithSplitLine(
        title: String,
        withSplitLine: Boolean = true,
        @DrawableRes icon: Int = com.mredrock.cyxbs.common.R.drawable.common_ic_back,
        listener: View.OnClickListener? = View.OnClickListener { finish() },
        titleOnLeft: Boolean = true
    ) {
        init(this@EditInfoActivity, title, withSplitLine, icon, titleOnLeft, listener)
    }

    private fun initViewAndData() {
        refreshUserInfo()

        //点击更换头像
        mine_edit_et_avatar.setOnSingleClickListener { changeAvatar() }

        mine_edit_iv_agreement.setOnSingleClickListener {
            showAgree()
        }
    }

    private fun refreshUserInfo() {
        loadAvatar(userService.getAvatarImgUrl(), mine_edit_et_avatar)
        viewModel.getUserData()
        /*
        * 如果返回的数据为空格，则表示数据为空，昵称除外
        * */
        mine_et_name.setText(userService.getNickname())
        mine_et_introduce.setText(
            if (userService.getIntroduction().isBlank()) "" else userService.getIntroduction()
        )
        mine_et_gender.text = userService.getGender()
        mine_et_college.text = userService.getBirth()
    }

    private fun initObserver() {
        viewModel.personData.observe(this) {
            mine_et_name.text = it.data.username
            mine_et_introduce.text = it.data.stunum
            mine_et_gender.text = it.data.gender
            mine_et_college.text = it.data.college
        }
        viewModel.upLoadImageEvent.observe {
            if (it) {
                loadAvatar(
                    IAccountService::class.impl.getUserService()
                        .getAvatarImgUrl(), mine_edit_et_avatar
                )
                //更新显示的头像
                IAccountService::class.impl.getUserService().refreshInfo()
                mine_edit_et_avatar.postDelayed(500) {
                    refreshUserInfo()
                }
                toast("修改头像成功")
            } else {
                toast("修改头像失败")
            }
        }
    }

    /*下面是上传头像部分的代码*/

    private fun changeAvatar() {
        //选择
        MaterialDialog(this@EditInfoActivity).show {
            listItems(items = listOf("拍照", "从相册中选择")) { dialog, index, text ->
                if (index == 0) {
                    getImageFromCamera()
                } else {
                    getImageFromAlbum()
                }
            }
            cornerRadius(res = com.mredrock.cyxbs.common.R.dimen.common_corner_radius)

        }
    }

    private val cameraImageFile by lazy { File(getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + System.currentTimeMillis() + ".png") }
    private val destinationFile by lazy { File(getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + userService.getStuNum() + ".png") }

    /**
     * 用于跳转至拍照界面获取头像
     */
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            //若获取成功则进入裁剪界面
            if (result) {
                startCropActivity(cameraImageFile.uri)
            }
        }


    /**
     * 申请权限后跳转至拍照界面获取图片
     */
    private fun getImageFromCamera() {
        doPermissionAction(Manifest.permission.CAMERA) {
            reason = "拍照需要访问你的相机哦~"
            doAfterGranted {
                takePictureLauncher.launch(cameraImageFile.uri)
            }
        }
    }

    /**
     * 跳转至图片选择界面，选择图片用于裁剪后上传头像
     */
    private val selectPictureFromAlbumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            if (uri != null) {
                startCropActivity(uri)
            } else {
                toast("无法识别该图像")
            }
        }

    //文件权限在点击头像框时已经获取到了，这里不需要再获取
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        selectPictureFromAlbumLauncher.launch(intent)
    }

    private fun startCropActivity(uri: Uri) {
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
        options.setToolbarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.config.R.color.colorPrimaryDark)
        )
        options.setStatusBarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.config.R.color.colorPrimaryDark)
        )
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
            val fileBody = MultipartBody.Part.createFormData(
                "fold",
                destinationFile.name,
                destinationFile.getRequestBody()
            )

            val numBody =
                userService.getStuNum().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            viewModel.uploadAvatar(numBody, fileBody)
        } catch (e: IOException) {
            e.printStackTrace()
            toast("图片加载失败")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (resultCode == UCrop.RESULT_ERROR && data != null) handleCropError(data)

        /**
         * UCrop内调用的是startActivityForResult，因此这一条留了下来，用于上传裁剪后的图片
         * 通过相册选取和拍照修改头像已改为 Activity Result API
         * 从相册选取改为 [selectPictureFromAlbumLauncher]
         * 拍照获取改为 [takePictureLauncher]
         */
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                uploadImage(data)
            } else {
                toast("未知错误，请重试")
            }
        }
    }


    private fun showAgree() {
        val materialDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.mine_layout_dialog_recyclerview_dynamic,
            materialDialog.window?.decorView as ViewGroup,
            false
        )
        val rvContent: RecyclerView = view.findViewById(R.id.rv_content)
        val loader: ProgressBar = view.findViewById(R.id.loader)
        materialDialog.setContentView(view)
        materialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val featureIntroAdapter = DynamicRVAdapter(viewModel.portraitAgreementList)
        rvContent.adapter = featureIntroAdapter
        rvContent.layoutManager = LinearLayoutManager(this@EditInfoActivity)
        if (viewModel.portraitAgreementList.isNotEmpty()) loader.visibility = View.GONE
        materialDialog.show()
        viewModel.getPortraitAgreement {
            featureIntroAdapter.notifyDataSetChanged()
            loader.visibility = View.GONE
        }
    }


}
