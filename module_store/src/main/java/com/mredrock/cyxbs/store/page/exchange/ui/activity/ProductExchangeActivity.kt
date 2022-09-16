package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.databinding.StoreActivityProductExchangeBinding
import com.mredrock.cyxbs.store.page.exchange.viewmodel.ProductExchangeViewModel
import com.mredrock.cyxbs.store.ui.activity.PhotoActivity
import com.mredrock.cyxbs.store.ui.fragment.ExchangeDialog
import com.mredrock.cyxbs.store.utils.StoreType
import com.mredrock.cyxbs.store.utils.getColor2
import com.ndhzs.slideshow.adapter.ImageViewAdapter
import com.ndhzs.slideshow.adapter.setImgAdapter
import com.ndhzs.slideshow.viewpager.transformer.AlphaPageTransformer
import com.ndhzs.slideshow.viewpager.transformer.ScaleInTransformer

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */
class ProductExchangeActivity :
  BaseVmBindActivity<ProductExchangeViewModel, StoreActivityProductExchangeBinding>() {
  
  private lateinit var mData: ProductDetail // 记录该页面的商品数据, 用于之后的判断
  private var mShopId = "" //商品ID
  private var mStampCount = 0 //我的余额
  private var mIsPurchased = false // 是否已经购买过, 只有邮货才有购买限制
  
  companion object {
    
    private const val INTENT_PRODUCT_ID = "id" // 商品 id
    private const val INTENT_STAMP_COUNT = "stampCount" // 邮票数量
    private const val INTENT_HAS_BOUGHT = "isPurchased" // 是否已经购买过了, 只有邮货才有购买限制
    
    fun activityStart(context: Context, id: Int, stampCount: Int, isPurchased: Boolean) {
      val intent = Intent(context, ProductExchangeActivity::class.java)
      intent.putExtra(INTENT_PRODUCT_ID, id)
      intent.putExtra(INTENT_STAMP_COUNT, stampCount)
      intent.putExtra(INTENT_HAS_BOUGHT, isPurchased)
      context.startActivity(intent)
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    window.setBackgroundDrawableResource(android.R.color.transparent)
    super.onCreate(savedInstanceState)
    // 降低因使用共享动画进入 activity 后的白闪情况
    initData()
    initJump()
    initObserve()
  }
  
  private fun initData() {
    mShopId = intent.getIntExtra(INTENT_PRODUCT_ID, 0).toString()
    mStampCount = intent.getIntExtra(INTENT_STAMP_COUNT, 0)
    mIsPurchased = intent.getBooleanExtra(INTENT_HAS_BOUGHT, false)
    if (mIsPurchased) { // 如果已经购买过
      binding.storeBtnExchange.setBackgroundColor(getColor2(R.color.store_btn_ban_product_exchange))
    }
    
    binding.storeTvUserStampCount.text = mStampCount.toString()
    
    viewModel.getProductDetail(mShopId) // 请求商品详细页数据
  }
  
  private fun initJump() {
    //设置左上角返回点击事件
    val button: ImageButton = findViewById(R.id.store_iv_toolbar_arrow_left)
    button.setOnSingleClickListener { finishAfterTransition() }
    
    binding.storeBtnExchange.setOnSingleClickListener {
      if (mIsPurchased) { // 如果已经购买过就禁止显示 diolog
        toast("每种商品只限领一次哦")
        return@setOnSingleClickListener
      }
      it.isClickable = false // 在继续显示 dialog 期间禁止用户点击
      ExchangeDialog.show(
        supportFragmentManager,
        null,
        type = ExchangeDialog.DialogType.TWO_BUTTON,
        exchangeTips = "确认要用${binding.storeTvExchangeDetailPrice.text}" +
          "邮票兑换${binding.storeTvProductName.text}吗？",
        onPositiveClick = {
          viewModel.getExchangeResult(mShopId) // 请求用户是否能购买
          dismiss()
        },
        onNegativeClick = {
          dismiss()
          it.isClickable = true
        },
        cancelCallback = { it.isClickable = true }
      )
    }
  }
  
  @SuppressLint("SetTextI18n")
  private fun initObserve() {
    viewModel.productDetail.observe {
      binding.data = it
      // 处理权益说明以及标题
      when (it.type) {
        StoreType.Product.DRESS -> {
          binding.storeTvProductDetailTitle.text =
            getString(R.string.store_attire_product_detail)
          binding.storeTvEquityDescription.text =
            "1、虚拟商品版权归红岩网校工作站所有。\n" +
              "2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
        }
        StoreType.Product.GOODS -> {
          binding.storeTvProductDetailTitle.text =
            getString(R.string.store_entity_product_detail)
          binding.storeTvEquityDescription.text =
            "1、每个实物商品每人限兑换一次，已经兑换的商品不能退货换货也不予折现。\n" +
              "2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
        }
      }
      //初始化轮播图
      initSlideShow(it.urls)
      //保存
      mData = it
    }
    
    // 请求购买成功的观察
    viewModel.exchangeResult.observe {
      // 根据不同商品类型弹出不同dialog
      when (mData.type) {
        StoreType.Product.DRESS -> {
          //刷新兑换后的余额与库存 下同
          mStampCount -= mData.price
          mData.amount = it.amount //由兑换成功时获取到的最新amount来更新mData 下同
          binding.data = mData //重新绑定是实现 购买后库存为0时 兑换按钮置灰(是否置灰的逻辑绑定在xml里) 下同
          binding.storeTvUserStampCount.text =
            mStampCount.toString()
          ExchangeDialog.show(
            supportFragmentManager,
            null,
            type = ExchangeDialog.DialogType.TWO_BUTTON,
            exchangeTips = "兑换成功！现在就换掉原来的名片吧！",
            positiveString = "好的",
            negativeString = "再想想",
            onNegativeClick = { dismiss() },
            onPositiveClick = {
              toast("个人界面即将上线")
              /*
              * 这里按下"好的", 应该还要写一个跳转
              *
              * 应该是跳转到个人界面
              * */
              dismiss()
            },
            dismissCallback = { binding.storeBtnExchange.isClickable = true }
          )
        }
        StoreType.Product.GOODS -> {
          mStampCount -= mData.price
          mData.amount = it.amount
          binding.data = mData
          binding.storeTvUserStampCount.text =
            mStampCount.toString()
          ExchangeDialog.show(
            supportFragmentManager,
            null,
            type = ExchangeDialog.DialogType.ONR_BUTTON,
            exchangeTips = "兑换成功！请在30天内到红岩网校领取哦",
            onPositiveClick = { dismiss() },
            dismissCallback = { binding.storeBtnExchange.isClickable = true }
          )
        }
      }
    }
    
    // 请求失败的观察
    viewModel.exchangeError.observe {
      when (it) {
        StoreType.ExchangeError.OUT_OF_STOCK -> {
          ExchangeDialog.show(
            supportFragmentManager,
            null,
            type = ExchangeDialog.DialogType.ONR_BUTTON,
            exchangeTips = "啊欧，手慢了！下次再来吧=.=",
            onPositiveClick = { dismiss() },
            dismissCallback = { binding.storeBtnExchange.isClickable = true }
          )
        }
        StoreType.ExchangeError.NOT_ENOUGH_MONEY -> {
          ExchangeDialog.show(
            supportFragmentManager,
            null,
            type = ExchangeDialog.DialogType.ONR_BUTTON,
            exchangeTips = "诶......邮票不够啊......穷日子真不好过呀QAQ",
            onPositiveClick = { dismiss() },
            dismissCallback = { binding.storeBtnExchange.isClickable = true }
          )
        }
        StoreType.ExchangeError.IS_PURCHASED -> {
          /*
          * 这里是不会触发的, 因为在之前就判断过是否已经购买而取消了 dialog
          * */
          toast("每种商品只限领一次哦")
          binding.storeBtnExchange.isClickable = true
        }
        else -> {
          toast("兑换请求异常")
          binding.storeBtnExchange.isClickable = true
        }
      }
    }
  }
  
  private var mPositionResult: PhotoActivity.Companion.Position? = null
  
  private fun initSlideShow(imgUrls: List<String>) {
    binding.storeSsExchangeProductImage
      .addTransformer(ScaleInTransformer())
      .addTransformer(AlphaPageTransformer())
      .setIsCyclical(true)
      .setImgAdapter(
        ImageViewAdapter.Builder(imgUrls)
          .onCreate {
            view.setOnSingleClickListener {
              // 装扮详情点击图片的元素共享动画
              val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@ProductExchangeActivity, Pair(
                  binding.storeSsExchangeProductImage,
                  "productImage"
                )
              )
  
              mPositionResult = PhotoActivity.activityStart(
                this@ProductExchangeActivity, ArrayList(imgUrls),
                // 因为开启了循环滑动, 所以必须使用 realPosition 得到你所看到的位置
                realPosition, options.toBundle()
              )
            }
          }.onBind {
            view.setImageFromUrl(data)
          }
      )
  }
  
  override fun onRestart() {
    super.onRestart()
    val position = mPositionResult
    if (position != null) {
      mPositionResult = null
      // 从 PhotoActivity 返回时就使轮播图跳转到对应位置
      binding.storeSsExchangeProductImage
        .setCurrentItem(position.value, false)
    }
  }
}