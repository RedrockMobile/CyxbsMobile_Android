package com.mredrock.cyxbs.mine.page.sign

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.SP_SIGN_REMIND
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.daily.PointDetailActivity
import com.mredrock.cyxbs.mine.page.daily.PointSpecActivity
import com.mredrock.cyxbs.mine.page.daily.SignRulesActivity
import com.mredrock.cyxbs.mine.util.user
import kotlinx.android.synthetic.main.mine_activity_daily_sign.*
import kotlinx.android.synthetic.main.mine_popup_window_sign_menu.view.*
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by zzzia on 2018/8/14.
 * origin by jay86
 * 每日签到
 */
class DailySignActivity(override val viewModelClass: Class<DailyViewModel> = DailyViewModel::class.java
                        , override val isFragmentActivity: Boolean = false)
    : BaseViewModelActivity<DailyViewModel>() {

    private val menuContentView: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(menuContentView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_daily_sign)

        common_toolbar.init("每日签到")

        initView()

        //加载信息
        viewModel.loadAllData(user!!)

        //连续签到每日提醒设置
        mine_daily_remindSwitch.setCheckedImmediately(defaultSharedPreferences.getBoolean(SP_SIGN_REMIND, false))
        mine_daily_remindSwitch.setOnCheckedChangeListener { _, isChecked ->
            remindStatusChange(isChecked)
        }

        //积分明细
        mine_daily_pointDetail.setOnClickListener {
            startActivity<PointDetailActivity>()
        }
    }

    private fun initView() {
        viewModel.status.observe(this, Observer {
            it!!
            freshSignView(it.serialDays, it.isChecked)
            mine_daily_pointCount.text = it.integral.toString()
        })
        if (!isLogin()) return
        loadAvatar(user!!.photoSrc, mine_daily_avatar)
    }

    /**
     * 刷新签到页面
     */
    @SuppressLint("SetTextI18n")
    private fun freshSignView(serialDays: Int, isSign: Boolean) {

        if (isSign) {
            mine_daily_signedFrame.visible()
            mine_daily_toSignFrame.gone()
        } else {
            mine_daily_signedFrame.gone()
            mine_daily_toSignFrame.visible()
            mine_daily_sign.setOnClickListener { checkIn() }
        }

        mine_daily_dayCount.text = "${if (serialDays < 10) "0" else ""}$serialDays"

        val checkedDrawable = ContextCompat.getDrawable(this, R.drawable.mine_shape_circle_src_activity_sign)
        val todayColor = ContextCompat.getColor(this, R.color.mine_sign_today_name)
        val otherDayColor = ContextCompat.getColor(this, R.color.mine_sign_day_name)

        var position = 0
        val weekDay = serialDays % 7
        val today = if (isSign) weekDay else weekDay + 1//应该着色的位置
        DayGenerator(serialDays, isSign).datas.forEach {
            val dayName = mine_daily_dayNameContainer.getChildAt(position) as TextView
            val dayPoint = mine_daily_dayPointContainer.getChildAt(position) as TextView

            if (it.ischecked) {
                (mine_daily_daySymbolContainer.getChildAt(position * 2 + 1) as ImageView).setImageDrawable(checkedDrawable)
            }

            dayName.text = "第${it.day}天"
            dayPoint.text = "+${it.money}"
            if (it.day == today) {
                dayName.setTextColor(todayColor)
                dayPoint.setTextColor(todayColor)
            } else {
                dayName.setTextColor(otherDayColor)
                dayPoint.setTextColor(otherDayColor)
            }
            position++
        }
    }

    private fun checkIn() {
        if (!isLogin()) return
        viewModel.checkIn(user!!) { viewModel.loadAllData(user!!) }
    }

    private val workName = "sign"
    private fun remindStatusChange(isChecked: Boolean) {
        defaultSharedPreferences.editor {
            putBoolean(SP_SIGN_REMIND, isChecked)
            if (isChecked){
                toast("将会在每天早晨通知您哦~")
                val request = PeriodicWorkRequest
                        .Builder(SignWorker::class.java, 15, TimeUnit.MINUTES)
                        .build()
                WorkManager.getInstance().enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.KEEP, request)
            }else{
                WorkManager.getInstance().cancelUniqueWork(workName)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mine_activity_question_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mine_menu_more) {
            popupWindow.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMenuContentView(): View {
        val root = layoutInflater.inflate(R.layout.mine_popup_window_sign_menu, null, false)
        root.mine_popup_menu_rules.setOnClickListener {
            startActivity<SignRulesActivity>()
            popupWindow.dismiss()
        }
        root.mine_popup_menu_spec.setOnClickListener {
            startActivity<PointSpecActivity>()
            popupWindow.dismiss()
        }
        return root
    }

    private fun PopupWindow.init(): PopupWindow {
        isTouchable = true
        isOutsideTouchable = true
        animationStyle = R.style.mine_PopupAnimation
        setOnDismissListener { mine_daily_frame.gone() }
        return this
    }

    private fun PopupWindow.show() {
        showAtLocation(common_toolbar, Gravity.END or Gravity.TOP, 0, common_toolbar.height)
        mine_daily_frame.visible()
    }

    private fun isLogin(): Boolean {
        if (!BaseApp.isLogin) {
            toast("还没有登录...")
        }
        return BaseApp.isLogin
    }
}
