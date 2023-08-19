package com.mredrock.cyxbs.noclass.page.ui.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.ui.dialog.BatchInputErrorDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.BatchQueryErrorDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SameNameSelectionDialog
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassCourseVpFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.BatchAdditionViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.other.CourseViewModel
import com.mredrock.cyxbs.noclass.util.InputFormatUtil

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

    /**
     * 绑定部分view的实例
     */
    // 返回图标
    private val back2NoClassHome: ImageView by R.id.noclass_batch_iv_back.view()

    // 输入框区域
    private val batchInputBox: EditText by R.id.noclass_batch_edt_input.view()

    // 查询按钮
    private val batchQuery: Button by R.id.noclass_batch_btn_query.view()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_batch_addition)
        initObserve()
        initInteractView()
        initCourse()

    }

    private fun initInteractView() {
        initButton()
        initTextView()
        initEditText()
    }

    private fun initEditText() {
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        batchInputBox.hint =
            "样例输入1:卷卷\n                   卷娘\n样例输入2:2022213333\n                   2011118888\n错误输入1:卷卷，卷娘\n                   卷卷，卷娘\n错误输入2:卷卷\n                   2022222222"

    }

    private fun initTextView() {
        // 点击返回图标执行回退逻辑
        back2NoClassHome.setOnClickListener {
            onBackPressed()
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
            doSearch()
        }
    }

    /**
     * 执行搜索操作
     */
    private fun doSearch() {
        // 输入框内容的合法性检测
        val contentList = batchInputBox.text.toString().split("\n")
        // 状态位 , 输入内容是否符合标准
        var standardFlag = true
        // 0为纯数字序列标准, 1为纯汉字序列标准
        for (standardType in 0 until 2) {
            run loop@{
                when (standardType) {
                    0 -> {
                        standardFlag = true
                        contentList.forEach {
                            if (!InputFormatUtil.isNumbersSequence(it)) {
                                standardFlag = false
                                return@loop
                            }
                        }
                    }

                    1 -> {
                        standardFlag = true
                        contentList.forEach {
                            if (!InputFormatUtil.isChineseCharacters(it)) {
                                standardFlag = false
                                return@loop
                            }
                        }
                    }
                }
            }
            if (standardFlag) break
        }

        if (standardFlag) {
            // 发起信息检查请求
            batchAdditionViewModel.getCheckUploadInfoResult(contentList)
        } else {
            // 显示格式有误
            BatchInputErrorDialog(this).show()
        }

    }

    private fun initObserve() {
        // 观察noClassData数据, 数据更新后 在底部空闲课表放置区域（容器）中展开整个课表
        freeCourseViewModel.noclassData.observe(this) {
            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // 观察infoCheckResult数据, 数据更新后进行处理
        batchAdditionViewModel.infoCheckResult.observe(this) {
            if (it.isWrong) { // 检查后发现数据有误，即it.errList不为空
                BatchQueryErrorDialog(this, it.errList).show()
                return@observe
            }

            // 弹出重名的信息
            SameNameSelectionDialog(it.repeat).show(supportFragmentManager, "SameNameSelectionDialog")

            // 暂存normal的返回数据，待到重名信息选择完毕之后再一起给到ViewModel
            if (tempPreparedList.isNotEmpty())
                tempPreparedList.removeAll(tempPreparedList)
            it.normal.forEach { normal ->
                tempPreparedList.add(Pair(normal.id, normal.name))
            }
        }

        // 观察isCreateSuccess数据（即新建分组是否成功）, 数据更新后进行处理
        batchAdditionViewModel.isCreateSuccess.observe(this) {

        }

        // 观察selectedSameNameStudents数据，即是否进行了重名学生的选择, 数据更新代表完成了新一次的选择
        batchAdditionViewModel.selectedSameNameStudents.observe(this) {
            tempPreparedList.addAll(it)
            batchAdditionViewModel.setPreparedStudents(tempPreparedList)
        }

        // 观察selectedSameNameStudents数据, 数据更新就执行查询空闲课程的操作
        batchAdditionViewModel.batchAdditionStudents.observe(this) {
            val tempList = mutableListOf<String>() // 临时的stuNumList
            it.forEach { pair ->
                tempList.add(pair.first)
            }
            freeCourseViewModel.getLessonsFromNum2Name(tempList, it)
        }
    }

    private fun initCourse() {
        // 绑定行为和布局
        mCourseSheetBehavior = BottomSheetBehavior.from(freeCourseContainer)
        // 重置底部空闲课表放置区域的课表状态
        replaceFragment(R.id.noclass_course_bottom_sheet_container) {
            NoClassCourseVpFragment.newInstance(NoClassSpareTime.EMPTY_PAGE)
        }
        mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onBackPressed() {
        if (mCourseSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }
}