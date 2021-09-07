package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.databinding.StoreActivityProductExchangeBinding
import com.mredrock.cyxbs.store.page.exchange.viewmodel.ProductExchangeViewModel
import com.mredrock.cyxbs.store.ui.activity.PhotoActivity
import com.mredrock.cyxbs.store.ui.fragment.ExchangeDialog
import com.mredrock.cyxbs.store.utils.StoreType
import com.mredrock.cyxbs.store.utils.transformer.AlphaTransformer
import com.mredrock.cyxbs.store.utils.transformer.ScaleInTransformer

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */
class ProductExchangeActivity : BaseViewModelActivity<ProductExchangeViewModel>() {

    private lateinit var dataBinding: StoreActivityProductExchangeBinding
    private lateinit var mData: ProductDetail // 记录该页面的商品数据, 用于之后的判断
    private var mId = "" //商品ID
    private var mStampCount = 0 //我的余额
    private var mImageList = ArrayList<String>()
    private var mIsAllowClick = true // 是否允许兑换按钮点击

    companion object {

        private const val INTENT_PRODUCT_ID = "id" // 商品 id
        private const val INTENT_STAMP_COUNT = "stampCount" // 邮票数量

        fun activityStart(context: Context, id: Int, stampCount: Int) {
            val intent = Intent(context, ProductExchangeActivity::class.java)
            intent.putExtra(INTENT_PRODUCT_ID, id)
            intent.putExtra(INTENT_STAMP_COUNT, stampCount)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 降低因使用共享动画进入 activity 后的白闪情况
        window.setBackgroundDrawableResource(android.R.color.transparent)

        dataBinding = StoreActivityProductExchangeBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)

        initData()
        initJump()
        initObserve()
    }

    private fun initData() {
        mId = intent.getIntExtra(INTENT_PRODUCT_ID, 0).toString()
        mStampCount = intent.getIntExtra(INTENT_STAMP_COUNT, 0)

        dataBinding.storeTvUserStampCount.text = mStampCount.toString()

        viewModel.getProductDetail(mId) // 请求商品详细页数据
    }

