package com.mredrock.cyxbs.noclass.page.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.NoClassTemporaryAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.GroupDetailViewModel

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      GroupDetailActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 04:34:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    具体分组页面
 */
class GroupDetailActivity : BaseActivity(){
    
    private val mViewModel by viewModels<GroupDetailViewModel>()

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView : EditText by R.id.et_noclass_group_add_classmate.view()

    /**
     * 标题文字
     */
    private val mTitleText : TextView by R.id.tv_noclass_detail_title.view()

    /**
     * RV
     */
    private val mRecyclerView : RecyclerView by R.id.rv_noclass_group_detail_container.view()

    /**
     * 按钮
     */
    private val mBtnQuery : Button by R.id.btn_noclass_group_detail_save.view()


    /**
     * 当前选择的NoclassGroup
     */
    private lateinit var mCurrentNoclassGroup : NoclassGroup
    
    /**
     * Adapter
     */
    private val mAdapter : NoClassTemporaryAdapter by lazy { NoClassTemporaryAdapter() }

    /**
     * 删除缓冲区
     */
    private val mWaitDeleteList : ArrayList<Student> by lazy { ArrayList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_detail)
        getList()
        initObserve()
        initRv()
        initTextView()
        initEditText()
        initQuery()
    }

    /**
     * 接受外部传来的数据
     */
    private fun getList(){
        mCurrentNoclassGroup = intent.getSerializableExtra("NoClassGroup") as NoclassGroup
        mTitleText.text = mCurrentNoclassGroup.name
    }
    
    /**
     * 初始化观测livedata
     */
    private fun initObserve(){
        //观察搜索结果出来就弹窗
        var searchAllDialog: SearchAllDialog?
        mViewModel.searchAll.observe(this){it ->
            Log.d("lx", "searchResult: =${it}")
            if (it.isExist) {
                searchAllDialog = SearchAllDialog(
                    searchResult = it,
                    groupId = mCurrentNoclassGroup.id
                ).apply {
                    setOnClickGroupDetailAdd { students ->
                        val stuList = mAdapter.currentList.toMutableSet()
                        stuList.addAll(students)
                        mAdapter.submitList(stuList.toList())
                        Log.d("lx", "添加成功 = ${students}} ")
                    }
                }
                searchAllDialog!!.show(supportFragmentManager, "SearchAllDialog")
            } else {
                toast("查无此人")
            }
        }
        // 监听删除是否成功决定本地是否删除
        mViewModel.deleteMembers.observe(this){
            // 如果删除成功
            if (it.second){
                for (stu in mWaitDeleteList){
                    // 如果待删除学生的id和要删除的学生的id一致,就真的删除，并且移除缓冲区中的元素
                    if (stu.id == it.first){
                        mAdapter.deleteMember(stu)
                        mWaitDeleteList.remove(stu)
                    }
                }
            }else{
                toast("删除失败")
            }
        }
    }
    
    /**
     * 初始化RV
     */
    private fun initRv(){
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GroupDetailActivity)
            adapter = mAdapter.apply {
                //设置删除功能
                setOnItemDelete {
                    mWaitDeleteList.add(it)
                    mViewModel.deleteMembers(mCurrentNoclassGroup.id,it)
                }
            }
        }
        // 将跳转过来的数据填充进去
        mAdapter.submitList(mCurrentNoclassGroup.members)
    }

    /**
     * 完成上方标题点击初始化
     */
    private fun initTextView(){
        // 点击回退到主界面
        findViewById<ImageView>(R.id.iv_noclass_group_detail_return).apply {
            setOnClickListener {
                onBackPressed()
            }
        }
    }

    /**
     * 这个是查询课表按钮
     */
    private fun initQuery(){
        mBtnQuery.setOnClickListener {
            //记得将用户本人放进去一起查
        }
    }

    /**
     * 上方编辑框的初始化
     */
    private fun initEditText(){
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
    
    /**
     * 执行搜索操作
     */
    private fun doSearch(){
        val content = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            toast("输入为空")
            return
        }
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        mEditTextView.setText("")
        mViewModel.getSearchAllResult(content)
    }

//    override fun onBackPressed() {
//        //todo 先将课表界面退出
//        if (mCourseSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        } else {
//            setResult(RESULT_OK, Intent().apply {
//                //todo 等待添加完毕之后，返回具体的值
//            })
//            super.onBackPressed()
//        }
//    }
}