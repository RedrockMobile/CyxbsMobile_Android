package com.mredrock.cyxbs.mine.page.mine.ui.activity


import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.page.mine.viewmodel.MineViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.config.route.DEFAULT_PAGE
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.network.model.UserInfo
import com.mredrock.cyxbs.mine.page.edit.EditInfoActivity
import com.mredrock.cyxbs.mine.page.mine.widget.BlurBitmap
import com.mredrock.cyxbs.mine.util.transformer.ScaleInTransformer
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import kotlin.math.abs

@Route(path = MINE_PERSON_PAGE)
class HomepageActivity : BaseViewModelActivity<MineViewModel>() {

    /**
     * 跳转到对应页面的标志 粉丝和关注
     */
    private val TO_FANS = 0
    private val TO_ATTENTION = 1
    private lateinit var dataBinding: MineActivityHomepageBinding

    /**
     * 个人信息view的初始透明度
     */
    private var alphaMineView = 0f

    /**
     * 是否是本人访问个人中心
     */
    private var isSelf = true

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

    private var redid: String? = null

    /**
     * 身份的Fragemnt
     */
    private val identityFragment by lazy {
        IdentityFragment()
    }

    /**
     * 是否需要刷新
     */
    private var isNeedRefresh = true

    /**
     * 是否是女生
     *
     */
    var isGirl: String = "男"




