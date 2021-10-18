package com.mredrock.cyxbs.mine.page.mine.ui.activity


import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.page.mine.viewmodel.MineViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.config.QA_DYNAMIC_MINE_FRAGMENT
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.page.mine.widget.BlurBitmap
import com.mredrock.cyxbs.store.utils.transformer.ScaleInTransformer
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.mine_activity_homepage_head.view.*
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import kotlin.math.abs


class HomepageActivity : BaseViewModelActivity<MineViewModel>() {


    private lateinit var dataBinding: MineActivityHomepageBinding

    /**
     * 个人信息view的初始透明度
     */
    private var alphaMineView = 0f


    /**
     * 原始图片   关于个人主页背景图片的高斯模糊实现  我是采用的两种图片重合的形式
     * 后面一张全部模糊 只是改变前面一张的透明度 就可以达到动态的模糊效果
     * （并没有采取即时渲染模糊的图片 因为因为性能的原因可能会卡）
     */
    private var mTempBitmap: Bitmap? = null

    /**
     * 模糊后的图片
     */
    private var mFinalBitmap: Bitmap? = null

    /**
     * 用户的昵称
     */
    private var nickname = "鱼鱼鱼鱼鱼鱼鱼鱼鱼"
    /**
     * redid
     */
    private var redid:String? = null

    /**
     * 身份的Fragemnt
     */
    private val identityFragment by lazy {
      IdentityFragment()
    }
    /**
     * 相册图片的地址
     */
    private val fileDir by lazy {
        StringBuilder(Environment.getExternalStorageDirectory().path)
            .append(DIR_PHOTO)
            .toString()
    }

