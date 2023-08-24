package com.mredrock.cyxbs.affair.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.adapter.TitleCandidateAdapter
import com.mredrock.cyxbs.affair.ui.fragment.utils.NoClassPageManager
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.affair.ui.viewmodel.fragment.NoClassAffairViewModel
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.api.affair.NotificationBean
import com.mredrock.cyxbs.lib.base.ui.BaseFragment


//没课约的安排日程
class NoClassAffairFragment : BaseFragment(R.layout.affair_fragment_noclass_affair) {

    companion object {
        fun newInstance(noClassBean: NoClassBean): NoClassAffairFragment {
            Log.d("lx", "newInstance:${noClassBean} ")
            return NoClassAffairFragment().apply {
                arguments = bundleOf(this::mNoClassBean.name to noClassBean)
            }
        }
    }

    private val mNoClassBean by arguments<NoClassBean>()
    private val mViewModel by viewModels<NoClassAffairViewModel>()
    private val mActivityViewModel by activityViewModels<AffairViewModel>()

    private val mTvLeftSpare by R.id.affair_tv_no_class_affair_spare_stu.view<TextView>()   //选择空闲成员
    private val mTvRightAll by R.id.affair_tv_no_class_affair_all_stu.view<TextView>()   //选择所有人员

    private val mRvTitleCandidate by R.id.affair_rv_no_class_affair_title_candidate.view<RecyclerView>()  //候选热词
    private val mRvHotLoc by R.id.affair_rv_no_class_affair_hot_loc.view<RecyclerView>()  //候选热词

    private val mRvTitleCandidateAdapter = TitleCandidateAdapter()
    private val mRvHotLocAdapter = TitleCandidateAdapter()
    private val mPageManager = NoClassPageManager(this)

    /**
     * 需要发送通知的
     */
    private var mWaitSubmit : ArrayList<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lx", "noclassfragment:${mNoClassBean}: ")
        initRv()
        initObserve()
        initClickChoose()
    }

    /**
     * 点击空闲人员和全体人员之后的操作
     */
    private fun initClickChoose() {
        //所有空闲人员
        val spareList = mNoClassBean.mStuList.filter { it.second }.map { it.first }
        Log.d("lx", "sparedpeople: $spareList")
        //全体人员
        val allList = mNoClassBean.mStuList.map { it.first }
        Log.d("lx", "allpeople: $allList")
        // 点击空闲人员，右边的选择所有要白底黑字，空闲人员要白字紫底
        mTvLeftSpare.setOnClickListener {
            mWaitSubmit = ArrayList()
            mWaitSubmit!!.addAll(spareList)
            mTvLeftSpare.apply{
                setBackgroundResource(R.drawable.affair_shape_affair_modify_button_background)
                setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_white_black))
            }
            mTvRightAll.apply {
                setBackgroundResource(R.drawable.affair_shape_button_default_bg)
                setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_black_white))
            }
        }
        mTvRightAll.setOnClickListener {
            mWaitSubmit = ArrayList()
            mWaitSubmit!!.addAll(allList)
            mTvLeftSpare.apply{
                setBackgroundResource(R.drawable.affair_shape_button_default_bg)
                setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_black_white))
            }
            mTvRightAll.apply {
                setBackgroundResource(R.drawable.affair_shape_affair_modify_button_background)
                setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_white_black))
            }
        }
    }

    private fun initRv() {
        // 仅对本例生效
        fun setDefault(recyclerView: RecyclerView, lastIndex: Int){
            val parentView = recyclerView.getChildAt(lastIndex) as ViewGroup
            // 获得索引为0的时候
            val textView = parentView.getChildAt(0) as TextView
            textView.setBackgroundResource(R.drawable.affair_shape_button_default_bg)
            textView.setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_black_white))
        }
        fun setSelect(recyclerView: RecyclerView,currentIndex : Int){
            // 找到当前选中的child
            val parentView = recyclerView.getChildAt(currentIndex) as ViewGroup
            // 获得索引为0的时候
            val textView = parentView.getChildAt(0) as TextView
            textView.setBackgroundResource(R.drawable.affair_shape_affair_modify_button_background)
            textView.setTextColor(resources.getColor(com.mredrock.cyxbs.config.R.color.config_white_black))
        }
        // 上一次选中的索引
        var lastIndex : Int? = null
        mRvTitleCandidate.apply {
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
            adapter = mRvTitleCandidateAdapter.apply {
                setClickListener {
                    // 记录下来
                    mPageManager.setTitle(it.toString())
                    // 取消上一次选中的
                    if (lastIndex != null){
                        setDefault(mRvTitleCandidate,lastIndex!!)
                    }
                    // 找到当前选中的index
                    val chooseIndex = mRvTitleCandidateAdapter.currentList.indexOf(it)
                    setSelect(mRvTitleCandidate,chooseIndex)
                    lastIndex = chooseIndex
                }
            }
        }
        // 上一次选中的索引
        var hotLocLastIndex : Int? = null
        mRvHotLoc.apply {
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
            adapter = mRvHotLocAdapter.apply {
                setClickListener {
                    // 记录下来
                    mPageManager.setLoc(it.toString())
                    // 取消上一次选中的
                    if (hotLocLastIndex != null){
                        setDefault(mRvHotLoc,hotLocLastIndex!!)
                    }
                    // 找到当前选中的index
                    val chooseIndex = mRvHotLocAdapter.currentList.indexOf(it)
                    setSelect(mRvHotLoc,chooseIndex)
                    hotLocLastIndex = chooseIndex
                }
            }
        }
    }

    private fun initObserve() {
        mViewModel.titleCandidates.observe {
            mRvTitleCandidateAdapter.submitList(it)
        }
        mViewModel.hotLocation.observe{
            mRvHotLocAdapter.submitList(it)
        }

        mActivityViewModel.clickAffect.collectLaunch {
            // 如果是标题页，点击下一页就请求地点，然后加载到下一页上
            if (mPageManager.isStartPage()){
                mViewModel.getHotLoc()
                mPageManager.loadNextPage()
            }else if (mPageManager.isChooseLoc()){
                //如果是选择地点界面，那么这次点击相当于进入通知界面
                mPageManager.loadNextPage()
            }else if (mPageManager.isEndPage()) {
                // 如果是发送通知界面，这次点击相当于发送通知
                Log.d("lx", "mWaitSubmit = ${mWaitSubmit}: ")
                if (mWaitSubmit != null && mWaitSubmit!!.isNotEmpty()){
                    val notificationBean = NotificationBean(mWaitSubmit!!,mNoClassBean.dateJson,mPageManager.getTitle(),mPageManager.getLoc())
                    Log.d("lx", "notificationBean = ${notificationBean}: ")
                    mViewModel.sendNotification(notificationBean)
                }else{
                    toast("掌友，人员不能为空哦")
                }
            }
        }

        mActivityViewModel.clickBack.collectLaunch {
            if (mPageManager.isStartPage()){
                requireActivity().finishAfterTransition()
            }else{
                mPageManager.loadLastPage()
            }
        }

        mViewModel.notification.observe{
            if (it.isSuccess()){
                if (it.data == "limit"){
                    toast("一天只能发送五次哦~")
                }else{
                    toast("发送成功~")
                }
            }else{
                toast("发送失败!")
            }
        }
    }

}