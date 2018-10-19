package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.event.MainVPChangeEvent
import com.mredrock.cyxbs.course.event.TabIsFoldEvent
import com.mredrock.cyxbs.course.event.WeekNumEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.arouter.ICourseEntry
import com.mredrock.cyxbs.course.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by anriku on 2018/10/16.
 */
@Route(path = COURSE_ENTRY)
class CourseEntryFragment : BaseFragment(), ICourseEntry {

    // 表示是否TabLayout折叠
    private var mIsFold = true
    private lateinit var mToolbarIc: Array<Drawable?>

    private lateinit var mToolbar: JToolbar
    // 用于记录当前Toolbar要显示的字符串
    private lateinit var mToolbarTitle: String

    override fun init(context: Context?) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_fragment_course_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCourseEntryFragment()
    }

    private fun initCourseEntryFragment() {
        activity ?: return

        mToolbar = (activity as BaseActivity).common_toolbar

        // 如果用户登录了就显示CourseContainerFragment；如果用户没有登录就行显示NoneLoginFragment进行登录
        if (BaseApp.isLogin) {
            // 获取向上向下的图标
            mToolbarIc = arrayOf(ContextCompat.getDrawable(activity!!, R.drawable.common_ic_course_fold),
                    ContextCompat.getDrawable(activity!!, R.drawable.common_ic_course_expand))

            mToolbarTitle = activity!!.getString(R.string.course_all_week)
            replaceFragment(CourseContainerFragment())
        } else {
            mToolbarTitle = activity!!.getString(R.string.common_course)
            replaceFragment(NoneLoginFragment())
        }
        setToolbar()
    }

    /**
     * 用于对Toolbar进行一些设置。如果用户登录了，就增加折叠图标，并添加相应的点击事件。
     * 如果用户没登录，就取消折叠图标以及取消点击事件。
     */
    private fun setToolbar() {
        val toolbar = mToolbar

        if (BaseApp.isLogin) {
            if (mIsFold) {
                toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        mToolbarIc[1], null)
            } else {
                toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        mToolbarIc[0], null)
            }

            toolbar.titleTextView.setOnClickListener {
                mIsFold = if (mIsFold) {
                    toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            mToolbarIc[0], null)
                    false
                } else {
                    toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            mToolbarIc[1], null)
                    true
                }
                EventBus.getDefault().post(TabIsFoldEvent(mIsFold))
            }
        } else {
            toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    null, null)
            toolbar.titleTextView.setOnClickListener(null)
        }

        // 设置当前Toolbar的内容
        toolbar.titleTextView.text = mToolbarTitle
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl, fragment)
        transaction.commit()
    }

    /**
     * 在用于的登录状态发生改变后进行调用。
     *
     * @param event 包含当前用户登录状态的事件。
     */
    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        if (event.newState) {
            replaceFragment(CourseContainerFragment())
        } else {
            replaceFragment(NoneLoginFragment())
        }
        setToolbar()
    }

    /**
     * 当main模块的ViewPager的page改变后，发送此事件。如果此Page是课表的Page就对Toolbar进行相应的设置。
     *
     * @param mainVPChangeEvent [MainVPChangeEvent]
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainVPChangeEvent(mainVPChangeEvent: MainVPChangeEvent) {
        if (mainVPChangeEvent.index == 0) {
            setToolbar()
        } else {
            val toolbar = mToolbar

            toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    null, null)
            toolbar.titleTextView.setOnClickListener(null)
        }
    }

    /**
     * 在[CourseContainerFragment]中的ViewPager的Page发生了变化后进行接收对应的事件来对[mToolbar]的标题进行
     * 修改
     *
     * @param weekNumEvent 包含当前周字符串的事件。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getWeekNumFromFragment(weekNumEvent: WeekNumEvent) {
        mToolbarTitle = weekNumEvent.weekString
        setToolbar()
    }
}