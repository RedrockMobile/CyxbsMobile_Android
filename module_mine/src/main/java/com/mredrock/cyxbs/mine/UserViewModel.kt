package com.mredrock.cyxbs.mine

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp.Companion.appContext
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.extensions.launchCatch
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.mine.network.model.ItineraryMsgBean
import com.mredrock.cyxbs.mine.network.model.QANumber
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.network.model.UfieldMsgBean
import com.mredrock.cyxbs.mine.network.model.UserCount
import com.mredrock.cyxbs.mine.network.model.UserUncheckCount
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


/**
 * Created by zia on 2018/8/26.
 */
class UserViewModel : BaseViewModel() {

    companion object {
        const val UNCHECK_PRAISE_KEY = "mine/uncheck_praise"
        const val UNCHECK_COMMENT_KEY = "mine/uncheck_comment"
    }

    private val _status = MutableLiveData<ScoreStatus>()//签到状态
    val status: LiveData<ScoreStatus>
        get() = _status

    private val _qaNumber = MutableLiveData<QANumber>()
    val qaNumber: LiveData<QANumber>
        get() = _qaNumber

    private val _userCount = MutableLiveData<UserCount?>()
    val userCount: LiveData<UserCount?>
        get() = _userCount

    private val _userUncheckCount = MutableLiveData<UserUncheckCount?>()
    val userUncheckCount: LiveData<UserUncheckCount?>
        get() = _userUncheckCount

    /**
     * ”新“通知消息（状态为未读的）的数量
     */
    val newNotificationCount: LiveData<Int> get() = _newNotificationCount
    private val _newNotificationCount = MutableLiveData<Int>()



    init {
        getNewNotificationCount()
    }

    /**
     * 用携程异步获取未读的notification数量
     */
    fun getNewNotificationCount() {
        viewModelScope.launchCatch {
            val uFieldActivityList = async(Dispatchers.IO) { apiService.getUFieldActivityList() }
            val itineraryList = listOf(
                async(Dispatchers.IO) { apiService.getSentItinerary() },
                async(Dispatchers.IO) { apiService.getReceivedItinerary() }
            )
            val newUFieldActivityCount = async(Dispatchers.Default) {
                getNewActivityCount(uFieldActivityList.await())
            }
            val newItineraryCount = async(Dispatchers.Default) {
                getNewItineraryCount(itineraryList.awaitAll())
            }
            _newNotificationCount.value = (newUFieldActivityCount.await() + newItineraryCount.await())
        }.catch {
            it.printStackTrace()
//            "获取最新消息失败,请检查网络连接".toast()
        }
    }

    /**
     * 获取“新”活动通知的数量
     * @param response
     */
    private fun getNewActivityCount(response: ApiWrapper<List<UfieldMsgBean>>) : Int{
        return if (response.isSuccess()) {
            val list = response.data.filter { !it.clicked }
            list.size
        } else
            0
    }

    /**
     * 获取“新”行程通知的数量
     * @param response
     */
    private fun getNewItineraryCount(response: List<ApiWrapper<List<ItineraryMsgBean>?>>): Int{
        val receivedCount: Int = if (response[1].isSuccess() && !response[1].data.isNullOrEmpty()) {
            val list = response[1].data!!.filter { !it.hasRead }
            list.size
        } else 0
        val sentCount: Int = if (response[0].isSuccess() && !response[0].data.isNullOrEmpty()) {
            val list = response[0].data!!.filter { !it.hasRead }
            list.size
        } else 0
        return receivedCount + sentCount
    }


    fun getScoreStatus() {
        apiService.getScoreStatus()
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .unsafeSubscribeBy(
                onNext = {
                    _status.postValue(it)
                }
            )
            .lifeCycle()
    }

    fun getQANumber() {
        apiService.getQANumber()
            .normalWrapper(this)
            .unsafeSubscribeBy(
                onNext = {
                    _qaNumber.postValue(it)
                }
            )
            .lifeCycle()

    }

