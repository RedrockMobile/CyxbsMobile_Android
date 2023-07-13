package com.mredrock.cyxbs.food.ui.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Size
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.config.route.CENTER_FOOD_ENTRY
import com.mredrock.cyxbs.food.R
import com.mredrock.cyxbs.food.ui.adapters.FoodMainRvAdapter
import com.mredrock.cyxbs.food.ui.view.FoodDetailDialog
import com.mredrock.cyxbs.food.ui.view.FoodMainDialog
import com.mredrock.cyxbs.food.viewmodel.FoodMainViewModel
import com.mredrock.cyxbs.lib.base.dailog.BaseDialog
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.*

@Suppress("LABEL_NAME_CLASH")
@Route(path = CENTER_FOOD_ENTRY)
class FoodMainActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[FoodMainViewModel::class.java] }
    private val mRvRegion by R.id.food_main_rv_canteen_region.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            this.layoutManager = flexboxLayoutManager
            this.overScrollMode = 2
            this.isNestedScrollingEnabled = false
        }

    private val mRvNumber by R.id.food_main_rv_canteen_number.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            this.layoutManager = flexboxLayoutManager
            this.overScrollMode = 2
            this.isNestedScrollingEnabled = false
        }

    private val mRvFeature by R.id.food_main_rv_canteen_feature.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            this.layoutManager = flexboxLayoutManager
            this.isNestedScrollingEnabled = false
            this.overScrollMode = 2
        }

    private val mImgNotification by R.id.food_main_img_notification.view<ImageView>()

    private val mBtnChange by R.id.food_main_btn_change.view<Button>()
    private val mBtnDetermine by R.id.food_main_btn_determine.view<Button>()
    private val mTvMealNew by R.id.food_main_tv_meal_new.view<TextView>()
    private val mTvMealOld by R.id.food_main_tv_meal_old.view<TextView>()
    private val mClResult by R.id.food_main_cl_result.view<ConstraintLayout>()
    private val mBtnDetail by R.id.food_main_btn_detail.view<Button>()
    private val mImgRefresh by R.id.food_main_img_refresh.view<ImageView>()
    private val mImgPicture by R.id.food_main_img_picture.view<ImageView>()
    private val mClMeal by R.id.food_main_cl_meal.view<ConstraintLayout>()
    private var dialog:FoodDetailDialog? = null

    //是否改变标签，如果标签改变了换一换需要重新请求数据
    private var changeLabel = true

    private val foodRegionRvAdapter = FoodMainRvAdapter() { state, position ->
        changeBtnState(state, position, mRvRegion)
        viewModel.hashMapRegion.replace(viewModel.dataRegion[position], state)
    }
    private val foodNumRvAdapter = FoodMainRvAdapter() { state, position ->
        //先将所有的hashMap键值对改为false，因为是单选
        viewModel.run {
            hashMapNumber.replaceAll { _, _ ->
                false
            }
            //将所有的button状态改为false
            for (i in 0 until dataNumber.size) changeBtnState(false, i, mRvNumber)
            changeBtnState(state, position, mRvNumber)
            hashMapNumber.replace(dataNumber[position], state)
        }
    }
    private val foodFeatureRvAdapter = FoodMainRvAdapter() { state, position ->
        changeBtnState(state, position, mRvFeature)
        viewModel.apply { hashMapFeature.replace(dataFeature[position], state) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_activity_food_main)
        initObserver()
        initView()
    }

    private fun initObserver() {
        viewModel.foodMainBean.observe {
            mImgPicture.setImageFromUrl(it.picture)
            initRecycleViewAdapter()
        }
        viewModel.determineSuccess.observe {
            mBtnDetail.visible()
            mBtnChange.visible()
            mBtnDetermine.gone()
        }
        viewModel.foodResultBean.observe {
            changeLabel = false
            if (viewModel.foodNum < it.size) {
                mTvMealNew.text = it[viewModel.foodNum].name
                changeResult()
            } else {
                FoodMainDialog.Builder(
                    this,
                    data = ChooseDialog.Data(
                        width = 255,
                        height = 183,
                        content = "如果还没找到你喜欢的美食，可以尝试多选一些关键词哦！",
                        type = BaseDialog.DialogType.ONE_BUT,
                        buttonSize = Size(130, 37)
                    )
                ).setPositiveClick {
                    this.dismiss()
                }.show()
            }
        }
        viewModel.foodRefreshBean.observe {
            foodFeatureRvAdapter.submitList(viewModel.dataFeature)
            viewModel.run {
                hashMapFeature.replaceAll { t, u ->
                    false
                }
                //将所有的button状态改为false
                for (i in dataFeature.indices) changeBtnState(false, i, mRvFeature)
            }
        }
        viewModel.resultChoose.observe {
            if (!it) {
                FoodMainDialog.Builder(
                    this,
                    data = ChooseDialog.Data(
                        width = 255,
                        height = 183,
                        content = "请选择标签",
                        type = BaseDialog.DialogType.ONE_BUT,
                        buttonSize = Size(130, 37)
                    )
                ).setPositiveClick {
                    this.dismiss()
                }.show()
                viewModel.resultChoose.value = true
            }
        }
        viewModel.foodPraiseBean.observe {
            dialog?.findViewById<TextView>(R.id.food_dialog_tv_praise_num)?.text =
                it.praise_num.toString()
            if (it.praise_is) {
                dialog?.findViewById<Button>(R.id.food_dialog_detail_btn_positive)
                    ?.apply {
                        background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.food_shape_btn_praise
                        )
                    }
            } else {
                dialog?.findViewById<Button>(R.id.food_dialog_detail_btn_positive)
                    ?.apply {
                        background = AppCompatResources.getDrawable(
                            context,
                            R.drawable.food_shape_btn_determine
                        )
                    }
            }
        }
    }

    private fun initView() {
        mBtnDetermine.setOnSingleClickListener {
            //此时是未第一次点击的时候，查看详情还未显示出来
            viewModel.postFoodResult()
        }
        mBtnDetail.setOnSingleClickListener {
            viewModel.dataFoodResult[viewModel.foodNum].apply {
                dialog = FoodDetailDialog.Builder(
                    this@FoodMainActivity,
                    data = FoodDetailDialog.Data(
                        content = introduce,
                        foodName = name,
                        imageUrl = picture,
                        praiseNum = praise_num,
                        positiveButtonBackground = if (this.praise_is) R.drawable.food_shape_btn_praise else R.drawable.food_shape_btn_determine,
                        width = 255,
                        height = 330,
                    )
                )
                    .setNegativeClick {
                        this.dismiss()
                    }
                    .setPositiveClick {
                        viewModel.postFoodPraise(this@apply.name)
                    }
                    .build()
                dialog?.show()
            }
        }
        mBtnChange.setOnSingleClickListener {
            viewModel.apply {
                if (!changeLabel) {
                    if (foodNum < dataFoodResult.size - 1) {
                        foodNum++
                        mTvMealNew.text = viewModel.dataFoodResult[foodNum].name
                        changeResult()
                    } else {
                        FoodMainDialog.Builder(
                            this@FoodMainActivity,
                            data = ChooseDialog.Data(
                                width = 255,
                                height = 183,
                                content = "如果还没找到你喜欢的美食，可以尝试多选一些关键词哦！",
                                type = BaseDialog.DialogType.ONE_BUT,
                                buttonSize = Size(130, 37)
                            )
                        ).setPositiveClick {
                            this.dismiss()
                        }.show()
                    }
                } else {
                    viewModel.foodNum = 0
                    postFoodResult()
                }
            }
        }
        mImgNotification.setOnSingleClickListener {
            FoodMainDialog.Builder(
                this,
                data = ChooseDialog.Data(
                    width = 255,
                    height = 327,
                    content = "美食咨询处的设置，一" +
                            "是为了帮助各位选择综合症的邮子们更好的选择自己的需要的美食，对选择综合症说拜拜！二是为" +
                            "了各位初来学校的新生学子更好的体验学校各处的美食！按照要求通过标签进行选择，" +
                            "卷卷会帮助你选择最符合要求的美食哦！",
                    type = BaseDialog.DialogType.ONE_BUT,
                    buttonSize = Size(130, 37)
                )
            ).setPositiveClick {
                this.dismiss()
            }.show()
        }
        mImgRefresh.setOnClickListener {
            viewModel.postFoodRefresh()
        }
    }

    private fun initRecycleViewAdapter() {
        viewModel.apply {
            //可多选的情况，列表为就餐区域，餐饮特征
            mRvRegion.adapter = foodRegionRvAdapter
            foodRegionRvAdapter.submitList(dataRegion)
            mRvFeature.adapter = foodFeatureRvAdapter
            foodFeatureRvAdapter.submitList(dataFeature)
            //只可单选的情况，就餐人数
            mRvNumber.adapter = foodNumRvAdapter
            foodNumRvAdapter.submitList(dataNumber)
        }
    }

    /**
     * 改变button的状态，从选中到未选中或者从未选中到选中
     * 此时的state在rv回调的时候已经取了反，此时的state为需要改变的状态
     */
    private fun changeBtnState(
        state: Boolean,
        position: Int,
        rv: RecyclerView,
    ) {
        changeLabel = true
        rv.layoutManager?.findViewByPosition(position)?.let {
            it.findViewById<Button>(R.id.food_rv_item_btn)
                ?.apply {
                    this.isSelected = state
                    if (state) {
                        background =
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.food_ic_btn_pressed
                            )
                        setTextColor(getColor(R.color.food_main))
                    } else {
                        background =
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.food_shape_btn_normal
                            )
                        setTextColor(getColor(R.color.food_btn_pressed_text))
                    }
                }
        }
    }

    private fun changeResult() {
        viewTranslation(1000)
    }

    private fun viewTranslation(
        duration: Long
    ) {
        //当换一个按钮为可见时，说明菜品位置已经有菜品了，此时
        val translation = ObjectAnimator.ofFloat(
            mClMeal,
            "translationY",
            0f,
            mClMeal.height.toFloat() / 2 -10
        )
        translation.duration = duration
        translation.addListener {
            //动画完成后 新旧的view交换位置
            mTvMealOld.text = mTvMealNew.text
            mTvMealNew.text = ""
            val t2 = ObjectAnimator.ofFloat(
                mClMeal,
                "translationY",
                mClMeal.y,
                0f
            )
            t2.duration = 0
            t2.start()
        }
        translation.start()
    }

}