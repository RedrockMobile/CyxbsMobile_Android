package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.LOGIN_ACTIVITY
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import kotlinx.android.synthetic.main.course_fragment_none_login.*

/**
 * Created by anriku on 2018/10/16.
 */
class NoneLoginFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_fragment_none_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv.setOnClickListener {
            ARouter.getInstance().build(LOGIN_ACTIVITY).navigation(activity)
        }
        tv.setOnClickListener {
            ARouter.getInstance().build(LOGIN_ACTIVITY).navigation(activity)
        }
    }

}