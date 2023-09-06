package com.mredrock.cyxbs.noclass.page.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.ui.dialog.BatchInputErrorDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.BatchQueryErrorDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.IsCreateSolidDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SameNameSelectionDialog
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassCourseVpFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.BatchAdditionViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.other.CourseViewModel
import com.mredrock.cyxbs.noclass.util.InputFormatUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description:
 *
 */
class BatchAdditionActivity : BaseActivity() {
    // 管理空闲课表数据的viewModel
    private val freeCourseViewModel by viewModels<CourseViewModel>()

    // 管理批量添加页面数据的viewModel
    private val batchAdditionViewModel by viewModels<BatchAdditionViewModel>()

    // 底部空闲课表放置区域
    private val freeCourseContainer: FrameLayout by R.id.noclass_course_bottom_sheet_container.view()

    // 底部BottomSheetFragmentDialog的Behavior，例如课表的  向上展开  与  向下折叠 行为
    private lateinit var mCourseSheetBehavior: BottomSheetBehavior<FrameLayout>

    // 取消顶部状态栏
    override val isCancelStatusBar: Boolean
        get() = true

    // 批量添加 已经检查好的学生list 的缓冲list
    private var tempPreparedList = mutableListOf<Pair<String, String>>()

    // 批量添加 已经检查好的学生的学号list
    private var tempStuNumList = mutableListOf<String>()

    // 状态位，是否已经将最新的一次成功查询的结果中的normal信息添加到了tempPreparedList中
    private var isSuccessSaveLatestNormal = true

    /**
     * 绑定部分view的实例
     */
    // 返回图标
    private val back2NoClassHome: ImageView by R.id.noclass_batch_iv_back.view()

    // 输入框区域
    private val batchInputBox: EditText by R.id.noclass_batch_edt_input.view()

    // 查询按钮
    private val batchQuery: Button by R.id.noclass_batch_btn_query.view()