    /**
     * 是否需要刷新
     */
    private var isNeedRefresh=true
    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }
    private val SELECT_PICTURE = 1

    private val SELECT_CAMERA = 2

    private val cameraImageFile by lazy { File(fileDir + File.separator + System.currentTimeMillis() + ".png") }
    private val destinationFile by lazy { File(fileDir + File.separator + userService.getStuNum() + ".png") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityHomepageBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initData()
        initView()
        initListener()
            dataBinding.srlRefresh.isMotionEventSplittingEnabled=false
    }


    fun initData() {

        alphaMineView = dataBinding.clPersonalInformation.alpha
        viewModel._userInfo.observeForever {
            dataBinding.clPersonalInformation.tv_id_number.text = it.data.uid.toString()
            dataBinding.clPersonalInformation.tv_grade.text = it.data.grade
            if (it.data.constellation.equals("")){
                dataBinding.clPersonalInformation.mine_tv_constellation.text="十三星座"
            }else{
                dataBinding.clPersonalInformation.mine_tv_constellation.text = it.data.constellation
            }
            dataBinding.clPersonalInformation.tv_sex.text = it.data.gender
            dataBinding.clPersonalInformation.tv_name.text = it.data.nickname
            dataBinding.clPersonalInformation.tv_signature.text = it.data.introduction
            nickname =  it.data.nickname
          it.data.redid.let {
              identityFragment.onSuccesss(it)
          }
        loadBitmap(it.data.backgroundUrl){
            initBlurBitmap(it)
        }
            dataBinding.srlRefresh.isRefreshing = false

            Log.e("wxtadasg","(HomepageActivity.kt:144)->>${it.data.identityies} ")
            loadAvatar(it.data.photoSrc,dataBinding.clPersonalInformation.civ_head)

        }
       getUserInfo(intent)

    }





    fun getUserInfo(data: Intent?){
        redid = data?.getStringExtra("redid")
        if (redid!=null){
            viewModel.getUserInfo(redid)
            dataBinding.clPersonalInformation.mine_tv_concern.visible()
        }else{
            dataBinding.clPersonalInformation.mine_tv_concern.visibility = View.INVISIBLE
            viewModel.getUserInfo(null)
        }

    }

    fun initView() {
        val tabNames = listOf<String>("我的动态", "我的身份")
        val dynamicFragment =
            ARouter.getInstance().build(QA_DYNAMIC_MINE_FRAGMENT).navigation() as Fragment
        val list = arrayListOf<Fragment>(dynamicFragment, identityFragment)
        dataBinding.vp2Mine.adapter = MineAdapter(this, list)
        dataBinding.vp2Mine.
            setPageTransformer(ScaleInTransformer())
        TabLayoutMediator(dataBinding.mineTablayout, dataBinding.vp2Mine, true) { tab, position ->
            tab.text = tabNames[position]
        }.attach()


    }


    @SuppressLint("ResourceAsColor")
    fun initListener() {
        /**
         * 嵌套滑动由滑动距离传过来的滑动百分比的接口
         */
        dataBinding.svgMine.setScrollChangeListener {
            dataBinding.clPersonalInformation.alpha = (1 - it) * alphaMineView
            dataBinding.clPersonalInformation.scaleX = (1 - it)
            dataBinding.clPersonalInformation.scaleY = (1 - it)
            var alpha = (1f - it * 2)   //因为滑动过程中涉及到两种动画效果的变化  所以我就产生一个-1和+1 从+1到—1
            isNeedRefresh = it==0f
            tabChange(alpha)
            if (alpha < 0) {
                if (dataBinding.tvMine.text == "个人主页") {
                    dataBinding.ivMineBackgroundNormal.alpha =0f
                    dataBinding.btMineBack.setImageResource(R.drawable.mine_ic_iv_back_black_arrow)
                    dataBinding.tvMine.text = nickname
                    dataBinding.tvMine.setTextColor(getResources().getColor(R.color.mine_black))
                }
                dataBinding.tvMine.alpha = -alpha
                dataBinding.flBackground.alpha = -alpha
                dataBinding.btMineBack.alpha = -alpha
                dataBinding.ivMineBackgroundBlur.alpha = 1f + alpha
            } else {
                if (dataBinding.tvMine.text == nickname) {
                    dataBinding.tvMine.text = "个人主页"
                    dataBinding.btMineBack.setImageResource(R.drawable.mine_ic_bt_back_arrow)
                    dataBinding.tvMine.setTextColor(getResources().getColor(R.color.mine_white))
                }
                dataBinding.flBackground.alpha = 0f
                dataBinding.ivMineBackgroundNormal.alpha = alpha

                dataBinding.btMineBack.alpha = alpha
                dataBinding.tvMine.alpha = alpha
                dataBinding.flLine.alpha = alpha

            }
        }
        dataBinding.ivMineBackgroundNormal.setOnClickListener {
            changeBackground()
        }

        viewModel._isChangeSuccess.observeForever {
            if (it==true){
                toast("恭喜切换背景图片成功!")
            }else{
                toast("抱歉 似乎出了一点小问题!")
            }
        }
            dataBinding.srlRefresh.setOnRefreshListener {
                getUserInfo(intent)
            }
        viewModel._isUserInfoFail.observeForever {
            if (it==true){
                dataBinding.srlRefresh.isRefreshing=false
                toast("服务器似乎出了一点小问题")
                initBlurBitmap(null)
            }
        }

    }


    /**
     * 分别初始化模糊和正常的Bitmap图片
     */
    fun initBlurBitmap(bitmap: Bitmap?) {
        if (bitmap == null) {

            mTempBitmap =
                BitmapFactory.decodeResource(getResources(), R.drawable.mine_ic_iv_background_test)
            mFinalBitmap = BlurBitmap.blur(this, mTempBitmap!!)
        } else {
            mTempBitmap = bitmap
            mFinalBitmap = BlurBitmap.blur(this, mTempBitmap!!)
        }

      loadBacgroundAnmator()


    }

    /**
     * 背景图片的加载动画
     */
    fun loadBacgroundAnmator(){
        val p= createCircularReveal(
            dataBinding.ivMineBackgroundNormal, 0, 0, 0f, dataBinding.ivMineBackgroundNormal.height.toFloat()*1.4f)
        p.duration = 750
        p.addListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
               Log.e("wxtagdasdassdsad","(HomepageActivity.kt:278)->动画开始> ")
                dataBinding.ivMineBackgroundNormal.setImageBitmap(mTempBitmap)
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.e("wxtagdasdassdsad","(HomepageActivity.kt:278)->动画结束 ")
                dataBinding.ivMineBackgroundBlur?.setImageBitmap(mFinalBitmap)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })
        p.start()
    }


    @SuppressLint("CheckResult")
    fun changeBackground() {
        //获取权限
        doPermissionAction(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            reason = "读取图片需要访问您的存储空间哦~"
            doAfterGranted {
                //检查目录
                val parent = File(fileDir)
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                //选择
                MaterialDialog(this@HomepageActivity).show {
                    listItems(items = listOf("拍照", "从相册中选择")) { dialog, index, text ->
                        if (index == 0) {
                            getImageFromCamera()
                        } else {
                            getImageFromAlbum()
                        }
                    }
                    cornerRadius(res = R.dimen.common_corner_radius)

                }
            }
        }
    }


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode==200){
            getUserInfo(data)
        }

        if (resultCode != Activity.RESULT_OK) return
        if (resultCode == UCrop.RESULT_ERROR && data != null) handleCropError(data)

        when (requestCode) {
            SELECT_PICTURE -> {
                val uri = data?.data
                if (uri != null) {
                    Log.e("wxtag", "(EditInfoActivity.kt:514)->>相册逻辑 ")
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
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(resultUri))
        initBlurBitmap(bitmap)
        if (resultUri == null) {
            toast("无法获得裁剪结果")
            return
        }

        try {

            val fileBody = MultipartBody.Part.createFormData("pic", destinationFile.name, destinationFile.getRequestBody())
           viewModel.changePersonalBackground(fileBody)
        } catch (e: IOException) {
            e.printStackTrace()
            toast("图片加载失败")
        }
    }


    private fun startCropActivity(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(destinationFile))
        val options = UCrop.Options()
        options.setCropGridStrokeWidth(5)
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        options.setCompressionQuality(100)
        options.setLogoColor(ContextCompat.getColor(this, R.color.common_level_two_font_color))
        options.setToolbarColor(
            ContextCompat.getColor(this, R.color.colorPrimaryDark)
        )
        options.setStatusBarColor(
            ContextCompat.getColor(this, R.color.colorPrimaryDark)
        )
        uCrop.withOptions(options)
            .withAspectRatio(
                dataBinding.ivMineBackgroundNormal.width.toFloat(),
                dataBinding.ivMineBackgroundNormal.height.toFloat()
            )
            .start(this)
    }



