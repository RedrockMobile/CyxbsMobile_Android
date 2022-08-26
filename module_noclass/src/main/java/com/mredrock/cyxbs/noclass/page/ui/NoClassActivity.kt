package com.mredrock.cyxbs.noclass.page.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.viewmodel.NoClassViewModel
import com.mredrock.cyxbs.noclass.util.collapseAnim
import com.mredrock.cyxbs.noclass.util.expandAnim
import com.mredrock.cyxbs.noclass.util.rotateAnim
import com.mredrock.cyxbs.noclass.widget.FlexHorizontalScrollView
import com.mredrock.cyxbs.noclass.widget.MyFlexLayout
import com.mredrock.cyxbs.noclass.widget.StickIndicator
import java.io.Serializable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      NoClassActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:07:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约主界面
 */


@Route(path = DISCOVER_NO_CLASS)
class NoClassActivity : BaseVmActivity<NoClassViewModel>(){

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView : EditText by R.id.et_noclass_add_classmate.view()

    /**
     *  主页面滑动的流式布局
     */
    private val mFlexHorizontalScrollView : FlexHorizontalScrollView by R.id.flex_noclass_container.view()

    /**
     * 分组管理的上拉下拉ICON
     */
    private val mImageView : ImageView by R.id.iv_noclass_show.view()

    /**
     * 进入分组管理中心文字
     */
    private val mTextView : TextView by R.id.tv_noclass_enter_group.view()

    /**
     * 指示器
     */
    private val mIndicator : StickIndicator by R.id.indicator_noclass_group.view()

    /**
     * 查询按钮
     */
    private val mBtnQuery : Button by R.id.noclass_btn_query.view()

    /**
     * 流式布局的展开动画
     */
    private var mCollapseAnim : ValueAnimator? = null

    /**
     * 流式布局的折叠动画
     */
    private var mExpandAnim : ValueAnimator? = null

    /**
     * icon展开动画
     */
    private var mIconCollapseAnim : ValueAnimator? = null

    /**
     * icon收缩动画
     */
    private var mIconExpandAnim : ValueAnimator? = null

    /**
     * 记录流式布局的状态
     */
    private var mFLexState = MyFlexLayout.FlexState.COLLAPSING

    /**
     * 整个分组数据
     */
    private var mList : List<NoclassGroup> = emptyList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_no_class)
        initTextView()
        initEditText()
        initImageView()
        initNet()
        //for test:
        var x = 0
        mIndicator.apply {
            setTotalCount(7)
        }
        mBtnQuery.setOnClickListener {
            mIndicator.setCurIndex(x++)
            if (x == 7){
                x = 0
            }
        }

        //****end****
    }

    /**
     * 初始化动画 在[initFlexLayout]之后自动调用
     * -> 如果FlexView被改变需要重新initAnim
     */
    private fun initAnim(){
        mFlexHorizontalScrollView.also {
            it.post {
                it.post {
                    mCollapseAnim = it.collapseAnim(it.height,200)
                    mExpandAnim = it.expandAnim(it.height,150)
                    //默认收缩
                    it.collapseAnim(it.height,1).start()
                }
                mIconCollapseAnim = mImageView.rotateAnim(180f,0f)
                mIconExpandAnim = mImageView.rotateAnim(0f,180f)
            }
        }
    }

    /**
     * 上拉下拉图片的点击效果 需要在[initAnim]之后调用
     */
    private fun initImageView(){
        mImageView.setOnClickListener {
            when(mFLexState){
                //折叠时点击
                MyFlexLayout.FlexState.COLLAPSING->{
                    if (mCollapseAnim?.isRunning == true){
                        mCollapseAnim?.cancel()
                    }
                    mIconExpandAnim?.start()
                    mExpandAnim?.start()
                    mFLexState = MyFlexLayout.FlexState.EXPANDING
                }
                //展开时点击
                MyFlexLayout.FlexState.EXPANDING->{
                    if (mExpandAnim?.isRunning == true){
                        mExpandAnim?.cancel()
                    }
                    mIconCollapseAnim?.start()
                    mCollapseAnim?.start()
                    mFLexState = MyFlexLayout.FlexState.COLLAPSING
                }
            }
        }
    }

    /**
     * 进入分组管理界面TextView
     */
    private fun initTextView(){
        mTextView.setOnClickListener {
            startActivity(Intent(this@NoClassActivity,GroupManagerActivity::class.java).apply {
                putExtra("GroupList",mList as Serializable)
            })
        }
    }

    /**
     * 上方编辑框的初始化
     */
    private fun initEditText(){
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = mEditTextView.text.toString().trim()
                if (TextUtils.isEmpty(key)) {
                    toast("输入为空")
                    return@setOnEditorActionListener true
                }
//                TODO  完成搜索操作
//                doSearch(key)
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                mEditTextView.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

    /**
     * 初始网络请求相关
     */
    private fun initNet(){
        viewModel.groupDetail.observe(this){
            mList = it
            initFlexLayout(mList)
        }
    }

    /**
     * 配置FlexLayout
     */
    private fun initFlexLayout(list: List<NoclassGroup>){
        mFlexHorizontalScrollView.setData(list, object : FlexHorizontalScrollView.OnCompleteCallback {
            //完成布局回调
            override fun onComplete(count: Int) {

            }
            //每次滑动的回调
            override fun onScroll(index: Int) {
            }
        })
        initAnim()
    }



}