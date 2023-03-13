package com.mredrock.cyxbs.food.ui.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.food.R
import com.mredrock.cyxbs.food.ui.adapters.FoodMainRvAdapter
import com.mredrock.cyxbs.food.ui.view.FoodMainDialog
import com.mredrock.cyxbs.food.viewmodel.FoodMainViewModel
import com.mredrock.cyxbs.lib.base.dailog.BaseDialog
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible

class FoodMainActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[FoodMainViewModel::class.java] }
    private val mRvRegion by R.id.food_main_rv_canteen_region.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.flexDirection = FlexDirection.ROW
            flexboxLayoutManager.flexWrap = FlexWrap.WRAP
            this.layoutManager = flexboxLayoutManager
            this.overScrollMode = 2
            this.isNestedScrollingEnabled = false
        }

    private val mRvNumber by R.id.food_main_rv_canteen_number.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.flexDirection = FlexDirection.ROW
            flexboxLayoutManager.flexWrap = FlexWrap.WRAP
            this.layoutManager = flexboxLayoutManager
            this.overScrollMode = 2
            this.isNestedScrollingEnabled = false
        }

    private val mRvFeature by R.id.food_main_rv_canteen_feature.view<RecyclerView>()
        .addInitialize {
            val flexboxLayoutManager = FlexboxLayoutManager(this@FoodMainActivity)
            flexboxLayoutManager.flexDirection = FlexDirection.ROW
            flexboxLayoutManager.flexWrap = FlexWrap.WRAP
            this.layoutManager = flexboxLayoutManager
            this.isNestedScrollingEnabled = false
            this.overScrollMode = 2
        }

    private val mImgNotification by R.id.food_main_img_notification.view<ImageView>()

    private val mBtnChange by R.id.food_main_btn_change.view<Button>()
    private val mBtnDetermine by R.id.food_main_btn_determine.view<Button>()
    private val mTvMeal by R.id.food_main_tv_meal.view<TextView>()

    //这个参数用于判断是否第一次请求随机生成
    private var mFirstDetermine = false

    private val dataRegion = listOf("1", "2", "3", "4", "5", "6", "7")
    private val dataNumber = listOf("1", "2", "3")
    private val dataFeature = listOf("1", "2", "3", "4", "5", "6", "重麻重辣")


    private val listRegion = arrayListOf(false, false, false, false, false, false, false)
    private val listNumber = arrayListOf(false, false, false)
    private val listFeature = arrayListOf(false, false, false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_activity_food_main)
        initRecycleViewAdapter()
        initView()
    }

    private fun initView() {
        mBtnDetermine.setOnSingleClickListener {
            //此时是未第一次点击的时候，查看详情还未显示出来
            if (mBtnChange.visibility == View.GONE) {
                mBtnChange.visible()
                mBtnDetermine.text = "查看详情"
                //第一次已请求
                mFirstDetermine = true
            }
            mTvMeal.text = "莘莘干锅鸡"
            val objectAnimation = ObjectAnimator.ofFloat(mTvMeal, "alpha", 0f, 1f)
            objectAnimation.duration = 1500
            objectAnimation.start()
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
                    buttonSize = Size(130,37)
                )
            ).setPositiveClick {
                this.dismiss()
            }
                .show()
        }
    }

    private fun initRecycleViewAdapter() {
        val adapterRegion = FoodMainRvAdapter(dataRegion) { state, position ->
            run {
                judgeBtnState(state, listRegion, position, mRvRegion)
            }
        }
        val adapterNumber = FoodMainRvAdapter(dataNumber) { state, position ->
            run {
                if (state) {
                    for (i in dataNumber.indices) {
                        mRvNumber.layoutManager?.findViewByPosition(i)
                            .run {
                                this?.findViewById<Button>(R.id.food_rv_item_btn)?.apply {
                                    if (position == i) {
                                        this.isSelected = true
                                        this.background =
                                            resources.getDrawable(R.drawable.food_shape_btn_pressed)
                                    } else {
                                        this.isSelected = false
                                        this.background =
                                            resources.getDrawable(R.drawable.food_shape_btn_normal)
                                    }
                                }
                                this?.findViewById<ConstraintLayout>(R.id.food_rv_item_cl)?.apply {
                                    if (position == i) {
                                        this.visible()
                                    } else {
                                        this.invisible()
                                    }
                                }
                            }
                    }
                } else {
                    for (i in dataNumber.indices) {
                        mRvNumber.layoutManager?.findViewByPosition(i)
                            .run {
                                this?.findViewById<Button>(R.id.food_rv_item_btn)?.apply {
                                    this.isSelected = false
                                    this.background =
                                        resources.getDrawable(R.drawable.food_shape_btn_normal)
                                }
                                this?.findViewById<ConstraintLayout>(R.id.food_rv_item_cl)?.apply {
                                    this.invisible()
                                }
                                listNumber.clear()
                            }
                    }
                    findViewById<ConstraintLayout>(R.id.food_rv_item_cl).apply {
                        this.invisible()
                    }
                }
            }
        }
        val adapterFeature = FoodMainRvAdapter(dataFeature) { state, position ->
            run {
                judgeBtnState(state, listFeature, position, mRvFeature)
            }
        }
        mRvRegion.adapter = adapterRegion
        mRvNumber.adapter = adapterNumber
        mRvFeature.adapter = adapterFeature
    }

    private fun judgeBtnState(
        state: Boolean,
        arraylist: ArrayList<Boolean>,
        position: Int,
        rv: RecyclerView
    ) {
        rv.layoutManager?.findViewByPosition(position)?.let {
            it.findViewById<Button>(R.id.food_rv_item_btn)
                ?.apply {
                    this.isSelected = state
                    if (state) {
                        this.background =
                            resources.getDrawable(R.drawable.food_shape_btn_pressed)
                    } else {
                        this.background =
                            resources.getDrawable(R.drawable.food_shape_btn_normal)
                    }
                }
            it.findViewById<ConstraintLayout>(R.id.food_rv_item_cl)
                ?.apply {
                    if (state) {
                        this.visible()
                    } else {
                        this.invisible()
                    }
                }
            arraylist[position] = state
        }
    }
}