var isSetBackground = false
    /**
     * 这一个方法用于改变在滑动的过程中 tab的变化过程
     */
    fun tabChange(pregress: Float) {

        if (pregress == -1f&&!isSetBackground) {

            isSetBackground = true
            dataBinding.mineTablayout.background =
                resources.getDrawable(R.drawable.mine_layer_list_shape_shadow)
        }
        if (isSetBackground&&pregress>-1f){

            dataBinding.mineTablayout.background =
                resources.getDrawable(R.drawable.mine_shape_ll_background)
            isSetBackground=false
        }

    }

    /**
     * 加载网络请求的Bitmap图片出来
     */
    fun loadBitmap(url: String, success: (Bitmap) -> Unit){
        Glide.with(this) // context，可添加到参数中
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // 成功返回 Bitmap
                    success.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    initBlurBitmap(null)
                }
            })
    }


    companion object{
        fun startHomePageActivity(redid:String?,activity: Activity){
            val intent = Intent(activity,HomepageActivity::class.java)
            intent.putExtra("redid",redid)
            activity.startActivity(intent)
        }

    }

    /**
     * 获取redid的接口
     */
    interface onGetRedid {
        fun onSuccesss(redid: String)
    }
    var downY=0f
    var distance=0f
    /**
     * 刷新滑动与左右滑动事件的处理
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when(ev.action){
            MotionEvent.ACTION_DOWN->{
               downY=ev.y
            }
        }
        distance = ev.y-downY
        Log.e("wxtagdasdas","(HomepageActivity.kt:507)->>downY=${downY},ev.y=${ev.y},distance =$distance")
            if (isNeedRefresh&&distance>80f){
                Log.e("wxtagdasdas","srlRefresh分发")
                distance=0f
                return dataBinding.srlRefresh.dispatchTouchEvent(ev)
            }else if (abs(distance) <8f){
                distance=0f
                return super.dispatchTouchEvent(ev)
            }
        Log.e("wxtagdasdas","svgMine分发")
        return dataBinding.svgMine.dispatchTouchEvent(ev)
    }

}