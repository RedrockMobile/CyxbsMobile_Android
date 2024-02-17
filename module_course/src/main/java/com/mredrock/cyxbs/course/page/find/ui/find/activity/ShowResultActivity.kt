package com.mredrock.cyxbs.course.page.find.ui.find.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.course.page.find.ui.course.stu.FindStuCourseFragment
import com.mredrock.cyxbs.course.page.find.ui.course.tea.FindTeaCourseFragment
import com.mredrock.cyxbs.course.page.find.ui.find.adapter.ShowStuResultRvAdapter
import com.mredrock.cyxbs.course.page.find.ui.find.adapter.ShowTeaResultRvAdapter
import com.mredrock.cyxbs.course.page.find.viewmodel.activity.ShowResultViewModel
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.operations.doIfLogin
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.utils.BindView
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 20:18
 */
class ShowResultActivity : BaseActivity() {
  
  companion object {
    
    fun startActivity(
      context: Context,
      data: PersonData,
    ) {
      context.startActivity(
        Intent(context, ShowResultActivity::class.java).apply {
          putExtra(ShowResultActivity::mPersonData.name, data)
        }
      )
    }
  }
  
  private val mViewModel by viewModels<ShowResultViewModel>()
  
  private val mImgBtnBack by R.id.course_ib_show_result_back.view<ImageButton>()
  private val mBottomSheetView by R.id.course_bsb_show_result_course.view<FrameLayout>()
  private val mBottomSheet by lazyUnlock {
    BottomSheetBehavior.from(mBottomSheetView)
  }
  
  private var mPersonData by intent<PersonData>()
  
