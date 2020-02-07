package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.config.WEEK_NUM
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentNoCourseInviteBinding
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteFragment : Fragment() {

    private lateinit var mBinding: CourseFragmentNoCourseInviteBinding
    private lateinit var mNoCourseInviteViewModel: NoCourseInviteViewModel
    private var mWeekNum = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_no_course_invite, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity ?: return

        initFragment()
    }

    private fun initFragment() {
        mBinding.lifecycleOwner = this

        mWeekNum = arguments?.getInt(WEEK_NUM) ?: 0
        mNoCourseInviteViewModel = (parentFragment as CourseContainerEntryFragment).mNoCourseInviteViewModel!!
        mBinding.nowWeek = mWeekNum
        mBinding.noCourseInviteViewModel = mNoCourseInviteViewModel

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mNoCourseInviteViewModel.getCourses {
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

}