    //获取用户三大数据的数量
    fun getUserCount() {
        apiService.getUserCount()
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .unsafeSubscribeBy(
                onNext = {
                    _userCount.postValue(it.data)
                },
                onError = {
//                    toast("获取动态等信息异常")
                }
            )
    }

    fun getUserUncheckedPraiseCount() {
        val sp = appContext.defaultSharedPreferences
        val lastCheckTimeStamp = sp.getLong(UNCHECK_PRAISE_KEY, 0L)
        if (lastCheckTimeStamp == 0L) return
        apiService.getUncheckedPraiseCount(lastCheckTimeStamp)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .unsafeSubscribeBy(
                onNext = {
                    _userUncheckCount.postValue(it.data)
                },
                onError = {
//                    appContext.toast("获取评论等信息异常")
                }
            )
    }

    fun getUserUncheckedCommentCount() {
        val sp = appContext.defaultSharedPreferences
        val lastCheckTimeStamp = sp.getLong(UNCHECK_COMMENT_KEY, 0L)
        if (lastCheckTimeStamp == 0L) return
        apiService.getUncheckedCommentCount(lastCheckTimeStamp)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .unsafeSubscribeBy(
                onNext = {
                    _userUncheckCount.postValue(it.data)
                },
                onError = {
//                    appContext.toast("获取评论等信息异常")
                }
            )
    }

    //思考了一下，这里view的引用应该会随着函数调用的结束出栈，所以不会引起内存泄漏
    fun judgeChangedAndSetText(textView: TextView, count: Int) {
        val text = getNumber(count)
        if (textView.text == text) return
        textView.text = text
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { va ->
            textView.scaleY = va.animatedValue as Float
        }
        animator.start()
    }

    fun setViewWidthAndText(textView: TextView, count: Int) {
        if (count == 0) {
            //如果当前的数值已经归零，就不操作了
            if (textView.text == "0") return
            textView.text = "0"
            //加上一个逐渐变大弹出的动画
            val animator = ValueAnimator.ofFloat(1f, 0f)
            animator.duration = 200
            animator.addUpdateListener { va ->
                textView.scaleX = va.animatedValue as Float
                textView.scaleY = va.animatedValue as Float
            }
            animator.interpolator = DecelerateInterpolator()
            animator.start()
            return
        }
        //如果前后数字没有变化就不进行刷新
        val text = getNumber(count)
        if (textView.text == text) return
        textView.visibility = View.VISIBLE

        //加上一个逐渐变大弹出的动画
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 200
        animator.addUpdateListener { va ->
            textView.scaleX = va.animatedValue as Float
            textView.scaleY = va.animatedValue as Float
        }
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    fun setLeftMargin(textView: TextView, count: Int) {
        var leftMargin = 17
        if (count > 99) {
            leftMargin += 45
        } else {
            leftMargin += if (count % 10 == 1) {
                12
            } else {
                15
            }
            leftMargin += when (count / 10) {
                1 -> {
                    12
                }
                0 -> 0
                else -> 15
            }
        }
        val lp = textView.layoutParams as ConstraintLayout.LayoutParams
        lp.leftMargin = appContext.dip(leftMargin)
        textView.layoutParams = lp
    }

    //转换数字为对应字符
    private fun getNumber(number: Int): String = when {
        number in 0..99 -> number.toString()
        number > 99 -> "99+"
        else -> "0"
    }

    fun saveCheckTimeStamp(type: Int) {
        appContext.defaultSharedPreferences.editor {
            if (type == 1) {//刷新未读回复数的本地记录时间戳
                putLong(UNCHECK_COMMENT_KEY, System.currentTimeMillis() / 1000)
            } else if (type == 2) {//刷新点赞数的本地记录时间戳
                putLong(UNCHECK_PRAISE_KEY, System.currentTimeMillis() / 1000)
            }
            apply()
        }
    }

    /**
     * 清除User的信息，唯一会调用这个方法的时候是在用户登出
     */
    fun clearUser() {
        ServiceManager(IAccountService::class).getVerifyService()
            .logout(appContext)
    }
}