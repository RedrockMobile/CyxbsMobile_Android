package com.mredrock.cyxbs.course.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.event.MainVPChangeEvent
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.TabIsFoldEvent
import com.mredrock.cyxbs.course.event.WeekNumEvent
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import kotlinx.android.synthetic.main.course_fragment_course_entry.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by anriku on 2018/10/16.
 */
@Route(path = COURSE_ENTRY)
class CourseEntryFragment : BaseFragment() {

    // 表示是否TabLayout折叠
    private var mIsFold = true
    private var mToolbarIc: Array<Drawable?> = arrayOf(null, null)

    // 用于记录当前Toolbar要显示的字符串
    private lateinit var mToolbarTitle: String

    // 用于通知更新Toolbar的Menu更新
    private var insideFragment: Fragment? = null

    // 用于检测用户登陆状态是否改变，用于刷新
    private var isChanged = false
    private var curIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_fragment_course_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCourseEntryFragment()
    }

    private fun initCourseEntryFragment() {
        activity ?: return

        // 如果用户登录了就显示CourseContainerFragment；如果用户没有登录就行显示NoneLoginFragment进行登录
        if (BaseApp.isLogin) {
            // 获取向上向下的图标
            mToolbarIc = arrayOf(ContextCompat.getDrawable(activity!!, R.drawable.course_ic_course_fold),
                    ContextCompat.getDrawable(activity!!, R.drawable.course_ic_course_expand))

            mToolbarTitle = activity!!.getString(R.string.course_all_week)
            replaceFragment(CourseContainerFragment())
        } else {
            mToolbarTitle = activity!!.getString(R.string.common_course)
            replaceFragment(NoneLoginFragment())
        }
        setToolbar()
    }

    /**
     * 注：在某些机型（测试机为api22）会出现EventBus通知了事件变化
     * 但是实际内包裹的fragment并没有被替换，
     * 发现在EventBus通知的方法调用replace方法后并没有对应调用fragment的生命周期
     * 暂时没有找到导致的原因，
     * 之前使用的setUserVisibleHint，但是在用户从该fragment进入Activity后再返回不会刷新状态
     * 然后更换了一下判断方式
     *
     * by Cchanges at 8.21
     */
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        if (isVisibleToUser && isChanged) {
//            initCourseEntryFragment()
//            isChanged = false
//        }
//        super.setUserVisibleHint(isVisibleToUser)
//    }

    override fun onResume() {
        super.onResume()
        if (isVisible && isChanged) {
            initCourseEntryFragment()
            isChanged = false
        }
    }

    /**
     * 用于对Toolbar进行一些设置。如果用户登录了，就增加折叠图标，并添加相应的点击事件。
     * 如果用户没登录，就取消折叠图标以及取消点击事件。
     */
    private fun setToolbar() {
        
        // 设置当前Toolbar的内容
        if (curIndex == 0 || userVisibleHint) course_tv_which_week.text = mToolbarTitle

        if (BaseApp.isLogin && curIndex == 0) {
            course_tv_which_week.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    if (mIsFold) mToolbarIc[1] else mToolbarIc[0], null)

            course_tv_which_week.setOnClickListener {
                course_tv_which_week.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        if (mIsFold) mToolbarIc[0] else mToolbarIc[1], null)
                mIsFold = !mIsFold
                EventBus.getDefault().post(TabIsFoldEvent(mIsFold))
            }
        } else {
            course_tv_which_week.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    null, null)
            course_tv_which_week.setOnClickListener(null)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fl, fragment)
        insideFragment = fragment
        transaction.commit()
    }

    /**
     * 在用于的登录状态发生改变后进行调用。
     *
     * @param event 包含当前用户登录状态的事件。
     */
    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        isChanged = true
        if (event.newState) {
           // replaceFragment(CourseContainerFragment())
            mToolbarTitle = activity!!.getString(R.string.course_all_week)
        } else {
           // replaceFragment(NoneLoginFragment())
            mToolbarTitle = activity!!.getString(R.string.common_course)
            Thread {
                ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java).clearCache()
            }.start()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        insideFragment?.onPrepareOptionsMenu(menu)
    }


    /**
     * 在[CourseContainerFragment]中的ViewPager的Page发生了变化后进行接收对应的事件来对[mToolbar]的标题进行
     * 修改
     *
     * @param weekNumEvent 包含当前周字符串的事件。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getWeekNumFromFragment(weekNumEvent: WeekNumEvent) {
        if (weekNumEvent.isOthers) {
            return
        }
        mToolbarTitle = weekNumEvent.weekString
        setToolbar()
    }
}