    var tabNames = listOf<String>("我的动态", "我的身份")
    val imageViewList by lazyUnlock {
        mutableListOf<ImageView>(
            dataBinding.clPersonalInformation.findViewById(R.id.iv_nameplate1),
            dataBinding.clPersonalInformation.findViewById(R.id.iv_nameplate2),
            dataBinding.clPersonalInformation.findViewById(R.id.iv_nameplate3)
        )
    }
    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }

    private val cameraImageFile by lazy { File(getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + System.currentTimeMillis() + ".png") }
    private val destinationFile by lazy { File(getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + userService.getStuNum() + ".png") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityHomepageBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initView()
        initData()
        initListener()
    }


    fun initData() {
        val layout  = dataBinding.clPersonalInformation
        val tv_id_number: TextView = layout.findViewById(R.id.tv_id_number)
        val tv_grade: TextView = layout.findViewById(R.id.tv_grade)
        val mine_tv_constellation: TextView = layout.findViewById(R.id.mine_tv_constellation)
        val tv_sex: TextView = layout.findViewById(R.id.tv_sex)
        val tv_name: TextView = layout.findViewById(R.id.tv_name)
        val tv_signature: TextView = layout.findViewById(R.id.tv_signature)
        val civ_head: CircleImageView = layout.findViewById(R.id.civ_head)
        alphaMineView = dataBinding.clPersonalInformation.alpha
        viewModel._userInfo.observeForever {
            initSatu(it)
            changeAttention(it)
            tv_id_number.text = it.data.uid.toString()
            tv_grade.text = it.data.grade + "级"
            if (it.data.constellation.equals("")) {
                mine_tv_constellation.text = "十三星座"
            } else {
                mine_tv_constellation.text = it.data.constellation
            }
            tv_sex.text = it.data.gender
            tv_name.text = it.data.nickname
            tv_signature.text = it.data.introduction
            nickname = it.data.nickname
            isNeedRefresh = false
            isSelf = it.data.is_self
            isGirl = it.data.gender
            it.data.redid.let {
                identityFragment.onSuccess(it, isSelf)
                dataBinding.vp2Mine.offscreenPageLimit = 2
                redid = it
                viewModel.getPersonalCount(redid)
            }
            loadBitmap(it.data.backgroundUrl) {
                initBlurBitmap(it)
            }
            dataBinding.srlRefresh.isRefreshing = false
            loadAvatar(it.data.photoSrc, civ_head)
            initTab()
        }

        viewModel._redRockApiChangeUsercount.observeForever {
            viewModel.getPersonalCount(redid)
            getUserInfo(intent)
        }
        getUserInfo(intent)

    }

    fun initSatu(user: UserInfo) {

        user.data.identityies?.forEachIndexed { index, s ->
            loadRedrockImage(s, imageViewList[index])
            imageViewList[index].visibility = View.VISIBLE
        }
    }

    fun initTab() {
        val iv_edit: ImageView = findViewById(R.id.iv_edit)
        val tv_edit: TextView = findViewById(R.id.tv_edit)
        if (isSelf) {
            tabNames = listOf<String>("我的动态", "我的身份")
        } else {
            if (isGirl == "男") {
                tabNames = listOf<String>("他的动态", "他的身份")
            } else {
                tabNames = listOf<String>("她的动态", "她的身份")
            }
            iv_edit.alpha = 0f
            tv_edit.alpha = 0f
        }
        TabLayoutMediator(dataBinding.mineTablayout, dataBinding.vp2Mine, true) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    val bg4 by lazy {
        dataBinding.clPersonalInformation.findViewById<TextView>(R.id.mine_tv_concern).background as GradientDrawable
    }


    @SuppressLint("ResourceAsColor")
    fun changeAttention(data: UserInfo) {
        val mine_tv_concern: TextView = findViewById(R.id.mine_tv_concern)
        if (data.data.is_self) {
            mine_tv_concern.visibility = View.INVISIBLE
        } else {
            mine_tv_concern.visibility = View.VISIBLE
            if (data.data.isFocus) {

                if (this.applicationContext.resources.configuration.uiMode == 0x21) {
                    bg4.setColor(Color.parseColor("#CC5A5A5A"))
                } else {
                    bg4.setColor(Color.parseColor("#E8F0FC"))

                }
                if (data.data.isBefocused) {
                    mine_tv_concern.text = "互相关注"
                } else {
                    mine_tv_concern.text = "已关注"
                }
            } else {
                mine_tv_concern.text = "关注"

                bg4.setColor(Color.parseColor("#4841E2"))
            }
        }

    }


    fun getUserInfo(data: Intent?) {
        if (redid == null) {
            redid = data?.getStringExtra("redid")

        }
        if (redid != null) {   //他人访问的情况

            viewModel.getUserInfo(redid)
            MineAndQa.refreshListener?.onRefresh(redid)
        } else {//自己访问的情况

            viewModel.getUserInfo(null)
            MineAndQa.refreshListener?.onRefresh(null)

        }
    }

    fun initView() {
        val dynamicFragment =
            ARouter.getInstance().build(QA_DYNAMIC_MINE_FRAGMENT).navigation() as Fragment
        val list = arrayListOf<Fragment>(dynamicFragment, identityFragment)
        if (dynamicFragment is MineAndQa.RefreshListener) {
            MineAndQa.refreshListener = dynamicFragment
        }
        dataBinding.vp2Mine.adapter = MineAdapter(this, list)
        dataBinding.vp2Mine.offscreenPageLimit = 2
        dataBinding.vp2Mine.setPageTransformer(ScaleInTransformer())
    }

    override fun onDestroy() {
        super.onDestroy()
        MineAndQa.refreshListener = null
    }

    @SuppressLint("ResourceAsColor")
    fun initListener() {
        val layout = dataBinding.clPersonalInformation
        val tv_fans_number: TextView = layout.findViewById(R.id.tv_fans_number)
        val tv_attention_number: TextView = layout.findViewById(R.id.tv_attention_number)
        val tv_praise_number: TextView = layout.findViewById(R.id.tv_praise_number)
        val mine_tv_concern: TextView = layout.findViewById(R.id.mine_tv_concern)
        /**
         * 嵌套滑动由滑动距离传过来的滑动百分比的接口
         */
        dataBinding.svgMine.setScrollChangeListener {
            dataBinding.clPersonalInformation.alpha = (1 - it) * alphaMineView
            dataBinding.clPersonalInformation.scaleX = (1 - it)
            dataBinding.clPersonalInformation.scaleY = (1 - it)
            val alpha = (1f - it * 2)   //因为滑动过程中涉及到两种动画效果的变化  所以我就产生一个-1和+1 从+1到—1
            isNeedRefresh = it == 0f
            tabChange(alpha)
            if (alpha < 0) {
                if (dataBinding.tvMine.text == "个人主页") {
                    dataBinding.ivMineBackgroundNormal.alpha = 0f
                    dataBinding.btMineBack.setImageResource(R.drawable.mine_ic_iv_back_black_arrow)
                    dataBinding.tvMine.text = nickname
                    dataBinding.tvMine.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.mine_black
                        )
                    )
                    dataBinding.flTabLine.gone()
                }
                dataBinding.tvMine.alpha = -alpha
                dataBinding.flBackground.alpha = -alpha
                dataBinding.btMineBack.alpha = -alpha
                dataBinding.ivMineBackgroundBlur.alpha = 1f + alpha
            } else {
                if (dataBinding.tvMine.text == nickname) {
                    dataBinding.tvMine.text = "个人主页"
                    dataBinding.btMineBack.setImageResource(R.drawable.mine_ic_bt_back_arrow)
                    dataBinding.tvMine.setTextColor(Color.WHITE)
                    dataBinding.flTabLine.visible()
                }
                dataBinding.flBackground.alpha = 0f
                dataBinding.ivMineBackgroundNormal.alpha = alpha

                dataBinding.btMineBack.alpha = alpha
                dataBinding.tvMine.alpha = alpha
                dataBinding.flLine.alpha = alpha

            }
        }
        viewModel._PersonalCont.observeForever {
            tv_fans_number.text = it.data.fans.toString()
            tv_attention_number.text = it.data.follows.toString()
            tv_praise_number.text = it.data.praise.toString()

        }
        mine_tv_concern.setOnClickListener {
            viewModel.changeFocusStatus(redid)
        }
        dataBinding.ivMineBackgroundNormal.setOnClickListener {
            if (isSelf) {
                changeBackground()
            } else {
                toast("不可以对别人的背景图片动手动脚哦!")
            }
        }
        dataBinding.btMineBack.setOnClickListener {
            onBackPressed()
        }
        viewModel._isChangeSuccess.observeForever {
            if (it == true) {
                toast("恭喜切换背景图片成功!")
            } else {
                toast("抱歉 似乎出了一点小问题!")
            }
        }
        dataBinding.srlRefresh.setOnRefreshListener {
            getUserInfo(intent)
            identityFragment.refresh()
            MineAndQa.refreshListener?.onRefresh(redid)
        }
        viewModel._isUserInfoFail.observeForever {
            if (it == true) {
                dataBinding.srlRefresh.isRefreshing = false
                toast("服务器似乎出了一点小问题")
                initBlurBitmap(null)
            }
            initTab()
        }
        //此处点击事件监听不可删除  为了防止发生穿透点击
        dataBinding.clPersonalInformation.setOnClickListener {

        }
        val tv_fans: TextView = layout.findViewById(R.id.tv_fans)
        tv_fans.setOnClickListener {
            redid?.let { it1 -> FanActivity.activityStart(this, it1, TO_FANS) }
        }
        val tv_attention: TextView = layout.findViewById(R.id.tv_attention)
        tv_attention.setOnClickListener {
            redid?.let { it1 -> FanActivity.activityStart(this, it1, TO_ATTENTION) }
        }
        val tv_edit:TextView = layout.findViewById(R.id.tv_edit)
        tv_edit.setOnClickListener {
            if (isSelf) {

                val intent = Intent(this, EditInfoActivity::class.java)
                startActivity(intent)
            } else {
                toast("不可以对别人资料动手动脚哦!")
            }
        }
        tv_fans_number.setOnClickListener {
            redid?.let { it1 -> FanActivity.activityStart(this, it1, TO_FANS) }
        }
        tv_attention_number.setOnClickListener {
            redid?.let { it1 -> FanActivity.activityStart(this, it1, TO_ATTENTION) }
        }
        val iv_edit:ImageView = findViewById(R.id.iv_edit)
        iv_edit.setOnClickListener {
            if (isSelf) {
                val intent = Intent(this, EditInfoActivity::class.java)
                startActivity(intent)
            } else {
                toast("不可以对别人的资料动手动脚哦!")
            }
        }
        val tv_praise:TextView = findViewById(R.id.tv_praise)
        tv_praise.setOnClickListener {
            if (isSelf) {
                ARouter.getInstance().build(QA_MY_PRAISE).navigation()
            } else {
                toast("不可以查看别人的获赞信息哦!")
            }

        }
        tv_praise_number.setOnClickListener {
            if (isSelf) {
                ARouter.getInstance().build(QA_MY_PRAISE).navigation()
            } else {
                toast("不可以查看别人的获赞信息哦!")
            }

        }
        dataBinding.btMineBack.setOnClickListener {
            onBackPressed()
        }
        dataBinding.tvMine.setOnClickListener {
            onBackPressed()
        }
    }


    /**
     * 分别初始化模糊和正常的Bitmap图片
     */
    fun initBlurBitmap(bitmap: Bitmap?) {
        if (bitmap == null) {
            mTempBitmap =
                BitmapFactory.decodeResource(resources, R.drawable.mine_ic_iv_background)
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
    fun loadBacgroundAnmator() {
        val p = createCircularReveal(
            dataBinding.ivMineBackgroundNormal,
            0,
            0,
            0f,
            dataBinding.ivMineBackgroundNormal.height.toFloat() * 1.4f
        )
        p.duration = 900
        p.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                dataBinding.ivMineBackgroundNormal.setImageBitmap(mTempBitmap)
                dataBinding.ivMineBackgroundBlur.setImageBitmap(mFinalBitmap)
            }

            override fun onAnimationEnd(animation: Animator) {
                dataBinding.ivMineBackgroundBlur.setImageBitmap(mFinalBitmap)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

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
                //选择
                MaterialDialog(this@HomepageActivity).show {
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
        }
    }


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 200) {
            getUserInfo(data)
        }

        if (resultCode != Activity.RESULT_OK) return
        if (resultCode == UCrop.RESULT_ERROR && data != null) handleCropError(data)

        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                uploadImage(data)
            } else {
                toast("未知错误，请重试")
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
        if (resultUri == null) {
            toast("无法获得裁剪结果")
            return
        }
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(resultUri))
        initBlurBitmap(bitmap)

        try {

            val fileBody = MultipartBody.Part.createFormData(
                "pic",
                destinationFile.name,
                destinationFile.getRequestBody()
            )
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
        options.setLogoColor(
            ContextCompat.getColor(
                this,
                com.mredrock.cyxbs.common.R.color.common_level_two_font_color
            )
        )
        options.setToolbarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.common.R.color.colorPrimaryDark)
        )
        options.setStatusBarColor(
            ContextCompat.getColor(this, com.mredrock.cyxbs.common.R.color.colorPrimaryDark)
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
    fun tabChange(progress: Float) {
        if (progress == -1f && !isSetBackground) {
            isSetBackground = true
            dataBinding.mineTablayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.mine_ic_list_shadow, theme)
            dataBinding.mineTablayout.elevation = this.px2dip(300)
            dataBinding.vp2Mine.elevation = this.px2dip(0)
            setTopStatus()
        }
        if (isSetBackground && progress > -1f) {
            dataBinding.mineTablayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.mine_shape_ll_background, theme)
            isSetBackground = false
            setBottomStatus()
            dataBinding.vp2Mine.elevation = this.px2dip(300)
        }
    }

    private fun setTopStatus() {
        dataBinding.mineTablayout.setPadding(0, 0, 0, this.dip(40))
        val tabLp = dataBinding.mineTablayout.layoutParams
        tabLp.height = this.dip(104)
        dataBinding.mineTablayout.layoutParams = tabLp
        val vpLp = dataBinding.vp2Mine.layoutParams as LinearLayout.LayoutParams
        vpLp.topMargin = this.dip(-40)
        dataBinding.vp2Mine.layoutParams = vpLp
        dataBinding.svgMine.offset = this.dip(258)
        dataBinding.vp2Mine.requestLayout()
    }

    private fun setBottomStatus() {
        dataBinding.mineTablayout.setPadding(0, 0, 0, 0)
        val tabLp = dataBinding.mineTablayout.layoutParams
        tabLp.height = this.dip(64)
        dataBinding.mineTablayout.layoutParams = tabLp
        val vpLp = dataBinding.vp2Mine.layoutParams as LinearLayout.LayoutParams
        vpLp.topMargin = this.dip(0)
        dataBinding.svgMine.offset = 0
        dataBinding.vp2Mine.layoutParams = vpLp
        dataBinding.vp2Mine.requestLayout()
    }

    /**
     * 加载网络请求的Bitmap图片出来
     */
    fun loadBitmap(url: String, success: (Bitmap) -> Unit) {
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


    companion object {
        fun startHomePageActivity(redid: String?, activity: Activity) {
            /**
             * TODO 关闭服务 个人主页
             */
            ARouter.getInstance().build(DEFAULT_PAGE).navigation()
//            val intent = Intent(activity, HomepageActivity::class.java)
//            intent.putExtra("redid", redid)
//            activity.startActivity(intent)
        }
    }

    /**
     * 获取redid的接口
     */
    interface OnGetRedid {
        fun onSuccess(redid: String, isSelf: Boolean)
    }

    var downY = 0f
    var distance = 0f

    /**
     * 刷新滑动与左右滑动事件的处理
     * todo 这是为了解决刷新控件的滑动冲突，但耦合性太高了，代码也比较 (，
     *  我实在是改不了，因为界面过于耦合，建议你们重构，使用 store 模块里的 SlideUpLayout 或者 协调者布局（协调者布局也比较耦合）
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
            }
        }
        distance = ev.y - downY

        if (isNeedRefresh && distance > 100f) {
            distance = 0f
            return dataBinding.srlRefresh.dispatchTouchEvent(ev)
        } else if (abs(distance) < 8f) {
            distance = 0f
            return super.dispatchTouchEvent(ev)
        }
        return dataBinding.svgMine.dispatchTouchEvent(ev)
    }

    /**
     * 修复在@HomePageActivity 界面切换深色模式 重新启动Activity资源不完全的bug
     * 导致部分资源获取不到
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        finish()
    }

    override fun onResume() {
        super.onResume()
        //在编辑界面更换头像后返回该页面时，更新一下显示的头像
        loadAvatar(IAccountService::class.impl.getUserService().getAvatarImgUrl(), findViewById(R.id.civ_head))
        MineAndQa.refreshListener?.onRefresh(redid)
    }
}