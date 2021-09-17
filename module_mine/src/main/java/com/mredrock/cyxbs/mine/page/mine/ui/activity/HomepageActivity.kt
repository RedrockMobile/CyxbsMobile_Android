package com.mredrock.cyxbs.mine.page.mine.ui.activity


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.page.mine.viewmodel.MineViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.widget.BlurBitmap


class HomepageActivity : BaseViewModelActivity<MineViewModel>() {


    private lateinit var dataBinding:MineActivityHomepageBinding

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityHomepageBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initData()
        initView()
        initListener()
        initBlurBitmap()
    }


    fun initData(){
          alphaMineView=dataBinding.clPersonalInformation.alpha
    }



    fun initView(){
        val tabNames= listOf<String>("我的动态","我的身份")
        val dynamicFragment =
            ARouter.getInstance().build("/identity/mine/entry").navigation() as Fragment
        val identityFragment = IdentityFragment()
        val list = arrayListOf<Fragment>(dynamicFragment,identityFragment)
        dataBinding.vp2Mine.adapter = MineAdapter(this,list)
        TabLayoutMediator(dataBinding.mineTablayout, dataBinding.vp2Mine,true) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }


    fun initListener(){
        /**
         * 嵌套滑动由滑动距离传过来的滑动百分比的接口
         */
        dataBinding.svgMine.setScrollChangeListener{
            dataBinding.clPersonalInformation.alpha = (1-it)*alphaMineView
            dataBinding.clPersonalInformation.scaleX = (1-it)
            dataBinding.clPersonalInformation.scaleY=(1-it)

            var alpha = (1f-it*2)   //因为滑动过程中涉及到两种动画效果的变化  所以我就产生一个-1和+1 来完成两种动画
            if (alpha<=0){
                dataBinding.flBackground.alpha = -alpha
            }else{
                dataBinding.flBackground.alpha=0f
                dataBinding.ivMineBackgroundNormal.alpha =alpha
            }


        }
    }


    /**
     * 分别初始化模糊和正常的Bitmap图片
     */
  fun  initBlurBitmap(){
      mTempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mine_ic_iv_background_test);
      mFinalBitmap = BlurBitmap.blur(this, mTempBitmap!!);

      // 填充模糊后的图像和原图
      dataBinding.ivMineBackgroundBlur?.setImageBitmap(mFinalBitmap);
      dataBinding.ivMineBackgroundNormal.setImageBitmap(mTempBitmap);
  }

}