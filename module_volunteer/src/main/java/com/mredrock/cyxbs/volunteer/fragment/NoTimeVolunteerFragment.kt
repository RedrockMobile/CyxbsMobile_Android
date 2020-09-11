package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R

class NoTimeVolunteerFragment : BaseFragment() {

    internal var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.volunteer_fragment_volunteer_notime, container, false)
        return view
    }

}