    private fun initJump() {
        //设置左上角返回点击事件
        val button: ImageButton = findViewById(R.id.store_iv_toolbar_arrow_left)
        button.setOnSingleClickListener { finishAfterTransition() }

        dataBinding.storeBtnExchange.setOnSingleClickListener {
            if (mIsAllowClick) {
                it.isClickable = false // 在继续显示 dialog 期间禁止用户点击
                ExchangeDialog.show(
                    supportFragmentManager,
                    null,
                    type = ExchangeDialog.DialogType.TWO_BUTTON,
                    exchangeTips = "确认要用${dataBinding.storeTvExchangeDetailPrice.text}" +
                            "邮票兑换${dataBinding.storeTvProductName.text}吗？",
                    onPositiveClick = {
                        viewModel.getExchangeResult(mId) // 请求用户是否能购买
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
    }

    @SuppressLint("SetTextI18n")
    private fun initObserve() {
        viewModel.productDetail.observeNotNull {
            dataBinding.data = it
            // 处理权益说明以及标题
            when (it.type) {
                StoreType.Product.DRESS -> {
                    dataBinding.storeTvProductDetailTitle.text =
                        getString(R.string.store_attire_product_detail)
                    dataBinding.storeTvEquityDescription.text =
                        "1、虚拟商品版权归红岩网校工作站所有。\n" +
                                "2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
                }
                StoreType.Product.GOODS -> {
                    dataBinding.storeTvProductDetailTitle.text =
                        getString(R.string.store_entity_product_detail)
                    dataBinding.storeTvEquityDescription.text =
                        "1、每个实物商品每人限兑换一次，已经兑换的商品不能退货换货也不予折现。\n" +
                                "2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
                }
            }
            //设置轮播图UrlList
            mImageList.clear()
            mImageList.addAll(it.urls)
            //初始化轮播图
            initSlideShow()
            //保存
            mData = it
        }

        // 请求购买成功的观察
        viewModel.exchangeResult.observeNotNull {
            // 根据不同商品类型弹出不同dialog
            when (mData.type) {
                StoreType.Product.DRESS -> {
                    //刷新兑换后的余额与库存 下同
                    mStampCount -= mData.price
                    mData.amount = it.amount //由兑换成功时获取到的最新amount来更新mData 下同
                    dataBinding.data = mData //重新绑定是实现 购买后库存为0时 兑换按钮置灰(是否置灰的逻辑绑定在xml里) 下同
                    dataBinding.storeTvUserStampCount.text =
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
                        dismissCallback = { dataBinding.storeBtnExchange.isClickable = true }
                    )
                }
                StoreType.Product.GOODS -> {
                    mStampCount -= mData.price
                    mData.amount = it.amount
                    dataBinding.data = mData
                    dataBinding.storeTvUserStampCount.text =
                        mStampCount.toString()
                    ExchangeDialog.show(
                        supportFragmentManager,
                        null,
                        type = ExchangeDialog.DialogType.ONR_BUTTON,
                        exchangeTips = "兑换成功！请在30天内到红岩网校领取哦",
                        onPositiveClick = { dismiss() },
                        dismissCallback = { dataBinding.storeBtnExchange.isClickable = true }
                    )
                }
            }
        }

        // 请求失败的观察
        viewModel.exchangeError.observeNotNull {
            when (it) {
                StoreType.ExchangeError.OUT_OF_STOCK -> {
                    ExchangeDialog.show(
                        supportFragmentManager,
                        null,
                        type = ExchangeDialog.DialogType.ONR_BUTTON,
                        exchangeTips = "啊欧，手慢了！下次再来吧=.=",
                        onPositiveClick = { dismiss() },
                        dismissCallback = { dataBinding.storeBtnExchange.isClickable = true }
                    )
                }
                StoreType.ExchangeError.NOT_ENOUGH_MONEY -> {
                    ExchangeDialog.show(
                        supportFragmentManager,
                        null,
                        type = ExchangeDialog.DialogType.ONR_BUTTON,
                        exchangeTips = "诶......邮票不够啊......穷日子真不好过呀QAQ",
                        onPositiveClick = { dismiss() },
                        dismissCallback = { dataBinding.storeBtnExchange.isClickable = true }
                    )
                }
                else -> {
                    toast("兑换请求异常")
                    dataBinding.storeBtnExchange.isClickable = true
                }
            }
        }
    }

    private fun initSlideShow() {
        if (!dataBinding.storeSlideShowExchangeProductImage.hasBeenSetAdapter()) {
            dataBinding.storeSlideShowExchangeProductImage
                .addTransformer(ScaleInTransformer())
                .addTransformer(AlphaTransformer())
                .openCirculateEnabled()
                .setImgAdapter(mImageList,
                    create = { holder ->
                        holder.view.setOnSingleClickListener {
                            // 装扮详情点击图片的元素共享动画
                            val options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    this, Pair<View, String>(
                                        dataBinding.storeSlideShowExchangeProductImage,
                                        "productImage"
                                    )
                                )

                            PhotoActivity.activityStart(
                                this, mImageList,
                                // 因为开启了循环滑动, 所以必须使用 getRealPosition() 得到你所看到的位置
                                dataBinding
                                    .storeSlideShowExchangeProductImage
                                    .getRealPosition(holder.layoutPosition),
                                options.toBundle()
                            )
                        }
                    },
                    refactor = { data, imageView, _, _ ->
                        imageView.setImageFromUrl(data)
                    })
        } else {
            dataBinding.storeSlideShowExchangeProductImage.notifyImgDataChange(mImageList)
        }
    }

    override fun onRestart() {
        super.onRestart()
        // 从 PhotoActivity 返回时就使轮播图跳转到对应位置
        dataBinding.storeSlideShowExchangeProductImage
            .setCurrentItem(PhotoActivity.SHOW_POSITION, false)
    }
}