    // 编辑框的提示
    private val editHint: Group by R.id.noclass_batch_group_edit_hint.view()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_batch_addition)
        initBackCallBack()
        initAction()
        initObserve()
        initInteractView()
        initCourse()

    }

    /**
     * 点击屏幕内除了编辑框的任何位置，取消编辑框焦点
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        batchInputBox.clearFocus()
        return super.onTouchEvent(event)
    }

    /**
     * 一些可交互view的初始化
     */
    private fun initInteractView() {
        initButton()
        initTextView()
        initEditText()
    }

    private fun initEditText() {
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        editHint.visible()
        batchInputBox.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                editHint.visible()
                return@addTextChangedListener
            }
            editHint.gone()
        }
    }

    private fun initTextView() {
        // 点击返回图标执行回退逻辑
        back2NoClassHome.setOnClickListener {
            finish()
        }
    }

    private fun initButton() {
        // 每秒最多一次的防抖点击
        batchQuery.setOnSingleClickListener(1000) {
            // 输入框非空检测
            val rawContent = batchInputBox.text.toString()
            if (InputFormatUtil.isNoInput(rawContent)) {
                toast("输入为空")
                return@setOnSingleClickListener
            }
            if (isSuccessSaveLatestNormal) // 当前保存的Normal已经是最新的normal才允许执行下一次的查询操作
                doSearch()
        }
    }

    /**
     * 执行搜索操作
     */
    private fun doSearch() {
        // 首先将输入框的内容按换行符分割
        val contentList = batchInputBox.text.toString().trim() // 先去除原始内容的 前导与后驱的所有空字符
            .split("\n").map { it.trim() }

        // 然后进行输入框内容的合法性检测
        /**
         * 去除字符串所有空格
         * str.replace("\\s".toRegex(), "")
         */
        // 状态位 , 输入内容是否符合已定义的标准
        var standardFlag: Boolean
        // 0为未在已有字符串标准找到, 1为纯数字序列标准, 2为纯汉字序列标准
        val standardType = InputFormatUtil.isWhatType(contentList[0])

        run loop@{
            when (standardType) {
                1 -> {
                    standardFlag = true
                    contentList.forEach {
                        if (!InputFormatUtil.isNumbersSequence(it)) {
                            // 检测到内容不是纯数字序列
                            standardFlag = false
                            return@loop
                        }
                    }
                }

                2 -> {
                    standardFlag = true
                    contentList.forEach {
                        if (!InputFormatUtil.isChineseCharacters(it)) {
                            // 检测到内容不是纯汉字序列
                            standardFlag = false
                            return@loop
                        }
                    }
                }
                else ->{ // 检测到内容不是 已定义的字符序列
                    standardFlag = false
                }
            }
        }

        if (standardFlag) {
            // 发起信息检查请求
            batchAdditionViewModel.getCheckUploadInfoResult(contentList)
        } else {
            // 格式有误，显示对应dialog
            BatchInputErrorDialog(this).show()
        }

    }

    private fun initObserve() {
        // 观察noClassData数据, 数据更新后 在底部空闲课表放置区域（容器）中展开整个课表
        freeCourseViewModel.noclassData.observe(this) {
            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * 初始化一些与行为强关联的逻辑
     */
    private fun initAction() {
        // 发起信息检查请求后，收集服务器返回的数据，并处理
        batchAdditionViewModel.getInfoCheckResult.collectLaunch {
            if (it.isWrong) { // 检查后发现传入的数据有误，即it.errList不为空（逻辑上是这样，但得考虑到数据出错的情况）
                // 已要求后端哪怕传入的数据没有err，也得返回应该空list而不是直接返回null
                BatchQueryErrorDialog(this, it.errList).show()
                return@collectLaunch
            }
            // 传入的数据无误，有了最新版的normal数据
            isSuccessSaveLatestNormal = false
            if (!it.repeat.isNullOrEmpty()) {
                // 弹出重名的信息列表

                SameNameSelectionDialog(it.repeat).show(
                    supportFragmentManager,
                    "SameNameSelectionDialog"
                )
            }
            // 暂存最新的normal数据，待到重名信息选择完毕之后再一起给到freeCourseViewModel进行空闲课表的网络请求
            tempPreparedList.clear()
            tempStuNumList.clear()
            tempPreparedList = mutableListOf()
            tempStuNumList = mutableListOf()
            it.normal?.forEach { normal ->
                tempPreparedList.add(Pair(normal.id, normal.name))
                tempStuNumList.add(normal.id)
            }
            // 最新的normal数据暂存成功
            isSuccessSaveLatestNormal = true
            if (it.repeat.isNullOrEmpty()) { // 没有重名信息数组
                if(tempPreparedList.isNotEmpty())
                    freeCourseViewModel.getLessonsFromNum2Name(tempStuNumList, tempPreparedList)
                else
                    "没有查到任何结果o(╥﹏╥)o".toast() // 最新的normal数据为空则不进行后续步骤
            }
        }

        // 从弹出重名的信息列表开始，直到点击了确认按钮后，收集在这期间选择的重名学生信息
        batchAdditionViewModel.getSelectedSameNameStudents.collectLaunch {
            if (it.isEmpty()) {  // 有重名学生，但未进行重名学生的选择
                waitLatestNormalSave {
                    if(tempPreparedList.isNotEmpty())
                        freeCourseViewModel.getLessonsFromNum2Name(tempStuNumList, tempPreparedList)
                    else
                        "没有查到任何结果o(╥﹏╥)o".toast()
                }
                return@collectLaunch
            }
            // 进行了重名学生的选择
            waitLatestNormalSave {
                tempPreparedList.addAll(it)
                it.forEach { selected ->
                    tempStuNumList.add(selected.first)
                }
                batchAdditionViewModel.setPreparedStudents(tempPreparedList)
            }
        }

        batchAdditionViewModel.getBatchAdditionStudents.collectLaunch {
            // 进行空闲课表查询
            /**
             * 下面这种获取StuNumList数据会更准确，但会更消耗时间
             */
//            val tempList = mutableListOf<String>() // 临时的stuNumList
//            it.forEach { pair ->
//                tempList.add(pair.first)
//            }
            if(tempPreparedList.isNotEmpty())
                freeCourseViewModel.getLessonsFromNum2Name(tempStuNumList, it)
            else
                "没有查到任何结果o(╥﹏╥)o".toast()
        }
    }

    /**
     * 初始化空闲课表
     */
    private fun initCourse() {
        // 绑定行为和布局
        mCourseSheetBehavior = BottomSheetBehavior.from(freeCourseContainer)
        // 重置底部空闲课表放置区域的课表状态
        replaceFragment(R.id.noclass_course_bottom_sheet_container) {
            NoClassCourseVpFragment.newInstance(NoClassSpareTime.EMPTY_PAGE)
        }
        mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mCourseSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { // 课表展开
                        // 记录下次是否不再提醒 的键值对，默认值为false，也就是默认下次需要提醒
                        if (!defaultSp.getBoolean("NeverRemindNextOnNoClass", false)) {
                            IsCreateSolidDialog(this@BatchAdditionActivity).apply {
                                // 等待弹窗的选择，然后进入下一个弹窗
                                setOnReturnClick { dialog, isRemind ->
                                    if (isRemind) {
                                        dialog.dismiss()
                                        // 记录下次是否需要该询问弹窗dialog
                                        defaultSp.edit()
                                            .putBoolean("NeverRemindNextOnNoClass", true).apply()
                                    }
                                    dialog.dismiss()
                                }
                                setOnContinueClick { dialog, isRemind ->
                                    if (isRemind) {
                                        dialog.dismiss()
                                        // 记录下次是否需要该询问弹窗dialog
                                        defaultSp.edit()
                                            .putBoolean("NeverRemindNextOnNoClass", true).apply()
                                    }
                                    dialog.dismiss()
                                    CreateGroupDialog {
                                        // 创建成功之后的操作
                                        setResult(
                                            RESULT_OK,
                                            Intent().putExtra("BulkAdditions", true)
                                        )
                                    }.apply {
                                        setExtraMembers(tempStuNumList.toList())
                                    }.show(
                                        supportFragmentManager,
                                        "CreateGroupDialogFragment"
                                    )
                                }
                            }.show()
                        }
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> { //折叠操作
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })
    }

    /**
     * 由于onBackPress已经废弃，所以改用OnBackPressedDispatcher
     */
    private fun initBackCallBack() {
        val backCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mCourseSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallBack)
    }

    /**
     * 等待最新的请求到的normal数据遍历暂存完成
     *
     * ## **tip:**
     * 由于查询空闲课表的操作无可避免的需要用到服务器返回的最新normal数据 （必须要保证最新且完整）
     * 故使用状态位``isSuccessSaveLatestNormal``表示当前暂存的normal数据是否为最新（且完整的）
     * 当有操作需要用到保存的normal时，就得保证保存的normal已经更新完成
     *
     * ### 为什么必须要保证是最新的normal：
     * 信息有时效性，服务器返回的结果与当次发起查询的行为有强关联性
     * （相当于该结果就是一个应该在 得到当次查询结果之后~下次查询行为开始前 被消费的事件）
     *
     * @param daAfterFinished       最新的normal数据暂存完成后的操作
     */
    private fun waitLatestNormalSave(daAfterFinished: () -> Unit) {
        if (isSuccessSaveLatestNormal) {
            daAfterFinished.invoke()
            return
        }
        lifecycleScope.launch {
            while (true){
                if (isSuccessSaveLatestNormal) {
                    daAfterFinished.invoke()
                    break
                }
                delay(200)
            }
        }
    }
}