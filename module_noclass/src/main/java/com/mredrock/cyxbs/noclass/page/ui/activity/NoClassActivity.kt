package com.mredrock.cyxbs.noclass.page.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.config.route.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.IsCreateSolidDialog
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassCourseVpFragment
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassSolidFragment
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassTemporaryFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.NoClassViewModel

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      NoClassActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:07:00
 * @UpdateRemark:   更新说明： 主页不需要网络删除成员
 * @Version:        1.1
 * @Description:    没课约主界面
 */

@Route(path = DISCOVER_NO_CLASS)
class NoClassActivity : BaseActivity() {

    private val mViewModel by viewModels<NoClassViewModel>()

    /**
     * 底部查询fragment的container
     */
    private val mCourseContainer: FrameLayout by R.id.noclass_course_bottom_sheet_container.view()

    private lateinit var mCourseSheetBehavior: BottomSheetBehavior<FrameLayout>

//  /**
//   * 获取批量添加界面的返回值
//   */
//  private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//    if (result.resultCode == Activity.RESULT_OK) {
//      val intent = result.data
//      val extra = intent?.getSerializableExtra("GroupListResult")
//      if (extra != null){
//        mList = extra as MutableList<NoclassGroup>
//        initFlexLayout(mList,false)
//      }
//    }
//  }
//
    /**
     * 取消状态栏
     */
    override val isCancelStatusBar: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_no_class)
        replaceFragment(R.id.fragment_container) {
//            NoClassTemporaryFragment.newInstance()
            NoClassSolidFragment.newInstance()
        }
        initObserve()
        initQueryEvent()
        initCourse()
    }

    private fun initCourse() {
        //先将课表替换为空课表
        replaceFragment(R.id.noclass_course_bottom_sheet_container) {
            NoClassCourseVpFragment.newInstance(NoClassSpareTime.EMPTY_PAGE)
        }
        mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /**
     * 搜索操作的初始化
     */
    private fun initQueryEvent() {
        mCourseSheetBehavior = BottomSheetBehavior.from(mCourseContainer)
        mCourseSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { //展开操作
                        //下次不再提醒，默认为false，也就是提醒,但是为false，进不去条件判断，这里取反，提醒之后的弹窗展示
                        if (!defaultSp.getBoolean("NeverRemindNextOnNoClass", false)) {
                            IsCreateSolidDialog(this@NoClassActivity).apply {
                                //等待弹窗的选择，然后进入下一个弹窗
                                setOnReturnClick { dialog, isRemind ->
                                    if (isRemind) {
                                        dialog.dismiss()
                                        defaultSp.edit()
                                            .putBoolean("NeverRemindNextOnNoClass", true).apply()
                                    }
                                    dialog.dismiss()
                                }
                                setOnContinueClick { dialog, isRemind ->
                                    if (isRemind) {
                                        dialog.dismiss()
                                        defaultSp.edit()
                                            .putBoolean("NeverRemindNextOnNoClass", true).apply()
                                    }
                                    dialog.dismiss()
                                    CreateGroupDialog().show(
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
        })
    }

    private fun initObserve() {
        //在滑动下拉课表容器中添加整个课表,等待fragment中请求数据
        mViewModel.noclassData.observe(this) {
            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onBackPressed() {
        if (mCourseSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

}