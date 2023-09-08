package com.mredrock.cyxbs.affair.ui.fragment.utils

import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.widge.TextViewTransition
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.visible

class NoClassPageManager(val fragment: BaseFragment){

    private fun <T : View> Int.view() = fragment.run { view<T>() }

    private val mRootView: ConstraintLayout by R.id.affair_root_no_class_affair.view()   //根布局
    private val mTvText2: TextView by R.id.affair_tv_no_class_affair_text_2.view()   //为你的行程添加
    private val mTvText3: TextView by R.id.affair_tv_no_class_affair_text_3.view()   //一个标题
    private val mRvTitleCandidate by R.id.affair_rv_no_class_affair_title_candidate.view<RecyclerView>()   //热词名单
    private val mRvHotLoc by R.id.affair_rv_no_class_affair_hot_loc.view<RecyclerView>()  //常用地点
    private val mTvLeftSpare by R.id.affair_tv_no_class_affair_spare_stu.view<TextView>()   //选择空闲成员
    private val mTvRightAll by R.id.affair_tv_no_class_affair_all_stu.view<TextView>()   //选择所有人员

    private var mAffairPage: AffairPage = AffairPage.ADD_TITLE

    companion object{
        const val TITLE = 0
        const val LOCATION = 1
        const val SEND = 2
    }

    /**
     * 加载下一页
     * @return 返回剩余页数
     */
    fun loadNextPage(): Int {
        // 如果已经到最后一页并且点击发送通知了，那么就不拦截了。此时返回就直接返回了
        if (mAffairPage.ordinal == AffairPage.values().size - 1) return -1
        mAffairPage = mAffairPage.next(this)
        return AffairPage.values().size - mAffairPage.ordinal - 1
    }

    /**
     * 回到上一页
     * @return 返回剩余页数
     */
    fun loadLastPage(): Int {
        if (mAffairPage.ordinal == TITLE) return -1
        mAffairPage = mAffairPage.last(this)
        return mAffairPage.ordinal
    }

    /**
     * @return 返回当前页数，从 0 开始
     */
    fun getCurrentPage(): Int {
        return mAffairPage.ordinal
    }

    /**
     * @return 是否是最后一页,此时为发送通知的那一页
     */
    fun isEndPage(): Boolean {
        return getCurrentPage() == AffairPage.values().size - 1
    }

    /**
     * @return 是否是第一页
     */
    fun isStartPage(): Boolean {
        return getCurrentPage() == 0
    }

    /**
     * @return 是否是选择地点那一页
     */
    fun isChooseLoc() : Boolean{
        return getCurrentPage() == LOCATION
    }

    // 拦截返回键
    private val mOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (loadLastPage() == -1) {
                isEnabled = false
            }
        }
    }

    init {
        @Suppress("DEPRECATION")
        fragment.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    // 拦截返回建
                    fragment.requireActivity().onBackPressedDispatcher.addCallback(
                        owner,
                        mOnBackPressedCallback
                    )
                    owner.lifecycle.removeObserver(this)
                }
            }
        )
    }

    private var mTitle = ""
    private var mLoc = ""
    // 在发送通知返回，更改背景
    private var mClickLast : (()->Unit)? = null

    fun setTitle(title : String){
        this.mTitle = title
    }
    fun setLoc(loc : String){
        this.mLoc = loc
    }

    fun getTitle() : String{
        return mTitle
    }

    fun getLoc() : String{
        return mLoc
    }

    fun setClickLast(mClickLast : (()->Unit)){
        this.mClickLast = mClickLast
    }

    //事务界面，几个页面就写几个枚举类(在原有fragment之上改变的)
    private enum class AffairPage {
        ADD_TITLE {
            override fun NoClassPageManager.nextInterval(): Boolean {
                if (mTitle.isNotBlank()) {
                    TransitionManager.beginDelayedTransition(mRootView, createTransition())
                    mTvText3.text = "选择地点"
                    mRvTitleCandidate.gone()
                    mRvHotLoc.visible()
                    mTvText2.invisible()
                    mOnBackPressedCallback.isEnabled = true
                    return true
                }
                toast("  掌友，标题不能为空哟！")
                return false
            }

            override fun NoClassPageManager.lastInterval(): Boolean{
                fragment.requireActivity().finishAfterTransition()
                return false
            }
        },
        ADD_LOCATION {
            override fun NoClassPageManager.nextInterval(): Boolean {
                TransitionManager.beginDelayedTransition(mRootView, createTransition())
                mTvLeftSpare.visible()
                mTvRightAll.visible()
                mRvHotLoc.gone()
                mTvText3.text = "选择通知人员"
                return true
            }

            override fun NoClassPageManager.lastInterval(): Boolean {
                TransitionManager.beginDelayedTransition(mRootView, createTransition())
                mTvText3.text = "一个标题"
                mLoc = ""
                mRvTitleCandidate.visible()
                mRvHotLoc.gone()
                mTvText2.visible()
                mOnBackPressedCallback.isEnabled = false
                return true
            }
        },
        //选择通知人员
        ADD_PEOPLE {
            override fun NoClassPageManager.nextInterval(): Boolean {
                // 进行网络请求
                return false
            }

            override fun NoClassPageManager.lastInterval(): Boolean {
                TransitionManager.beginDelayedTransition(mRootView, createTransition())
                mTvText2.visible()
                mTvText3.text = "选择地点"
                mTvLeftSpare.invisible()
                mTvRightAll.invisible()
                mRvHotLoc.visible()
                // 回调，更改activity中的按钮的bg
                mClickLast?.invoke()
                return true
            }
        };

        // 进行下一页
        fun next(manager: NoClassPageManager): AffairPage {
            if (manager.run { nextInterval() }) {
                // 当返回 true 时表示下一页成功
                return values()[ordinal + 1]
            }
            return this
        }

        fun last(manager: NoClassPageManager): AffairPage {
            if (manager.run { lastInterval() }) {
                return values()[ordinal - 1]
            }
            return this
        }

        abstract fun NoClassPageManager.nextInterval(): Boolean
        abstract fun NoClassPageManager.lastInterval(): Boolean

        fun createTransition(): TransitionSet {
            return TransitionSet().apply {
                addTransition(Fade())
                addTransition(Slide().apply { slideEdge = Gravity.END })
                addTransition(ChangeBounds())
                addTransition(TextViewTransition())
                duration = 300
            }
        }
    }
}