  private val mPersonHandler: PersonHandler by lazyUnlock {
    PersonHandler.getHandler(mPersonData, this)
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.course_activity_show_results)
    initView()
    initImgButton()
  }
  
  private fun initView() {
    mPersonHandler.apply { initialize() }
    mBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
              mCollapsedBackPressedCallback.isEnabled = true
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
              mCollapsedBackPressedCallback.isEnabled = false
            }
            else -> {}
          }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          if (slideOffset >= 0) {
            mBottomSheetView.alpha = slideOffset
          }
        }
      }
    )
  }
  
  private fun initImgButton() {
    mImgBtnBack.setOnSingleClickListener {
      finishAfterTransition()
    }
  }
  
  /**
   * 用于拦截返回键，在 BottomSheet 未折叠时先折叠
   */
  private val mCollapsedBackPressedCallback by lazyUnlock {
    onBackPressedDispatcher.addCallback {
      mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }
  }
  
  sealed interface PersonData : Serializable
  
  // 学生数据
  data class StuData(
    val studentList: List<FindStuBean>,
    val linkStudent: LinkStuEntity?,
  ) : PersonData
  
  // 老师数据
  data class TeaData(
    val teacherList: List<FindTeaBean>,
  ) : PersonData
  
  /**
   * 用于分离学生和老师搜索的结果
   */
  private sealed class PersonHandler(val activity: ShowResultActivity) {
    companion object {
      fun getHandler(data: PersonData, activity: ShowResultActivity): PersonHandler {
        return when (data) {
          is StuData -> StudentHandler(data, activity)
          is TeaData -> TeacherHandler(data, activity)
        }
      }
    }
    
    abstract fun ShowResultActivity.initialize()
    
    protected fun <T : View> Int.view(): BindView<T> = BindView(this, activity)
    
    // 这里是需要不同实现的控件
    protected val mTvTitle: TextView by R.id.course_tv_show_result_title.view()
    protected val mRvResult: RecyclerView by R.id.course_rv_show_result.view<RecyclerView>()
      .addInitialize {
        layoutManager = LinearLayoutManager(activity)
      }
  }
  
  private class StudentHandler(
    var data: StuData,
    activity: ShowResultActivity,
  ) : PersonHandler(activity) {
    
    private lateinit var mRvAdapter: ShowStuResultRvAdapter
    
    override fun ShowResultActivity.initialize() {
      mTvTitle.text = "同学课表"
      initRecyclerView()
      initObserve()
    }
    
    /**
     * 初始化 RecyclerView
     *
     * 写出这么长的代码我很抱歉，因为 Adapter 中不应该写这些逻辑，他们应该通过回调让 Activity 操作
     *
     * 整体是通过链式调用的，稍稍看一下就能看懂
     */
    private fun ShowResultActivity.initRecyclerView() {
      mRvResult.adapter = ShowStuResultRvAdapter()
        .apply {
          mRvAdapter = this
          submitList( // 提交数据
            data.studentList.map {
              ShowStuResultRvAdapter
                .FindStuWithLinkBean(it, it.stuNum == data.linkStudent?.linkNum)
            }
          )
        }.setOnItemClick {
          // 点击整个 item
          mViewModel.saveHistory(this) // 保存搜索历史
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
          FindStuCourseFragment.tryReplaceOrFresh(supportFragmentManager, mBottomSheetView.id, num)
        }.setOnLinkNoClick {
          // 点击没有关联的图标
          doIfLogin { // 登录才能使用
            if (data.linkStudent == null) {
              // 这里说明他没有关联人
              mViewModel.changeLinkStudent(stuNum)
              mViewModel.saveHistory(this) // 保存搜索历史
            } else {
              ChooseDialog.Builder(
                this@initRecyclerView,
                ChooseDialog.DataImpl(
                  content = "你已有一位关联的同学\n确定要替换吗？",
                  width = 255,
                  height = 167
                )
              ).setPositiveClick {
                mViewModel.changeLinkStudent(stuNum)
                mViewModel.saveHistory(this@setOnLinkNoClick) // 保存搜索历史
                dismiss()
              }.setNegativeClick {
                dismiss()
              }.show()
            }
          }
        }.setOnLinkIngClick {
          // 点击已关联的图标
          ChooseDialog.Builder(
            this@initRecyclerView,
            ChooseDialog.DataImpl(
              content = "确定要取消关联吗？",
              width = 255,
              height = 146
            )
          ).setPositiveClick {
            mViewModel.deleteLinkStudent()
            dismiss()
          }.setNegativeClick {
            dismiss()
          }.show()
        }
    }
    
    private fun ShowResultActivity.initObserve() {
      mViewModel.changeLinkResult.collectLaunch {
        if (it != null) {
          toast("关联成功！")
          val newData = data.copy(linkStudent = it)
          mPersonData = newData // 修改 intent，防止 Activity 重建后数据不变
          data = newData
          mRvAdapter.submitList(
            newData.studentList.map { bean ->
              ShowStuResultRvAdapter
                .FindStuWithLinkBean(bean, bean.stuNum == newData.linkStudent?.linkNum)
            }
          )
        }
      }
      
      mViewModel.deleteLinkResult.collectLaunch {
        if (it) {
          toast("已取消关联")
          val newData = data.copy(linkStudent = null)
          mPersonData = newData // 修改 intent，防止 Activity 重建后数据不变
          data = newData
          mRvAdapter.submitList(
            newData.studentList.map { bean ->
              ShowStuResultRvAdapter
                .FindStuWithLinkBean(bean, bean.stuNum == newData.linkStudent?.linkNum)
            }
          )
        } else {
          toast("取消关联失败")
        }
      }
    }
  }
  
  private class TeacherHandler(
    val data: TeaData,
    activity: ShowResultActivity,
  ) : PersonHandler(activity) {
    override fun ShowResultActivity.initialize() {
      mTvTitle.text = "老师课表"
      initRecyclerView()
    }
    
    private fun ShowResultActivity.initRecyclerView() {
      mRvResult.adapter = ShowTeaResultRvAdapter(data.teacherList)
        .setOnItemClick {
          mViewModel.saveHistory(this) // 保存搜索历史
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
          FindTeaCourseFragment.tryReplaceOrFresh(supportFragmentManager, mBottomSheetView.id, num)
        }
    }
  }
}