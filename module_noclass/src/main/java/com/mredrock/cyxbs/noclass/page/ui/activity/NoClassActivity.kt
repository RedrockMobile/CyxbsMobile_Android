package com.mredrock.cyxbs.noclass.page.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.config.route.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGroupAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchStudentDialog
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassCourseVpFragment
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
   * 底部查询fragment的container
   */
  private val mCourseContainer : FrameLayout by R.id.noclass_course_bottom_sheet_container.view()
  
  private lateinit var mCourseSheetBehavior: BottomSheetBehavior<FrameLayout>
  
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
   * 当前选择分组
   * -1代表默认分组
   */
  private var mGroupId : String = "-1"
  
  /**
   * 默认分组的Group
   */
  private var mDefaultMembers = mutableListOf<NoclassGroup.Member>()
  
  /**
   * 记录待添加进组的学生
   */
  private var toBeAddStu = NoclassGroup.Member("","")
  
  /**
   * 记录待从组中删除的学生
   */
  private var toBeDeleteStu = NoclassGroup.Member("","")
  
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
    initSearchEvent()
    initImageView()
  }
  
  /**
   * 初始化用户信息
   */
  private fun initUserInfo(){
    ServiceManager.invoke(IAccountService::class).getUserService().apply {
      mUserName = this.getRealName()
      mUserId = this.getStuNum()
    }
    mDefaultMembers.add(NoclassGroup.Member(mUserName, mUserId))
  }
  
  /**
   * 初始化RV
   */
  private fun initRv(){
    mAdapter = NoClassGroupAdapter().apply {
      setOnItemDelete {
        //删除RV下方的成员
        if (mGroupId != "-1"){
          toBeDeleteStu = it
          viewModel.deleteNoclassGroupMember(mGroupId,it.stuNum)
        }else{
          mDefaultMembers.remove(it)
          mAdapter.submitList(mDefaultMembers.toMutableList())
        }
        
      }
    }
    mRecyclerView.adapter = mAdapter.apply {
      //添加的默认分组
      submitList(mDefaultMembers.toMutableList())
    }
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
      val intent = (Intent(this@NoClassActivity, GroupManagerActivity::class.java).apply {
        putExtra("GroupList",mList as Serializable)
      })
      startForResult.launch(intent)
    }
  }
  
  /**
   * 搜索操作的初始化
   */
  private fun initSearchEvent(){
    //防止软键盘弹起导致视图错位
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    mCourseSheetBehavior = BottomSheetBehavior.from(mCourseContainer)
    mCourseSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
      override fun onSlide(bottomSheet: View, slideOffset: Float) {
      
      }
    
      override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
          BottomSheetBehavior.STATE_EXPANDED -> { //展开操作
            mEditTextView.apply {
              clearFocus()
            }
          }
          BottomSheetBehavior.STATE_COLLAPSED -> { //折叠操作
            mEditTextView.apply {
              isFocusable = true
              isFocusableInTouchMode = true
              requestFocus()
            }
          }
          else -> {
            //忽略
          }
        }
      }
    })
    mCourseSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    
    //设置查询课表
    mBtnQuery.setOnSingleClickListener {
      doSearchCourse()
    }
    //设置键盘上点击搜索的监听
    mEditTextView.setOnEditorActionListener{ v, actionId, event ->
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        doSearchStu()
        return@setOnEditorActionListener true
      }
      return@setOnEditorActionListener false
    }
  }
  
  /**
   * 执行查找学生操作
   */
  private fun doSearchStu(){
    val name = mEditTextView.text.toString().trim()
    if (TextUtils.isEmpty(name)) {
      toast("输入为空")
      return
    }
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    mEditTextView.setText("")
    viewModel.searchStudent(stu = name)
  }
  
  /**
   * 执行查询课程的操作
   */
  private fun doSearchCourse(){
    
    //for test
    // ----
    SchoolCalendarUtil.updateFirstCalendar(1)
    // ----
    viewModel.getLessons(getCurrentGroup(mGroupId).members.map { it.stuNum })
    //在滑动下拉课表容器中添加整个课表
    replaceFragment(R.id.noclass_course_bottom_sheet_container){
      NoClassCourseVpFragment()
    }
    mCourseSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
   * 主要是观察livedata
   */
  private fun initNet(){
    var mSearchStudentDialog : SearchStudentDialog? = null
    //得到整个List
    viewModel.groupList.observe(this){
      mList = if (it.isNotEmpty()){
        it as MutableList<NoclassGroup>
      }else{
        mutableListOf()
      }
      initFlexLayout(mList,true)
    }
    //搜索学生
    viewModel.students.observe(this){ students ->
      if(students.isEmpty()){
         toast("查无此人")
       }else{
         mSearchStudentDialog = SearchStudentDialog(students){
           //点击分组事件回调
           if (mGroupId == "-1"){//默认分组情况
             if (mDefaultMembers.contains(it)){
               toast("用户已经存在分组中了哦~")
               return@SearchStudentDialog
             }
             toast("添加成功")
             //添加成功关闭自身
             mSearchStudentDialog?.dismiss()
             mDefaultMembers.add(it)
             mAdapter.submitList(mDefaultMembers.toMutableList())
           }else{//用户分组情况
            val currentGroup = getCurrentGroup(mGroupId)
             if (currentGroup.id =="-1"){
               return@SearchStudentDialog
             }
             if (currentGroup.members.contains(it)){
               toast("用户已经存在分组中了哦~")
               return@SearchStudentDialog
             }
             toBeAddStu = it
             viewModel.addNoclassGroupMember(mGroupId,it.stuNum)
           }
         }.apply {
           show(supportFragmentManager,"searchStudentDialog")
         }
       }
    }
    //添加人物
    viewModel.addState.observe(this){
      if (it.first){
        val currentGroup = getCurrentGroup(it.second)
        if (toBeAddStu.stuName.isNotEmpty() && toBeAddStu.stuNum.isNotEmpty())
        currentGroup.members += (toBeAddStu)
        toBeAddStu = NoclassGroup.Member("","")
        if (mGroupId == it.second){
          mAdapter.submitList(currentGroup.members.toMutableList())
        }
        toast("添加成功")
        mSearchStudentDialog?.dismiss()
      }else{
        toast("似乎出现了什么错误呢~")
      }
    }
    //删除人物
    viewModel.deleteState.observe(this){
      if (it.first){
        val currentGroup = getCurrentGroup(it.second)
        if (toBeDeleteStu.stuNum.isNotEmpty() && toBeDeleteStu.stuName.isNotEmpty())
        currentGroup.members -= (toBeDeleteStu)
        toBeDeleteStu = NoclassGroup.Member("", "")
        if (mGroupId == it.second){
          mAdapter.submitList(currentGroup.members.toMutableList())
        }
        toast("删除成功")
        mSearchStudentDialog?.dismiss()
      }else{
        toast("似乎出现了什么错误呢~")
      }
    }
  }
  
  /**
   * 得到当前选择的GroupId
   */
  private fun getCurrentGroup(groupId : String) : NoclassGroup{
    for (group in mList){
      if (group.id == groupId){
        return group
      }
    }
    return NoclassGroup("-1",false,mDefaultMembers,"DefaultMembers")
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
      
      //点击回调
      // groupId -1 为默认分组
      setOnItemSelected { it, bool ->
        if (bool) {
          mGroupId = it.id
          mAdapter.submitList(it.members.toMutableList())
        } else {
          mGroupId = "-1"
          mAdapter.submitList(mDefaultMembers.toMutableList())
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