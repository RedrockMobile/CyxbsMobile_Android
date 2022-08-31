package com.mredrock.cyxbs.noclass.page.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.config.route.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGroupAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.NoClassViewModel
import com.mredrock.cyxbs.noclass.util.alphaAnim
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
     * 用户名称
     */
    private lateinit var mUserName : String

    /**
     * 用户id
     */
    private lateinit var mUserId : String

    /**
     * 界面人员RV
     */
    private val mRecyclerView : RecyclerView by R.id.rv_noclass_container.view()

    /**
     * Rv的Adapter
     */
    private lateinit var mAdapter : NoClassGroupAdapter

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
     * 指示器淡出
     */
    private var mIndicatorShow : ValueAnimator? = null

    /**
     *  指示器淡入动画
     */
    private var mIndicatorHide : ValueAnimator? = null

    /**
     * 直接打开的动画
     */
    private lateinit var mExpandImmediately : ValueAnimator

    /**
     * 记录流式布局的状态
     */
    private var mFLexState = MyFlexLayout.FlexState.COLLAPSING

    /**
     * 整个分组数据
     */
    private var mList : MutableList<NoclassGroup> = mutableListOf()

    /**
     * groupId
     *
     * -1代表默认分组
     */
    private var mGroupId : String = "-1"

    /**
     * 默认分组的Group
     */
    private var mDefaultGroup = NoclassGroup("-1",false, emptyList(),"defaultGroup")

    /**
     * 是否有更改值
     */
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val extra = intent?.getSerializableExtra("GroupListResult")
            if (extra != null){
                mList = extra as MutableList<NoclassGroup>
                initFlexLayout(mList,false)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_no_class)
        initUserInfo()
        initNet()
        initRv()
        initTextView()
        initEditText()
        initImageView()
    }

    /**
     * 初始化用户信息
     */
    private fun initUserInfo(){
        ServiceManager.invoke(IAccountService::class).getUserService().apply {
            val stuName = this.getRealName()
            val stuNum = this.getStuNum()
            Log.e("stuName",stuName.toString())
            Log.e("stuName",stuNum.toString())
        }
    }

    /**
     * 初始化RV
     */
    private fun initRv(){
        mAdapter = NoClassGroupAdapter().apply {
            setOnItemDelete {
                //删除RV下方的成员
                if (mGroupId != "-1"){
                    viewModel.deleteNoclassGroupMember(mGroupId,it.stuNum)
                }
            }
        }
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 初始化动画 在[initFlexLayout]之后自动调用
     * -> 如果FlexView被改变需要重新initAnim
     *
     * @param hide 是否需要收缩view
     */
    private fun initAnim(hide: Boolean){
        mFlexHorizontalScrollView.also {
            it.post {
                it.post {
                    mCollapseAnim = it.collapseAnim(it.height,200)
                    mExpandAnim = it.expandAnim(it.height,150)
                    mExpandImmediately = it.expandAnim(it.height,1)
                    //默认收缩
                    if (hide)
                    it.collapseAnim(it.height,1).start()
                }
                mIconCollapseAnim = mImageView.rotateAnim(180f,0f)
                mIconExpandAnim = mImageView.rotateAnim(0f,180f)
            }
        }
        mIndicator.also {
            it.post {
                mIndicatorShow = mIndicator.alphaAnim(0f,1f).apply {
                    doOnStart { _ ->
                        it.visibility = View.VISIBLE
                    }
                }
                mIndicatorHide = mIndicator.alphaAnim(1f,0f).apply {
                    doOnEnd { _ ->
                        it.visibility = View.GONE
                    }
                }
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
                    mIndicatorShow?.start()
                    mFLexState = MyFlexLayout.FlexState.EXPANDING
                }
                //展开时点击
                MyFlexLayout.FlexState.EXPANDING->{
                    if (mExpandAnim?.isRunning == true){
                        mExpandAnim?.cancel()
                    }
                    mIconCollapseAnim?.start()
                    mCollapseAnim?.start()
                    mIndicatorHide?.start()
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
            val intent = (Intent(this@NoClassActivity,GroupManagerActivity::class.java).apply {
                putExtra("GroupList",mList as Serializable)
            })
            startForResult.launch(intent)
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
     * 流式布局下方的指示器
     */
    private fun initIndicator(total : Int){
        if (total <= 0) return
        mIndicator.visibility = if (mFLexState === MyFlexLayout.FlexState.COLLAPSING) View.GONE else View.VISIBLE
        mIndicator.reset()
        mIndicator.setTotalCount(total)
    }

    /**
     * 初始网络请求相关
     */
    private fun initNet(){
        viewModel.groupList.observe(this){
            if (it.isNotEmpty()){
                mList = it as MutableList<NoclassGroup>
            }else{
                mList = mutableListOf()
            }
            initFlexLayout(mList,true)
        }
    }

    /**
     * 配置FlexLayout
     * @param firstTime 是否是第一次
     */
    private fun initFlexLayout(list: List<NoclassGroup>,firstTime : Boolean) {

        if (mFLexState === MyFlexLayout.FlexState.COLLAPSING && !firstTime) {
            //不是第一次被调用的同时执行是在折叠状态就需要先打开
            mExpandImmediately.start()
        }

        mFlexHorizontalScrollView.apply {
            setData(list)
            setOnCompleteCallBack(object : FlexHorizontalScrollView.OnCompleteCallback{
                override fun onComplete(count: Int) {
                    initIndicator(count)
                }

                override fun onScroll(index: Int) {
                    mIndicator.setCurIndex(index)
                }

            })
            setOnItemSelected { it, bool ->
                if (bool) {
                    mGroupId = it.id
                    mAdapter.submitList(it.members)
                } else {
                    mGroupId = "-1"
                    mAdapter.submitList(mDefaultGroup.members)
                }
            }
        }


        if (!firstTime && mFLexState === MyFlexLayout.FlexState.COLLAPSING) {
            initAnim(true)
        }else if (!firstTime){
            initAnim(false)
        }else{
            initAnim(true)
        }
    }
}