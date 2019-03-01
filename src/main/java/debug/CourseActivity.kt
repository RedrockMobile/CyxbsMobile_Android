package debug

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.TabIsFoldEvent
import com.mredrock.cyxbs.course.event.WeekNumEvent
import com.mredrock.cyxbs.course.ui.CourseContainerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 这个Activity用于进行Debug的Activity
 */

class CourseActivity : BaseActivity() {

    override val isFragmentActivity: Boolean
        get() = true

    private var mIsFold = true
    private val mToolbarIc: Array<Drawable?> by lazy {
        arrayOf(ContextCompat.getDrawable(this, R.drawable.course_ic_course_fold),
                ContextCompat.getDrawable(this, R.drawable.course_ic_course_expand))
    }
    private val mLoginDialog: LoginDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoginDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_course)
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)

        initActivity()

        analogLogin()

        replaceFragment(CourseContainerFragment())
    }

    /**
     * 此方法用于单模块测试模拟登陆
     */
    private fun analogLogin() {
        if (!BaseApp.isLogin) {
            mLoginDialog.analogLogin()
        }
    }

    private fun initActivity() {
        val toolbar = common_toolbar.apply {
            init("整学期", listener = null)
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.course_ic_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                mToolbarIc[1], null)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mLoginDialog.analogLogin()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getWeekNumFromFragment(weekNumEvent: WeekNumEvent) {
        common_toolbar.titleTextView.text = weekNumEvent.weekString
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_course_fragment, fragment)
        transaction.commit()
    }
}
